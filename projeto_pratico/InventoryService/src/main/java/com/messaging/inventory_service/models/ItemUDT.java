package com.messaging.inventory_service.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.AllArgsConstructor;
import lombok.Data;

@UserDefinedType("item_udt")
@Data
@AllArgsConstructor
public class ItemUDT {

    @Column("name")
    private String name;

    @Column("description")
    private String description;

    @Column("quantity_stock")
    private Integer quantityStock;
    
}