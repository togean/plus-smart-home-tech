package ru.yandex.practicum.collector.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.handler.SensorEventHandler;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BasicSensorEventHandler<T extends SpecificRecordBase> implements SensorEventHandler {
    @Value(value = "${sensorsTopic}")

    private String sensorsTopic;
    private final CollectorKafkaProducer producer;

    protected abstract T MapToAvro(SensorEventProto sensorEvent);

    public void handle(SensorEventProto event) throws IllegalArgumentException {

        T payload = MapToAvro(event);

        SensorEventAvro sensorEventAvro = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
        producer.send(
                sensorsTopic,
                Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()),
                event.getId(),
                sensorEventAvro
        );
    }
}

