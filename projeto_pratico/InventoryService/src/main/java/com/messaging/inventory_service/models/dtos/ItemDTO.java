package com.messaging.inventory_service.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

public record ItemDTO(
    
    @NotNull(message = "O nome não pode ser nulo.")
    String name,

    @NotNull(message = "A descrição não pode ser nula.")
    @Size(min = 1, max = 250, message = "A descrição deve ter entre 1 e 250 caracteres.")
    String description,

    @NotNull(message = "A quantidade em estoque não pode ser nula.")
    @Min(value = 1, message = "A quantidade em estoque deve ser no mínimo 1.")
    Integer quantityStock
) {}
