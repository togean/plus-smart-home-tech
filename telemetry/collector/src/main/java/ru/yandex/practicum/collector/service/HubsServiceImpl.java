package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.handler.HubEventHandler;
import ru.yandex.practicum.collector.model.hub.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HubsServiceImpl implements HubsService {

    private final Map<HubEventType, HubEventHandler> hubHandlers;


    @Override
    public void sendHubEvent(BasicHubEvent hubEvent) throws IllegalAccessException {
        log.info("hubHandlers: {}", hubHandlers);
        if (hubHandlers.containsKey(hubEvent.getType())) {
            log.info("Обработка события от хаба]: {}", hubEvent);
            hubHandlers.get(hubEvent.getType()).handle(hubEvent);
        } else {
            log.warn("Ошибка: неизвестный тип события от хаба: {}", hubEvent.getType());
        }
    }
}
