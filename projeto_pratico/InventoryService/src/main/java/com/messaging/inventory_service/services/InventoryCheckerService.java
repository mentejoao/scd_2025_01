package com.messaging.inventory_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.messaging.inventory_service.models.Item;
import com.messaging.inventory_service.models.ItemUDT;
import com.messaging.inventory_service.models.Response;
import com.messaging.inventory_service.models.dtos.ItemDTO;
import com.messaging.inventory_service.models.dtos.ItemOrderDTO;
import com.messaging.inventory_service.models.dtos.OrderDTO;
import com.messaging.inventory_service.models.dtos.ResponseDTO;
import com.messaging.inventory_service.models.enums.OrderStatus;
import com.messaging.inventory_service.models.enums.StockStatus;
import com.messaging.inventory_service.repositories.ItemRepository;
import com.messaging.inventory_service.repositories.ResponseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventoryCheckerService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private JmsTemplate jmsTemplate;

    private static final String LISTENER_QUEUE = "orders";
    private static final String RESPONSE_QUEUE = "inventory-events";
    private static final Logger logger = LoggerFactory.getLogger(InventoryCheckerService.class);

    @JmsListener(destination = LISTENER_QUEUE)
    @Transactional
    public void checkInventoryEventQueue(OrderDTO orderEvent) {
        logger.info("Mensagem recebida da fila [orders]: {}", orderEvent.toString());
        
        // Idempotência
        Optional<Response> existingResponse = responseRepository.findById(orderEvent.orderId());
        if (existingResponse.isPresent()) {
            logger.warn("Pedido com id {} já foi processado. Reenviando resposta anterior.", orderEvent.orderId());
            publicResponse(existingResponse.get());
            return;
        }

        Response response = generateResponse(orderEvent);
        responseRepository.save(response);
        
        logger.info("Resposta salva no banco de dados para o pedido {}", response.getOrderID());

        publicResponse(response);
    }

    private Response generateResponse(OrderDTO orderEvent) {
        Response response = new Response();
        response.setOrderID(orderEvent.orderId());
        response.setTimestamp(orderEvent.timestamp());
        logger.info("Checando estoque para a order com id: {}", orderEvent.orderId().toString());

        for (ItemOrderDTO itemOrder : orderEvent.items()) {
            //TODO: Revisar como extinguir multiplas consultas ao banco de dados
            Optional<Item> item = itemRepository.findById(itemOrder.itemName());
            StockStatus stockStatus = checkStock(item, itemOrder);
            

            if (stockStatus == StockStatus.IN_STOCK) {
                ItemUDT itemBanco = new ItemUDT(item.get().getName(),
                        item.get().getDescription(), item.get().getQuantityStock());
                response.getListaEstoqueDisponivel().add(itemBanco);
            }

            if (stockStatus == StockStatus.OUT_OF_STOCK) {
                ItemUDT itemBanco = new ItemUDT(item.get().getName(),
                        item.get().getDescription(), item.get().getQuantityStock());
                response.getListaEstoqueIndisponivel().add(itemBanco);
            }

            if (stockStatus == StockStatus.NON_EXISTENT) {
                response.getListaEstoqueInexistente().add(itemOrder.itemName());
            }
        }
        if (response.getListaEstoqueIndisponivel().isEmpty() && response.getListaEstoqueInexistente().isEmpty()) {
            response.setOrderStatus(OrderStatus.SUCCESS);
            updateStock(response.getListaEstoqueDisponivel(), orderEvent.items());
            logger.info("Todos os itens estão disponíveis. Pedido aprovado.");
        } else {
            response.setOrderStatus(OrderStatus.FAILED);
            logger.info("Alguns itens não estão disponíveis. Pedido rejeitado.");
        }

        return response;
    }

    private void updateStock(List<ItemUDT> itemsStock, List<ItemOrderDTO> itemsOrder) {
        for (int i = 0; i < itemsStock.size(); i++) {
            ItemUDT item = itemsStock.get(i);
            ItemOrderDTO itemOrder = itemsOrder.get(i);
            item.setQuantityStock(item.getQuantityStock() - itemOrder.quantity());
            Item itemSalvar = new Item(item.getName(), item.getDescription(), item.getQuantityStock());
            itemRepository.save(itemSalvar);
            logger.info("Estoque atualizado para o item: {}. Nova quantidade em estoque: {}",
                    item.getName(), item.getQuantityStock());
        }
    }

    private StockStatus checkStock(Optional<Item> item, ItemOrderDTO itemOrderDTO) {
        if (item.isPresent()) {
            if (itemOrderDTO.quantity() > item.get().getQuantityStock()) {
                logger.info("Item {} não disponível em estoque. Quantidade solicitada: {}, quantidade disponível: {}",
                        itemOrderDTO.itemName(), itemOrderDTO.quantity(), item.get().getQuantityStock());
                return StockStatus.OUT_OF_STOCK;
            }
            logger.info("Item {} disponível em estoque. Quantidade solicitada: {}, quantidade disponível: {}",
                    itemOrderDTO.itemName(), itemOrderDTO.quantity(), item.get().getQuantityStock());
            return StockStatus.IN_STOCK;
        }
        logger.info("Item {} não encontrado.", itemOrderDTO.itemName());
        return StockStatus.NON_EXISTENT;
    }

    private void publicResponse(Response response) {
        try {
            ResponseDTO responseDTO = convertToResponseDTO(response);
            logger.info("Publicando resposta na fila [inventory-events]: {}", responseDTO.toString());
            jmsTemplate.convertAndSend(RESPONSE_QUEUE, responseDTO);
            logger.info("Resposta publicada com sucesso.");
        } catch (Exception e) {
            logger.error("Falha ao publicar resposta para a orderID: {}. Erro: {}", response.getOrderID(), e.getMessage());
        }
    }

    private ResponseDTO convertToResponseDTO(Response response) {
        List<ItemDTO> availableItems = new ArrayList<>();
        List<ItemDTO> unavailableItems = new ArrayList<>();
        for (ItemUDT item : response.getListaEstoqueDisponivel()) {
            availableItems.add(new ItemDTO(item.getName(), item.getDescription(), item.getQuantityStock()));
        }
        for (ItemUDT item : response.getListaEstoqueIndisponivel()) {
            unavailableItems.add(new ItemDTO(item.getName(), item.getDescription(), item.getQuantityStock()));
        }
        return new ResponseDTO(
            response.getOrderID(),
            response.getTimestamp(),
            availableItems,
            unavailableItems,
            response.getListaEstoqueInexistente(),
            response.getOrderStatus().toString()
        );
    }


}
