package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
public class SwitchSensorEventHandler extends BasicSensorEventHandler<SwitchSensorAvro> {

    public SwitchSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected SwitchSensorAvro MapToAvro(SensorEventProto sensorEvent) {
        SwitchSensorEvent switchSensorEvent = sensorEvent.getSwitchSensorEvent();
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
