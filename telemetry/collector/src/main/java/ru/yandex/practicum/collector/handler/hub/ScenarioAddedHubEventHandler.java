package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;
import java.util.List;

@Component
public class ScenarioAddedHubEventHandler extends BasicHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHubEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getEventType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
    @Override
    protected ScenarioAddedEventAvro MapToAvro(HubEventProto hubEvent) {
        ScenarioAddedEventProto scenarioAddedEvent = hubEvent.getScenarioAdded();
        List<DeviceActionAvro> deviceActionAvroList = scenarioAddedEvent.getActionList().stream().map(this::mapActionToActionAvro).toList();
        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEvent.getConditionList().stream().map(this::mapConditionToConditionAvro).toList();

        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setActions(deviceActionAvroList)
                .setConditions(scenarioConditionAvroList)
                .build();
    }

    private DeviceActionAvro mapActionToActionAvro(DeviceActionProto deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(ActionTypeAvro.valueOf(deviceAction.getType().name()))
                .setValue(deviceAction.getValue())
                .build();
    }

    private ScenarioConditionAvro mapConditionToConditionAvro(ScenarioConditionProto condition) {
        Object value = null;
        if(condition.getValueCase()==ScenarioConditionProto.ValueCase.BOOL_VALUE){
            value = condition.getBoolValue();
        }
        if(condition.getValueCase()==ScenarioConditionProto.ValueCase.INT_VALUE){
            value = condition.getIntValue();
        }

        return ScenarioConditionAvro.newBuilder()
                .setSensorId(condition.getSensorId())
                .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                .setValue(value)
                .build();

    }
}
