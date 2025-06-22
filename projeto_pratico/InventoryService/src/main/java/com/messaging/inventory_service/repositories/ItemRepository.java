package com.messaging.inventory_service.repositories;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.messaging.inventory_service.models.Item;

public interface ItemRepository extends CassandraRepository<Item, String> {}
