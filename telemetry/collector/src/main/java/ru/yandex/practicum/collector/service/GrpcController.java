package ru.yandex.practicum.collector.service;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.collector.handler.HubEventHandler;
import ru.yandex.practicum.collector.handler.SensorEventHandler;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
@Slf4j
public class GrpcController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlersMap;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlersMap;

    public GrpcController(Set<SensorEventHandler> sensorEventHandlersSet, Set<HubEventHandler> hubEventHandlersSet) {
        this.sensorEventHandlersMap = sensorEventHandlersSet.stream().collect(Collectors.toMap(SensorEventHandler::getEventType, Function.identity()));
        this.hubEventHandlersMap = hubEventHandlersSet.stream().collect(Collectors.toMap(HubEventHandler::getEventType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            if (sensorEventHandlersMap.containsKey(request.getPayloadCase())) {
                sensorEventHandlersMap.get(request.getPayloadCase()).handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                throw new IllegalArgumentException("Ошибка: неизвестный тип события от датчика " + request.getPayloadCase());
            }
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            if (hubEventHandlersMap.containsKey(request.getPayloadCase())) {
                hubEventHandlersMap.get(request.getPayloadCase()).handle(request);
                responseObserver.onNext(Empty.getDefaultInstance());
                responseObserver.onCompleted();
            } else {
                throw new IllegalArgumentException("Ошибка: неизвестный тип события от хаба " + request.getPayloadCase());
            }
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL
                            .withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
