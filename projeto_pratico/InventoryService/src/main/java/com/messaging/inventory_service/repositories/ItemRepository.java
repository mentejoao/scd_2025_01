package com.messaging.inventory_service.repositories;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import com.messaging.inventory_service.models.Item;

public interface ItemRepository extends CassandraRepository<Item, String> {

    List<Item> findByNameIn(List<String> names);

    @Query("UPDATE itens SET quantity_stock = :newQuantity WHERE name = :name IF quantity_stock = :previousQuantity")
    boolean updateStockWithLWT(String name, int newQuantity, int previousQuantity);

}
