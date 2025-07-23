package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.model.Product;
import ru.yandex.practicum.model.ProductDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product productDtoToProduct(ProductDto productDto);

    ProductDto productToProductDto(Product product);

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);
}