package com.kafkaprect.kafkaperctice.Controller;

import com.kafkaprect.kafkaperctice.Model.OrderEntity;
import com.kafkaprect.kafkaperctice.Model.OrderRequest;
import com.kafkaprect.kafkaperctice.Repo.OrderRepository;
import com.kafkaprect.kafkaperctice.Service.OrderProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderProducer producer;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest order) {
        producer.sendOrder(order);
        return ResponseEntity.ok("Order placed: " + order.getOrderId());
    }

    @GetMapping
    public ResponseEntity<List<OrderEntity>> getAllOrders() {
        List<OrderEntity> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }
    @GetMapping("/paged")
    public ResponseEntity<Page<OrderEntity>> getOrdersPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("orderId").descending());
        Page<OrderEntity> orders = orderRepository.findAll(pageable);
        return ResponseEntity.ok(orders);
    }


}
