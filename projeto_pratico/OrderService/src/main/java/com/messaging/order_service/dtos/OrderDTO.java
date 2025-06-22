package com.messaging.order_service.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO(
    @NotNull(message = "O ID do pedido não pode ser nulo")
    UUID orderId,

    @NotNull(message = "A data e hora do pedido não podem ser nulas")
    LocalDateTime timestamp,

    @NotNull(message = "A lista de itens não pode ser nula")
    @NotEmpty(message = "A lista de itens não pode estar vazia")
    List<@NotNull(message = "O item do pedido não pode ser nulo") ItemOrderDTO> items
) {}
