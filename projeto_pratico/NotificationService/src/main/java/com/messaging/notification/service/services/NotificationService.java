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
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String ANSI_CYAN = "\u001B[36m";


    @JmsListener(destination = LISTENER_QUEUE)
    public void receiveMessage(@Valid ResponseDTO responseDTO) {
        logger.info("Received message");

        System.out.println(ANSI_CYAN + "==================================" + " Início E-mail " + "==================================");

        System.out.println(ANSI_PURPLE + "ID do pedido: " + ANSI_WHITE + responseDTO.orderID() + '\n');
        
        System.out.println(ANSI_PURPLE + "Timestamp: " + ANSI_WHITE + responseDTO
            .timestamp().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + '\n');
        if(!responseDTO.listaEstoqueDisponivel().isEmpty())
            System.out.println(ANSI_GREEN + "Itens disponíveis:");

        for(ItemDTO item : responseDTO.listaEstoqueDisponivel()) {
            System.out.println(ANSI_CYAN + "\tNome do item: " + ANSI_WHITE + item.name());
            System.out.println(ANSI_CYAN + "\tDescrição: " + ANSI_WHITE + item.description());
            System.out.println(ANSI_CYAN + "\tQuantidade atualmente em estoque: " + ANSI_WHITE + item.quantityStock() + '\n');
        }

        if(!responseDTO.listaEstoqueIndisponivel().isEmpty())
            System.out.println(ANSI_YELLOW + "Itens estoque insuficiente:");

        for(ItemDTO item : responseDTO.listaEstoqueIndisponivel()) {
            System.out.println(ANSI_CYAN + "\tNome do item: " + ANSI_WHITE + item.name());
            System.out.println(ANSI_CYAN + "\tDescrição: " + ANSI_WHITE + item.description());
            System.out.println(ANSI_CYAN + "\tQuantidade atualmente em estoque: " + ANSI_WHITE + item.quantityStock() + '\n');

        }
        
        if(!responseDTO.listaEstoqueInexistente().isEmpty())
            System.out.println(ANSI_RED + "Itens inexistentes:");

        for(String item : responseDTO.listaEstoqueInexistente()) {
            System.out.println(ANSI_CYAN + "\tItem inexistente: " + ANSI_WHITE + item + '\n');
        }
        
        System.out.print(ANSI_PURPLE + "Status do pedido: ");
        if (responseDTO.orderStatus().equalsIgnoreCase("FAILED")) {
            System.out.println(ANSI_RED + "Falha");
        } else {
            System.out.println(ANSI_GREEN + "Sucesso");
        }

        System.out.println(ANSI_CYAN + "==================================" + " Final E-mail " + "==================================");
    }

}
