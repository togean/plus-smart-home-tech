package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorEvent;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Component
public class MotionSensorEventHandler extends BasicSensorEventHandler<MotionSensorAvro> {

    public MotionSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected MotionSensorAvro MapToAvro(SensorEventProto sensorEvent) {
        MotionSensorEvent motionSensorEvent = sensorEvent.getMotionSensorEvent();
        return MotionSensorAvro.newBuilder()
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.MOTION_SENSOR_EVENT;
    }
}
