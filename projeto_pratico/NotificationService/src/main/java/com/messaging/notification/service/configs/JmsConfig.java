package com.messaging.notification.service.configs; // Pacote do seu serviço receptor

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messaging.notification.service.dtos.ResponseDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    public static final String RESPONSE_TYPE_ID = "response-dto";

    @Bean
    public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        
        typeIdMappings.put(RESPONSE_TYPE_ID, ResponseDTO.class);
        
        converter.setTypeIdMappings(typeIdMappings);
        converter.setObjectMapper(objectMapper);
        
        return converter;
    }
}