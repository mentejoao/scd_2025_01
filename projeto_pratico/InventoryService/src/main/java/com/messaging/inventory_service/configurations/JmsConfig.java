package com.messaging.inventory_service.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messaging.inventory_service.models.dtos.OrderDTO;
import com.messaging.inventory_service.models.dtos.ResponseDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String ORDER_TYPE_ID = "order-dto";
    
    public static final String RESPONSE_TYPE_ID = "response-dto";

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        
        typeIdMappings.put(ORDER_TYPE_ID, OrderDTO.class);
        
        typeIdMappings.put(RESPONSE_TYPE_ID, ResponseDTO.class);
        
        converter.setTypeIdMappings(typeIdMappings);
        
        objectMapper.registerModule(new JavaTimeModule());
        converter.setObjectMapper(objectMapper);
        
        return converter;
    }
}