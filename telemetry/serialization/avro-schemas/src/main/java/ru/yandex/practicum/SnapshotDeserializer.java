package ru.yandex.practicum;

import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

public class SnapshotDeserializer extends BaseAvroDeserializer<SensorSnapshotAvro> {

    public SnapshotDeserializer() {
        super(SensorSnapshotAvro.getClassSchema());
    }
}
