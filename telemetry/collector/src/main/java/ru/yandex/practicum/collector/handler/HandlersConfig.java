package ru.yandex.practicum.collector.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.collector.model.hub.HubEventType;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Configuration
public class HandlersConfig {
    @Bean
    public Map<SensorEventType, SensorEventHandler> sensorHandlers(List<SensorEventHandler> handlers) {
        return handlers.stream().collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));
    }

    @Bean
    public Map<HubEventType, HubEventHandler> hubHandlers(List<HubEventHandler> handlers) {
        return handlers.stream().collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }
}
