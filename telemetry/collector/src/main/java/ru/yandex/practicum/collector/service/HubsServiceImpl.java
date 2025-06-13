package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.handler.HubEventHandler;
import ru.yandex.practicum.collector.model.hub.*;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubsServiceImpl implements HubsService {

    private final Map<HubEventType, HubEventHandler> hubHandlers;

    @Override
    public void sendHubEvent(HubEventProto hubEvent) throws IllegalAccessException {
        if (hubHandlers.containsKey(hubEvent.getPayloadCase())) {
            log.info("Обработка события от хаба]: {}", hubEvent);
            hubHandlers.get(hubEvent.getPayloadCase()).handle(hubEvent);
        } else {
            log.warn("Ошибка: неизвестный тип события от хаба: {}", hubEvent.getPayloadCase());
        }
    }
}
