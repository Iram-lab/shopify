package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.dto.InventoryDtos.*;
import com.ecommerce.inventory.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    @Mapping(target = "availableStock", expression = "java(inventory.getAvailableStock())")
    InventoryResponse toDto(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Inventory toEntity(InventoryRequest request);
}
