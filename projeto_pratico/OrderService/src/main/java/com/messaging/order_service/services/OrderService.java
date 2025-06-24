package com.messaging.order_service.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import com.messaging.order_service.dtos.ItemOrderDTO;
import com.messaging.order_service.dtos.OrderDTO;

@Service
public class OrderService {
    
    @Autowired
    JmsTemplate jmsTemplate;

    private static final String RESPONSE_QUEUE = "orders";
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public OrderDTO placeNewOrder(List<ItemOrderDTO> items) {
        logger.info("Iniciando o processo de criação do pedido com os itens: {}", items);
        OrderDTO order = this.createOrder(items);
        logger.info("Enviando o pedido para a fila: {}", order);
        this.sendOrder(order);
        return order;
    }

    private OrderDTO createOrder(List<ItemOrderDTO> items) {
        if (items == null || items.isEmpty()) {
            logger.error("Criação de pedido falhou: A lista de itens está vazia ou nula.");
            throw new IllegalArgumentException("Order não pode ser criada com uma lista de itens vazia ou nula.");
        }
        OrderDTO order = new OrderDTO(
            java.util.UUID.randomUUID(),
            java.time.LocalDateTime.now(),
            items
        );
        logger.info("Pedido criado com sucesso: {}", order);
        return order;
    }

    private void sendOrder(OrderDTO order) {
        if (order == null) {
            throw new IllegalArgumentException("Order não pode ser nulo.");
        }
        jmsTemplate.convertAndSend(RESPONSE_QUEUE, order);
        logger.info("Pedido enviado com sucesso: {}", order);
    }

}
