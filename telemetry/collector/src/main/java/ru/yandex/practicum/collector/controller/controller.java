package ru.yandex.practicum.collector.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import ru.yandex.practicum.collector.model.hub.BasicHubEvent;
import ru.yandex.practicum.collector.model.sensor.BasicSensorEvent;
import ru.yandex.practicum.collector.service.HubsServiceImpl;
import ru.yandex.practicum.collector.service.SensorsServiceImpl;


@Validated
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class controller {

    private final SensorsServiceImpl sensorService;
    private final HubsServiceImpl hubService;

    @PostMapping("/sensors")
    public void collectSensorEvent(@RequestBody @Valid BasicSensorEvent sensorEvent) throws IllegalAccessException {
        sensorService.sendSensorEvent(sensorEvent);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@RequestBody @Valid BasicHubEvent hubEvent) throws IllegalAccessException {
        hubService.sendHubEvent(hubEvent);
    }
}
