package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.ConditionOperation;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.service.HubRouterClient;

import java.util.List;
import java.util.Map;

import static ru.yandex.practicum.model.ConditionType.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotHandler {
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final HubRouterClient hubRouterClient;

    public void handle(SensorSnapshotAvro sensorsSnapshot) {

        try {
            Map<String, SensorStateAvro> sensorsStateMap = sensorsSnapshot.getSensorState();
            List<Scenario> scenarios = scenarioRepository.findByHubId(sensorsSnapshot.getHubId());

            scenarios.forEach(scenario -> {
                if (checkScenario(sensorsStateMap, scenario)) {
                    executeScenarioActions(scenario);
                }
            });
        } catch (Exception e) {
            log.error("Ошибка обработки снапшота", e);
        }
    }

    private boolean checkScenario(Map<String, SensorStateAvro> sensorsStateMap, Scenario scenario) {
        List<Condition> conditions = conditionRepository.findAllByScenario(scenario);
        return conditions.stream().allMatch(condition -> checkCondition(condition, sensorsStateMap));
    }

    private boolean checkCondition(Condition condition, Map<String, SensorStateAvro> sensorStateMap) {
        String sensorId = condition.getSensor().getId();
        SensorStateAvro sensorState = sensorStateMap.get(sensorId);
        if (sensorState == null) {
            return false;
        }
        if (condition.getType() == MOTION) {
            MotionSensorAvro motionSensor = (MotionSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, motionSensor.getMotion() ? 1 : 0));
        }
        if (condition.getType() == LUMINOSITY) {
            LightSensorAvro lightSensor = (LightSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, lightSensor.getLuminosity()));
        }
        if (condition.getType() == SWITCH) {
            SwitchSensorAvro switchSensor = (SwitchSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, switchSensor.getState() ? 1 : 0));
        }
        if (condition.getType() == TEMPERATURE) {
            ClimateSensorAvro temperatureSensor = (ClimateSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, temperatureSensor.getTemperatureC()));
        }
        if (condition.getType() == CO2LEVEL) {
            ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, climateSensor.getCo2Level()));
        }
        if (condition.getType() == HUMIDITY) {
            ClimateSensorAvro climateSensor = (ClimateSensorAvro) sensorState.getData();
            return Boolean.TRUE.equals(checkOperation(condition, climateSensor.getHumidity()));
        }
        return false;
    }

    private Boolean checkOperation(Condition condition, Integer currentValue) {
        ConditionOperation conditionOperation = condition.getOperation();
        int targetValue = condition.getValue();
        if (conditionOperation.equals(ConditionOperation.EQUALS)) {
            return targetValue == currentValue;
        }
        if (conditionOperation.equals(ConditionOperation.GREATE_THAN)) {
            return currentValue > targetValue;
        }
        if (conditionOperation.equals(ConditionOperation.LOWER_THAN)) {
            return currentValue < targetValue;
        }
        return null;
    }

    private void executeScenarioActions(Scenario scenario) {
        try {
            List<Action> actions = actionRepository.findAllByScenario(scenario);
            actions.forEach(action -> {
                try {
                    hubRouterClient.sendAction(action);
                } catch (Exception e) {
                }
            });
        } catch (Exception e) {
            log.error("Ошибка получения действий для сценария {}", scenario.getName(), e);
        }
    }
}
