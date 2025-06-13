package ru.yandex.practicum.collector.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.BasicSensorEvent;

@Component
public interface SensorsService {
    void sendSensorEvent(BasicSensorEvent hubEvent) throws IllegalAccessException;
}
