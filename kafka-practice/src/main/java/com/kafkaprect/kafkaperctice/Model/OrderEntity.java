package com.kafkaprect.kafkaperctice.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderEntity{
    @Id
    private String orderId;
    private String item;
    private int quantity;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }




}
