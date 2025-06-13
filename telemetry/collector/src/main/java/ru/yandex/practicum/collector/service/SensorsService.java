package ru.yandex.practicum.collector.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public interface SensorsService {
    void sendSensorEvent(SensorEventProto hubEvent) throws IllegalAccessException;
}
