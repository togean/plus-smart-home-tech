package ru.yandex.practicum.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
@Slf4j
@Configuration
public class HubEventProcessor implements Runnable {

    private static final Duration ATTEMPTS_TIMEOUT = Duration.ofMillis(500);
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final HubEventHandler hubEventHandler;

    public HubEventProcessor(HubEventHandler hubEventHandler) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, "hubConsumer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "hub");
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "ru.yandex.practicum.HubEventDeserializer");

        this.consumer = new KafkaConsumer<>(properties);
        this.hubEventHandler = hubEventHandler;
    }

    @Override
    public void run() {
        try {
            String HUBS_TOPIC = "telemetry.hubs.v1";
            consumer.subscribe(List.of(HUBS_TOPIC));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(ATTEMPTS_TIMEOUT);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, HubEventAvro> record : records) {
                        HubEventAvro hubEventAvro = record.value();
                        hubEventHandler.handle(hubEventAvro);
                    }
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка: произошла ошибка в цикле обработки событий от устройств");
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }
}
