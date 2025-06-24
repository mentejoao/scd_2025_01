package com.messaging.inventory_service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public void checkInventoryEventQueue(OrderDTO orderEvent) {
        logger.info("Mensagem recebida da fila [orders]: {}", orderEvent.toString());

        // idempotência
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
        logger.info("Checando estoque para o pedido com id: {}", orderEvent.orderId());

        List<String> itemNames = orderEvent.items().stream().map(ItemOrderDTO::itemName).toList();

        Map<String, Item> itemsFromDB = itemRepository.findByNameIn(itemNames)
                .stream()
                .collect(Collectors.toMap(Item::getName, item -> item));

        for (ItemOrderDTO itemOrder : orderEvent.items()) {
            Item item = itemsFromDB.get(itemOrder.itemName());
            StockStatus stockStatus = checkStock(item, itemOrder);

            if (stockStatus == StockStatus.IN_STOCK) {
                response.getListaEstoqueDisponivel().add(new ItemUDT(item.getName(), item.getDescription(), item.getQuantityStock()));
            } 
            
            if (stockStatus == StockStatus.OUT_OF_STOCK) {
                response.getListaEstoqueIndisponivel().add(new ItemUDT(item.getName(), item.getDescription(), item.getQuantityStock()));
            } 
            
            if (stockStatus == StockStatus.NON_EXISTENT) {
                response.getListaEstoqueInexistente().add(itemOrder.itemName());
            }
        }

        if (response.getListaEstoqueIndisponivel().isEmpty() && response.getListaEstoqueInexistente().isEmpty()) {
            boolean stockUpdatedSuccessfully = updateStockAtomically(response.getListaEstoqueDisponivel(), orderEvent.items());

            if (stockUpdatedSuccessfully) {
                response.setOrderStatus(OrderStatus.SUCCESS);
                
                for (int i = 0; i < response.getListaEstoqueDisponivel().size(); i++) {
                    ItemUDT itemUDT = response.getListaEstoqueDisponivel().get(i);
                    ItemOrderDTO itemOrder = orderEvent.items().get(i);
                    itemUDT.setQuantityStock(itemUDT.getQuantityStock() - itemOrder.quantity());
                }
                
                logger.info("Todos os itens estão disponíveis e o estoque foi atualizado. Pedido aprovado.");
            } else {
                response.setOrderStatus(OrderStatus.FAILED);
                logger.error("Falha ao atualizar o estoque atomicamente devido a concorrência. Pedido rejeitado.");
            }
        } else {
            response.setOrderStatus(OrderStatus.FAILED);
            logger.info("Alguns itens não estão disponíveis ou não existem. Pedido rejeitado.");
        }
        
        return response;
    }

    private boolean updateStockAtomically(List<ItemUDT> itemsInStock, List<ItemOrderDTO> itemsOrder) {
        List<Item> successfullyUpdatedItems = new ArrayList<>();
        
        for (int i = 0; i < itemsInStock.size(); i++) {
            ItemUDT itemStock = itemsInStock.get(i);
            ItemOrderDTO itemOrder = itemsOrder.get(i);
            
            int newQuantity = itemStock.getQuantityStock() - itemOrder.quantity();
            
            boolean wasApplied = itemRepository.updateStockWithLWT(
                itemStock.getName(),
                newQuantity,
                itemStock.getQuantityStock()
            );

            if (wasApplied) {
                logger.info("Estoque atualizado para o item: {}. Nova quantidade: {}", itemStock.getName(), newQuantity);

                successfullyUpdatedItems.add(new Item(itemStock.getName(), itemStock.getDescription(), itemOrder.quantity()));
            } else {
                logger.error("Falha para o item: {}. Outra transação pode ter alterado o estoque. Iniciando compensação.", itemStock.getName());
                compensateStockUpdate(successfullyUpdatedItems);
                return false;
            }
        }
        return true;
    }

    private void compensateStockUpdate(List<Item> itemsToCompensate) {
        logger.warn("Iniciando processo de compensação para {} itens.", itemsToCompensate.size());
        for (Item item : itemsToCompensate) {
            Optional<Item> currentItem = itemRepository.findById(item.getName());
            if (currentItem.isPresent()) {
                Item itemToRollback = currentItem.get();
                itemToRollback.setQuantityStock(itemToRollback.getQuantityStock() + item.getQuantityStock());
                itemRepository.save(itemToRollback);
                logger.info("Compensação: Estoque do item {} revertido.", item.getName());
            }
        }
    }

    private StockStatus checkStock(Item item, ItemOrderDTO itemOrderDTO) {
        if (item != null) {
            if (itemOrderDTO.quantity() > item.getQuantityStock()) {
                logger.info("Item {} não disponível em estoque. Solicitado: {}, Disponível: {}", itemOrderDTO.itemName(), itemOrderDTO.quantity(), item.getQuantityStock());
                return StockStatus.OUT_OF_STOCK;
            }
            logger.info("Item {} disponível em estoque. Solicitado: {}, Disponível: {}", itemOrderDTO.itemName(), itemOrderDTO.quantity(), item.getQuantityStock());
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