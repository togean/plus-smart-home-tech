package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.SnapshotHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
@Configuration
public class SnapshotProcessor  implements Runnable {

    private static final Duration ATTEMPTS_TIMEOUT = Duration.ofMillis(500);
    private final KafkaConsumer<String, SensorSnapshotAvro> consumer;
    private final SnapshotHandler snapshotHandler;

    public SnapshotProcessor(SnapshotHandler snapshotHandler) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "snapshotConsumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "snapshot");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.SnapshotDeserializer");
        this.consumer = new KafkaConsumer<>(properties);
        this.snapshotHandler = snapshotHandler;
    }

    @Override
    public void run() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            String SNAPSHOTS_TOPIC = "telemetry.snapshots.v1";
            consumer.subscribe(List.of(SNAPSHOTS_TOPIC));

            while (true) {
                ConsumerRecords<String, SensorSnapshotAvro> records = consumer.poll(ATTEMPTS_TIMEOUT);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, SensorSnapshotAvro> record : records) {
                        SensorSnapshotAvro sensorsSnapshotAvro = record.value();
                        snapshotHandler.handle(sensorsSnapshotAvro);
                    }
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка: произошла ошибка в цикле обработки снапшотов");
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
