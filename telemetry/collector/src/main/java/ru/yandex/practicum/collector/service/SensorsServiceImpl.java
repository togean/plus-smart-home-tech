package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.collector.handler.SensorEventHandler;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SensorsServiceImpl implements SensorsService {

    private final Map<SensorEventType, SensorEventHandler> sensorHandlers;

    @Override
    public void sendSensorEvent(SensorEventProto sensorEvent) throws IllegalAccessException {
        if (sensorHandlers.containsKey(sensorEvent.getPayloadCase())) {
            log.info("Обработка нового события от датчика: {}", sensorEvent);
            sensorHandlers.get(sensorEvent.getPayloadCase()).handle(sensorEvent);
        } else {
            log.warn("Ошибка: неизвестный тип события от датчика: {}", sensorEvent.getPayloadCase());
        }
    }
}
