package ru.yandex.practicum.collector.handler.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.collector.handler.HubEventHandler;
import ru.yandex.practicum.collector.model.hub.BasicHubEvent;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@RequiredArgsConstructor
public abstract class BasicHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {
    @Value(value = "${hubsTopic}")
    private String hubsTopic;

    private final CollectorKafkaProducer producer;

    protected abstract T MapToAvro(BasicHubEvent hubEvent);

    public void handle(BasicHubEvent event) throws IllegalAccessException {

        T payload = MapToAvro(event);

        HubEventAvro hubEventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
        producer.send(
                hubsTopic,
                event.getTimestamp(),
                event.getHubId(),
                hubEventAvro
        );
    }
}
