package ru.yandex.practicum.collector.handler;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {
    SensorEventProto.PayloadCase getEventType();

    void handle(SensorEventProto event) throws IllegalArgumentException;
}
