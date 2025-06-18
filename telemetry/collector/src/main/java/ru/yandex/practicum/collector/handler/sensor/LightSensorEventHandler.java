package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BasicSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorAvro MapToAvro(SensorEventProto sensorEvent) {
        LightSensorEvent lightSensorEvent = sensorEvent.getLightSensorEvent();
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }
}
