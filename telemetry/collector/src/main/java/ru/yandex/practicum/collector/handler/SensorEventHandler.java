package ru.yandex.practicum.collector.handler;

import ru.yandex.practicum.collector.model.sensor.BasicSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;

public interface SensorEventHandler {
    SensorEventType getEventType();

    void handle(BasicSensorEvent event) throws IllegalAccessException;
}
