package com.messaging.order_service.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemOrderDTO(
    
    @NotBlank(message = "O nome do item é obrigatório.")
    @Size(min = 1, max = 250, message = "O nome do item deve ter entre 1 e 250 caracteres.")
    String itemName,

    @NotNull(message = "A quantidade é obrigatória.")
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1.")
    Integer quantity) {}
