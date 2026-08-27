package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.dto.InventoryDtos;
import com.ecommerce.inventory.entity.Inventory;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-12T16:22:06+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.11 (Oracle Corporation)"
)
@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryDtos.InventoryResponse toDto(Inventory inventory) {
        if ( inventory == null ) {
            return null;
        }

        Long id = null;
        Long productId = null;
        Integer quantityAvailable = null;
        Integer reservedQuantity = null;

        id = inventory.getId();
        productId = inventory.getProductId();
        quantityAvailable = inventory.getQuantityAvailable();
        reservedQuantity = inventory.getReservedQuantity();

        Integer availableStock = inventory.getAvailableStock();

        InventoryDtos.InventoryResponse inventoryResponse = new InventoryDtos.InventoryResponse( id, productId, quantityAvailable, reservedQuantity, availableStock );

        return inventoryResponse;
    }

    @Override
    public Inventory toEntity(InventoryDtos.InventoryRequest request) {
        if ( request == null ) {
            return null;
        }

        Inventory.InventoryBuilder inventory = Inventory.builder();

        inventory.productId( request.productId() );
        inventory.quantityAvailable( request.quantityAvailable() );

        inventory.reservedQuantity( 0 );

        return inventory.build();
    }
}
