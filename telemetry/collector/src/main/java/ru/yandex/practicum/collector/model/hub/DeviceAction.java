package ru.yandex.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceAction {

    @NotBlank
    private String sensorId;
    @NotBlank
    private ActionType type;

    private Integer value;
}
