package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.model.Sensor;

import java.util.Collection;
import java.util.Optional;

public interface SensorRepository extends JpaRepository<Sensor, String> {
    Boolean existsByIdInAndHubId(Collection<String> ids, String hubId);

    void deleteByIdAndHubId(String id, String hubId);

    Optional<Sensor> findByIdAndHubId(String id, String hubId);
}
