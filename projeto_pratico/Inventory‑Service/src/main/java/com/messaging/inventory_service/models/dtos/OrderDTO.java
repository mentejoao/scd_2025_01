package com.messaging.inventory_service.models.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDTO (UUID orderId, LocalDateTime timestamp,  List<String> items) {}
