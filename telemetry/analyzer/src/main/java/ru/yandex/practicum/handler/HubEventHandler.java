package ru.yandex.practicum.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.ActionRepository;
import ru.yandex.practicum.repository.ConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;
import ru.yandex.practicum.repository.SensorRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventHandler {
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioRepository scenarioRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public void handle(HubEventAvro hubEventAvro) {
        String hubId = hubEventAvro.getHubId();
        Object payload = hubEventAvro.getPayload();

        if (payload.getClass() == DeviceAddedEventAvro.class) {
            log.info("Добавляю устройство");
            handleDeviceAddedEvent(hubId, (DeviceAddedEventAvro) payload);

        } else if (payload.getClass() == DeviceRemovedEventAvro.class) {
            log.info("Удаляю устройство");
            handleDeviceRemovedEvent(hubId, (DeviceRemovedEventAvro) payload);

        } else if (payload.getClass() == ScenarioAddedEventAvro.class) {
            log.info("Добавляю сценарий");
            handleScenarioAddedEvent(hubId, (ScenarioAddedEventAvro) payload);

        } else {
            log.info("Удвляю сценарий");
            handleScenarioRemovedEvent(hubId, (ScenarioRemovedEventAvro) payload);
        }
    }

    public void handleDeviceAddedEvent(String hubId, DeviceAddedEventAvro event) {
        String id = event.getId();
        Boolean isSensorExist = sensorRepository.existsByIdInAndHubId(List.of(id), hubId);
        if (!isSensorExist) {
            Sensor sensor = new Sensor();
            sensor.setId(id);
            sensor.setHubId(hubId);
            sensorRepository.save(sensor);
        }
    }

    public void handleDeviceRemovedEvent(String hubId, DeviceRemovedEventAvro event) {
        String id = event.getId();
        sensorRepository.deleteById(id);
    }

    public void handleScenarioAddedEvent(String hubId, ScenarioAddedEventAvro event) {
        Optional<Scenario> scenario = scenarioRepository.findByHubIdAndName(hubId, event.getName());
        try {
            if (scenario.isPresent()) {
                actionRepository.deleteByScenario(scenario.get());
                conditionRepository.deleteByScenario(scenario.get());
                List<String> sensorIdsList = event.getActions().stream().map(DeviceActionAvro::getSensorId).toList();
                List<Sensor> sensorsList = sensorRepository.findAllById(sensorIdsList);
                checkHub(scenario.get(), sensorsList);
                sensorIdsList = event.getConditions().stream().map(ScenarioConditionAvro::getSensorId).toList();
                sensorsList = sensorRepository.findAllById(sensorIdsList);
                addEventCondition(scenario.get(), event, sensorsList);
                addEventAction(scenario.get(), event, sensorsList);
            } else {
                Scenario newScenario = new Scenario();
                newScenario.setHubId(hubId);
                newScenario.setName(event.getName());
                scenarioRepository.save(newScenario);
                List<String> sensorIdsList = event.getActions().stream().map(DeviceActionAvro::getSensorId).toList();
                List<Sensor> sensorsList = sensorRepository.findAllById(sensorIdsList);
                checkHub(newScenario, sensorsList);
                addEventAction(newScenario, event, sensorsList);
                sensorIdsList = event.getConditions().stream().map(ScenarioConditionAvro::getSensorId).toList();
                sensorsList = sensorRepository.findAllById(sensorIdsList);
                addEventCondition(newScenario, event, sensorsList);

            }
        } catch (RuntimeException e) {
            log.info("Ошибка: ошибка при обработке сценария {}", e.getMessage());
        }
    }

    public void handleScenarioRemovedEvent(String hubId, ScenarioRemovedEventAvro event) {
        String scenarioName = event.getName();
        Optional<Scenario> scenarioToBeDeleted = scenarioRepository.findByHubIdAndName(hubId, scenarioName);
        if (scenarioToBeDeleted.isPresent()) {
            Scenario scenario = scenarioToBeDeleted.get();
            conditionRepository.deleteByScenario(scenario);
            actionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
        }
    }

    private void addEventAction(Scenario scenario, ScenarioAddedEventAvro event, List<Sensor> sensorsList) {
        Map<String, Sensor> sensorMap = sensorsList.stream().collect(Collectors.toMap(Sensor::getId, Function.identity()));
        List<Action> actions = event.getActions().stream()
                .map(actionAvro -> mapToAction(sensorMap.get(actionAvro.getSensorId()), scenario, actionAvro))
                .toList();
        actionRepository.saveAll(actions);
    }

    private void addEventCondition(Scenario scenario, ScenarioAddedEventAvro event, List<Sensor> sensorsList) {
        Map<String, Sensor> sensorMap = sensorsList.stream().collect(Collectors.toMap(Sensor::getId, Function.identity()));
        List<Condition> conditions = event.getConditions().stream()
                .map(conditionAvro -> mapToCondition(sensorMap.get(conditionAvro.getSensorId()), scenario, conditionAvro))
                .toList();
        conditionRepository.saveAll(conditions);
    }

    private Condition mapToCondition(Sensor sensor, Scenario scenario, ScenarioConditionAvro conditionAvro) {
        Object value = conditionAvro.getValue();
        String conditionType = conditionAvro.getType().name();
        String conditionOperation = conditionAvro.getOperation().name();

        Condition condition = new Condition();
        condition.setType(ConditionType.valueOf(conditionType));
        condition.setOperation(ConditionOperation.valueOf(conditionOperation));

        if (value.getClass() == Integer.class) {
            condition.setValue((int) value);
        }
        if (value.getClass() == Boolean.class) {
            if (value.equals(Boolean.TRUE)) {
                condition.setValue(1);
            }
            if (value.equals(Boolean.FALSE)) {
                condition.setValue(0);
            }
        }
        condition.setSensor(sensor);
        condition.setScenario(scenario);
        return condition;
    }

    private Action mapToAction(Sensor sensor, Scenario scenario, DeviceActionAvro deviceActionAvro) {
        String actionType = deviceActionAvro.getType().name();
        Action action = new Action();
        action.setSensor(sensor);
        action.setScenario(scenario);
        action.setType(ActionTypeAvro.valueOf(actionType));
        action.setValue(deviceActionAvro.getValue());
        return action;
    }


    private void checkHub(Scenario scenario, List<Sensor> sensors) throws RuntimeException {
        boolean isHubFound = sensors.stream().map(Sensor::getHubId).anyMatch(hubId -> !hubId.equals(scenario.getHubId()));
        if (isHubFound) {
            throw new RuntimeException("Ошибка: хаб с указанным id не найден");
        }
    }
}
