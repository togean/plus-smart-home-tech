package ru.yandex.practicum.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.handler.HubEventHandler;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BasicHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    @Value(value = "${hubsTopic}")
    private String hubsTopic;

    private final CollectorKafkaProducer producer;

    protected abstract T MapToAvro(HubEventProto hubEvent);

    public void handle(HubEventProto event) throws IllegalArgumentException {

        T payload = MapToAvro(event);

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()))
                .setPayload(payload)
                .build();
        producer.send(
                hubsTopic,
                Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()),
                event.getHubId(),
                hubEventAvro
        );
    }
}
