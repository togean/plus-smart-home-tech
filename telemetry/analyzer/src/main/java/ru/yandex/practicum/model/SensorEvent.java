package ru.yandex.practicum.model;

import lombok.Data;

@Data
public class SensorEvent {
    private String id;

    private Object data;
}
