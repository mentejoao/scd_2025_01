package com.messaging.inventory_service.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.messaging.inventory_service.models.enums.OrderStatus;

import lombok.Getter;
import lombok.Setter;

@Table("responses")
@Getter
@Setter
public class Response {

    @PrimaryKey
    private UUID orderID;

    @Column("timestamp")
    private LocalDateTime timestamp;

    @Column("itens_disponiveis")
    private List<ItemUDT> listaEstoqueDisponivel = new ArrayList<>();

    @Column("itens_indisponiveis")
    private List<ItemUDT> listaEstoqueIndisponivel = new ArrayList<>();

    @Column("itens_inexistentes")
    private List<String> listaEstoqueInexistente = new ArrayList<>();

    @Column("order_status")
    private OrderStatus orderStatus;
}
