package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorEvent;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
public class TemperatureSensorEventHandler extends BasicSensorEventHandler<TemperatureSensorAvro> {

    public TemperatureSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected TemperatureSensorAvro MapToAvro(SensorEventProto sensorEvent) {
        TemperatureSensorEvent temperatureSensorEvent = sensorEvent.getTemperatureSensorEvent();
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .build();
    }

    @Override
    public SensorEventProto.PayloadCase getEventType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }
}
