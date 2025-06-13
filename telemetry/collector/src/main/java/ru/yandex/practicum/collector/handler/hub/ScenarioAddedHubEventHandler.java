package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.*;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class ScenarioAddedHubEventHandler extends BasicHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHubEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro MapToAvro(BasicHubEvent hubEvent) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setActions(scenarioAddedEvent.getActions().stream().map(this::mapActionToActionAvro).toList())
                .setConditions(scenarioAddedEvent.getConditions().stream().map(this::mapConditionToConditionAvro).toList())
                .build();
    }

    private DeviceActionAvro mapActionToActionAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ScenarioConditionAvro mapConditionToConditionAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(condition.getValue())
                .build();

    }
}
