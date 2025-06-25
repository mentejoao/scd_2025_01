package com.messaging.notification.service.services;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.messaging.notification.service.dtos.ItemDTO;
import com.messaging.notification.service.dtos.ResponseDTO;

import jakarta.validation.Valid;

@Service
public class NotificationService {

    private static final String LISTENER_QUEUE = "inventory-events";
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_CYAN = "\u001B[36m";

    @JmsListener(destination = LISTENER_QUEUE)
    public void receiveMessage(@Valid ResponseDTO responseDTO) {
        logger.info("Received message");

        StringBuilder emailContent = new StringBuilder();

        emailContent.append(ANSI_CYAN).append("==================================").append(" Início E-mail ").append("==================================").append(ANSI_RESET).append('\n');

        emailContent.append(ANSI_PURPLE).append("ID do pedido: ").append(ANSI_WHITE).append(responseDTO.orderID()).append(ANSI_RESET).append('\n').append('\n');
        
        emailContent.append(ANSI_PURPLE).append("Timestamp: ").append(ANSI_WHITE).append(responseDTO
            .timestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append(ANSI_RESET).append('\n').append('\n');
        
        if (!responseDTO.listaEstoqueDisponivel().isEmpty()) {
            emailContent.append(ANSI_GREEN).append("Itens disponíveis:").append(ANSI_RESET).append('\n');
            for (ItemDTO item : responseDTO.listaEstoqueDisponivel()) {
                emailContent.append(ANSI_CYAN).append("\tNome do item: ").append(ANSI_WHITE).append(item.name()).append(ANSI_RESET).append('\n');
                emailContent.append(ANSI_CYAN).append("\tDescrição: ").append(ANSI_WHITE).append(item.description()).append(ANSI_RESET).append('\n');
                emailContent.append(ANSI_CYAN).append("\tQuantidade atualmente em estoque: ").append(ANSI_WHITE).append(item.quantityStock()).append(ANSI_RESET).append('\n').append('\n');
            }
        }

        if (!responseDTO.listaEstoqueIndisponivel().isEmpty()) {
            emailContent.append(ANSI_YELLOW).append("Itens estoque insuficiente:").append(ANSI_RESET).append('\n');
            for (ItemDTO item : responseDTO.listaEstoqueIndisponivel()) {
                emailContent.append(ANSI_CYAN).append("\tNome do item: ").append(ANSI_WHITE).append(item.name()).append(ANSI_RESET).append('\n');
                emailContent.append(ANSI_CYAN).append("\tDescrição: ").append(ANSI_WHITE).append(item.description()).append(ANSI_RESET).append('\n');
                emailContent.append(ANSI_CYAN).append("\tQuantidade atualmente em estoque: ").append(ANSI_WHITE).append(item.quantityStock()).append(ANSI_RESET).append('\n').append('\n');
            }
        }
        
        if (!responseDTO.listaEstoqueInexistente().isEmpty()) {
            emailContent.append(ANSI_RED).append("Itens inexistentes:").append(ANSI_RESET).append('\n');
            for (String item : responseDTO.listaEstoqueInexistente()) {
                emailContent.append(ANSI_CYAN).append("\tItem inexistente: ").append(ANSI_WHITE).append(item).append(ANSI_RESET).append('\n').append('\n');
            }
        }
        
        emailContent.append(ANSI_PURPLE).append("Status do pedido: ").append(ANSI_RESET);
        if (responseDTO.orderStatus().equalsIgnoreCase("FAILED")) {
            emailContent.append(ANSI_RED).append("Falha").append(ANSI_RESET).append('\n');
        } else {
            emailContent.append(ANSI_GREEN).append("Sucesso").append(ANSI_RESET).append('\n');
        }

        emailContent.append(ANSI_CYAN).append("==================================").append(" Final E-mail ").append("==================================").append(ANSI_RESET).append('\n');

        System.out.println(emailContent.toString());
    }
}