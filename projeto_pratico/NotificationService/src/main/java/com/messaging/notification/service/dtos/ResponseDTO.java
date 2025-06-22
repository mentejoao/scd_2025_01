package com.messaging.notification.service.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponseDTO(
    @NotNull(message = "O campo orderID não pode ser nulo")
    UUID orderID,

    @NotNull(message = "O campo timestamp não pode ser nulo")
    LocalDateTime timestamp,

    @NotNull(message = "A lista de estoque disponível não pode ser nula")
    @NotEmpty(message = "A lista de estoque disponível não pode ser vazia")
    List<@NotNull(message = "Item da lista de estoque disponível não pode ser nulo") ItemDTO> listaEstoqueDisponivel,

    @NotNull(message = "A lista de estoque indisponível não pode ser nula")
    @NotEmpty(message = "A lista de estoque indisponível não pode ser vazia")
    List<@NotNull(message = "Item da lista de estoque indisponível não pode ser nulo") ItemDTO> listaEstoqueIndisponivel,

    @NotNull(message = "A lista de estoque inexistente não pode ser nula")
    @NotEmpty(message = "A lista de estoque inexistente não pode ser vazia")
    List<@NotNull(message = "Item da lista de estoque inexistente não pode ser nulo") String> listaEstoqueInexistente,

    @NotNull(message = "O campo orderStatus não pode ser nulo")
    String orderStatus
) {}
