package com.messaging.inventory_service.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;

import com.messaging.inventory_service.models.Response;

public interface ResponseRepository extends CassandraRepository<Response, UUID> {}
