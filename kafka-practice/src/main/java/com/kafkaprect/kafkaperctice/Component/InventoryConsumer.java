package com.kafkaprect.kafkaperctice.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaprect.kafkaperctice.Model.InventoryStatus;
import com.kafkaprect.kafkaperctice.Model.OrderRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaTemplate<String, String> kafkaTemplate;

    public InventoryConsumer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "order_topic", groupId = "order-group")
    public void consume(String message) {
        try {
            OrderRequest order = objectMapper.readValue(message, OrderRequest.class);

            InventoryStatus status = new InventoryStatus();
            status.setOrderId(order.getOrderId());
            status.setInStock(order.getQuantity() <= 5); // dummy logic
            status.setMessage("Stock " + (status.isInStock() ? "available" : "unavailable"));

            String response = objectMapper.writeValueAsString(status);
            kafkaTemplate.send("inventory_topic", response);

            System.out.println("Inventory checked and message sent: " + response);

        } catch (Exception e) {
            System.err.println("Error in InventoryConsumer: " + e.getMessage());
        }
    }
}
