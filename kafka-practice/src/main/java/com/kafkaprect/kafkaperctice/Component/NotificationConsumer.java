package com.kafkaprect.kafkaperctice.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafkaprect.kafkaperctice.Model.InventoryStatus;
import com.kafkaprect.kafkaperctice.Model.InventoryStatusEntity;
import com.kafkaprect.kafkaperctice.Repo.InventoryStatusRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InventoryStatusRepository inventoryStatusRepository;

    public NotificationConsumer(InventoryStatusRepository inventoryStatusRepository) {
        this.inventoryStatusRepository = inventoryStatusRepository;
    }

    @KafkaListener(topics = "inventory_topic", groupId = "notification-group")
    public void consumeInventoryStatus(String message) {
        try {
            InventoryStatus status = objectMapper.readValue(message, InventoryStatus.class);

            // Save to DB
            InventoryStatusEntity entity = new InventoryStatusEntity();
            entity.setOrderId(status.getOrderId());
            entity.setInStock(status.isInStock());
            entity.setMessage(status.getMessage());

            inventoryStatusRepository.save(entity);
            System.out.println("✅ Saved inventory status to DB: " + entity.getOrderId());


            System.out.println("📩 Notification saved: Order " + status.getOrderId()
                    + " → " + status.getMessage());

        } catch (Exception e) {
            System.err.println("❌ NotificationConsumer failed: " + e.getMessage());
        }
    }
}
