package com.kafkaprect.kafkaperctice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaprect.kafkaperctice.Model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendOrder(OrderRequest order) {
        try {
            String json = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("order_topic", json);
        } catch (Exception e) {
            System.err.println("Failed to serialize order: " + e.getMessage());
        }
    }
}
