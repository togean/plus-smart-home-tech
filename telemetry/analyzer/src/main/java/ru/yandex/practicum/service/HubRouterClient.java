package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.model.Action;

import java.time.Instant;

@Component
@Slf4j
public class HubRouterClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    public HubRouterClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient) {
        this.hubRouterClient = hubRouterClient;
    }

    public void sendAction(Action action) {
        try {

            DeviceActionRequest deviceActionRequest = DeviceActionRequest.newBuilder()
                    .setHubId(action.getScenario().getHubId())
                    .setScenarioName(action.getScenario().getName())
                    .setAction(DeviceActionProto.newBuilder()
                            .setSensorId(action.getSensor().getId())
                            .setType(convertToTypeProto(action.getType()))
                            .setValue(action.getValue())
                            .build())
                    .setTimestamp(setTimeStamp())
                    .build();
            hubRouterClient.handleDeviceAction(deviceActionRequest);
            log.info("Действие отправлено в HubRouter");
        } catch (Exception e) {
            log.info("Ошибка: ошибка при отправке действия в HubRouter: {}", e.getLocalizedMessage());
            throw new RuntimeException("Ошибка при попытке отправить действие в HubRouter");
        }
    }

    private ActionTypeProto convertToTypeProto(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

    private Timestamp setTimeStamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}
