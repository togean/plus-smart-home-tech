package ru.yandex.practicum.collector.model.hub;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioCondition {

    @NotBlank
    private String sensorId;
    @NotBlank
    private ScenarioConditionType type;
    @NotBlank
    private ScenarioOperation operation;

    private int value;
}
