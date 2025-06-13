package ru.yandex.practicum.collector.handler;

import ru.yandex.practicum.collector.model.hub.BasicHubEvent;
import ru.yandex.practicum.collector.model.hub.HubEventType;

public interface HubEventHandler {
    HubEventType getEventType();

    void handle(BasicHubEvent event) throws IllegalAccessException;
}
