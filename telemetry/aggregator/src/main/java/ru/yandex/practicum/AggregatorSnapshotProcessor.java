package ru.yandex.practicum;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AggregatorSnapshotProcessor {
    private final Map<String, SensorSnapshotAvro> snapshotsMap = new ConcurrentHashMap<>();

    public Optional<SensorSnapshotAvro> updateState(SensorEventAvro event) {

        SensorSnapshotAvro sensorSnapshot = SensorSnapshotAvro.newBuilder()
                .setHubId(event.getHubId())
                .setSensorState(new HashMap<>())
                .setTimestamp(event.getTimestamp())
                .build();
        SensorSnapshotAvro sensorSnapshotAvro = snapshotsMap.getOrDefault(event.getHubId(), sensorSnapshot);

        SensorStateAvro oldState = sensorSnapshotAvro.getSensorState().get(event.getId());
        if (oldState != null) {
            if (oldState.getTimestamp().isAfter(event.getTimestamp()) || oldState.getData().equals(event.getPayload())) {
                return Optional.empty();
            }
        }
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();
        sensorSnapshotAvro.getSensorState().put(event.getId(), newState);
        sensorSnapshotAvro.setTimestamp(event.getTimestamp());
        snapshotsMap.put(event.getHubId(), sensorSnapshotAvro);

        return Optional.of(sensorSnapshotAvro);

    }
}
