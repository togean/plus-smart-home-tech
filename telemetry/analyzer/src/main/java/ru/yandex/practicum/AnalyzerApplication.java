package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.processor.HubEventProcessor;
import ru.yandex.practicum.processor.SnapshotProcessor;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AnalyzerApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(AnalyzerApplication.class, args);
        HubEventProcessor hubEventProcessor = applicationContext.getBean(HubEventProcessor.class);
        SnapshotProcessor snapshotProcessor = applicationContext.getBean(SnapshotProcessor.class);

        Thread threadForHubEvents = new Thread(hubEventProcessor);
        threadForHubEvents.setName("HubEventHandlerThread");
        threadForHubEvents.start();
        snapshotProcessor.run();
    }
}
