package com.kafkaprect.kafkaperctice.Repo;

import com.kafkaprect.kafkaperctice.Model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, String> {
}
