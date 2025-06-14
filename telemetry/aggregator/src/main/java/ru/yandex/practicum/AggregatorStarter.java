package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AggregatorStarter {
    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorSnapshotAvro> producer;
    private final AggregatorSnapshotProcessor aggregatorSnapshotProcessor;

    private static final Duration ATTEMPTS_TIMEOUT = Duration.ofMillis(500);
    @Value("${sensorsTopic}")
    private String SENSORS_TOPIC;

    @Value("${snapshotsTopic}")
    private String SNAPSHOT_TOPIC;

    public void startAggregator() {


        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(SENSORS_TOPIC));
            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(ATTEMPTS_TIMEOUT);
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    SensorEventAvro newEvent = record.value();
                    aggregatorSnapshotProcessor.updateState(newEvent).ifPresent(snapshot -> {
                        producer.send(new ProducerRecord<>(SNAPSHOT_TOPIC, snapshot));
                    });
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.info("Ошибка: ошибка при обработке поступающих данных от датчиков", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                consumer.close();
                producer.close();
            }
        }
    }
}
