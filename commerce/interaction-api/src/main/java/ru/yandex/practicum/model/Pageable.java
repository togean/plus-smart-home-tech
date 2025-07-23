package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Pageable {
    private Integer page;
    private Integer size;
    private List<String> sort;
}
