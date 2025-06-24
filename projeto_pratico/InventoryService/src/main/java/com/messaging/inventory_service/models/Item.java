package com.messaging.inventory_service.models;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Table("itens")
@Getter @Setter
@AllArgsConstructor
public class Item {

    @PrimaryKey
    @Column("name")
    String name;

    @Column("description")
    String description;

    @Column("quantity_stock")
    Integer quantityStock;
    
}
