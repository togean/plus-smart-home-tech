package ru.yandex.practicum.collector.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends BasicSensorEvent {

    @NotNull
    private int temperatureC;

    @NotNull
    private int humidity;

    @NotNull
    private int co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
