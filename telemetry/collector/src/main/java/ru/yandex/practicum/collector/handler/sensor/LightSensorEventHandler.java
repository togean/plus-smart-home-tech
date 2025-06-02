package ru.yandex.practicum.collector.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensor.BasicSensorEvent;
import ru.yandex.practicum.collector.model.sensor.LightSensorEvent;
import ru.yandex.practicum.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.collector.service.CollectorKafkaProducer;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Component
public class LightSensorEventHandler extends BasicSensorEventHandler<LightSensorAvro> {

    public LightSensorEventHandler(CollectorKafkaProducer producer) {
        super(producer);
    }

    @Override
    protected LightSensorAvro MapToAvro(BasicSensorEvent sensorEvent) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    @Override
    public SensorEventType getEventType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
