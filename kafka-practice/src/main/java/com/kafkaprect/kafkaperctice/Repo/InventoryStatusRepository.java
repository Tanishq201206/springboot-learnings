package com.kafkaprect.kafkaperctice.Repo;

import com.kafkaprect.kafkaperctice.Model.InventoryStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryStatusRepository extends JpaRepository<InventoryStatusEntity, String> {
}
