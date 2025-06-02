package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.BasicHubEvent;
import ru.yandex.practicum.collector.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedHubEventHandler extends BasicHubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemovedHubEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getEventType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    protected DeviceRemovedEventAvro MapToAvro(BasicHubEvent hubEvent) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;

        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }
}
