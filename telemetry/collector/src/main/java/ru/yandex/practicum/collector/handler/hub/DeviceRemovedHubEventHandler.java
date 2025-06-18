package ru.yandex.practicum.collector.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.grpc.telemetry.event.DeviceRemovedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Component
public class DeviceRemovedHubEventHandler extends BasicHubEventHandler<DeviceRemovedEventAvro> {

    public DeviceRemovedHubEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getEventType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }
    @Override
    protected DeviceRemovedEventAvro MapToAvro(HubEventProto hubEvent) {
        DeviceRemovedEventProto deviceRemovedEvent = hubEvent.getDeviceRemoved();

        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }
}
