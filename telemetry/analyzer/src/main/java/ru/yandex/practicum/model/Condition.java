package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="conditions")
@SecondaryTable(name="scenario_conditions", pkJoinColumns = @PrimaryKeyJoinColumn(name="condition_id"))
public class Condition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    private ConditionType type;

    private ConditionOperation operation;

    @ManyToOne
    @JoinColumn(name = "scenario_id", table = "scenario_conditions")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id", table = "scenario_conditions")
    private Sensor sensor;
}
