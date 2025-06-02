package ru.yandex.practicum.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.handler.SensorEventHandler;
import ru.yandex.practicum.collector.model.sensor.BasicSensorEvent;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@RequiredArgsConstructor
public abstract class BasicSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    @Value(value = "${sensorsTopic}")
    private String sensorsTopic;
    private final CollectorKafkaProducer producer;

    protected abstract T MapToAvro(BasicSensorEvent sensorEvent);

    public void handle(BasicSensorEvent event) throws IllegalAccessException {

        T payload = MapToAvro(event);

        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        producer.send(
                sensorsTopic,
                event.getTimestamp(),
                event.getId(),
                sensorEventAvro
        );
    }
}

