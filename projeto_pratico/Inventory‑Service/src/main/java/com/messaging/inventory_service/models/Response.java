package com.messaging.inventory_service.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("responses")
public class Response {
    @PrimaryKey
    private UUID orderID;

    @Column("timestamp")
    private LocalDateTime timestamp;

    @Column("items_disponiveis")
    private List<Item> listaEstoqueDisponivel;

    @Column("items_indisponiveis")
    private List<Item> listaEstoqueIndisponivel;

    @Column("preco_total")
    private BigDecimal precoTotal;
}
