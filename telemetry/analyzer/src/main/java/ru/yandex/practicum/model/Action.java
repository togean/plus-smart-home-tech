package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;

@Entity
@Data
@Table(name="actions")
@SecondaryTable(name="scenario_actions", pkJoinColumns = @PrimaryKeyJoinColumn(name="action_id"))
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    private ActionTypeAvro type;

    @ManyToOne
    @JoinColumn(name = "scenario_id", table = "scenario_actions")
    private Scenario scenario;

    @ManyToOne
    @JoinColumn(name = "sensor_id", table = "scenario_actions")
    private Sensor sensor;


}
