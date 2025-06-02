package ru.yandex.practicum.collector.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hub.BasicHubEvent;

@Component
public interface HubsService {
    void sendHubEvent(BasicHubEvent hubEvent) throws IllegalAccessException;
}
