package ru.yandex.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollectorKafkaProducer implements AutoCloseable {
    @Value("${kafka_server}")
    String bootstrap;

    @Value("${key_serializer}")
    String keySerializer;

    @Value("${value_serializer}")
    String valueSerializer;
    private KafkaProducer<String, SpecificRecordBase> producer;


    public void send(String topic, Instant timestamp, String hubId, SpecificRecordBase event) {
        if (event == null) {
            return;
        }

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topic,
                null,
                timestamp.toEpochMilli(),
                hubId,
                event
        );

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        producer = new KafkaProducer<>(properties);

        Future<RecordMetadata> futureResult = producer.send(record);
        producer.flush();

        try {
            RecordMetadata metadata = futureResult.get();
            log.info("Событие {} успешно записано в топик {} в партицию {} со смещением {}",
                    record, metadata.topic(), metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("Ошибка: не удалось записать событие", e);
        }
    }

    @Override
    public void close() {
        try {
            producer.flush();
            producer.close();
        } catch (Exception e) {
            log.error("Ошибка: ошибка при завершении работы Kafka producer", e);
        }
    }
}
