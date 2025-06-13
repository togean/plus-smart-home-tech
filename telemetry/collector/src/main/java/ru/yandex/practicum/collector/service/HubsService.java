package ru.yandex.practicum.collector.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public interface HubsService {
    void sendHubEvent(HubEventProto hubEvent) throws IllegalAccessException;
}
