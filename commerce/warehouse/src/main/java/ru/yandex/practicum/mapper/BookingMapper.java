package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.model.BookedProductsDto;
import ru.yandex.practicum.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookedProductsDto toBookedProductsDto(Booking booking);
}
