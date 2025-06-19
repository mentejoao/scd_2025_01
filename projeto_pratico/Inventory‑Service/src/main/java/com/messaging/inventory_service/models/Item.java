package com.messaging.inventory_service.models;

import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("itens")
public class Item {

    @PrimaryKey
    UUID id;

    @Column("name")
    String name;

    @Column("description")
    String description;

    @Column("quantity_stock")
    Integer quantityStock;
    
}
