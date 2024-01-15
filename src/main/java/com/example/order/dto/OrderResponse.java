package com.example.order.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderResponse {

    private long id;
    private String description;
    private int qty;
    private double price;
    private LocalDate orderDate;
    private LocalDateTime modifiedDate;
    private List<Product> orderProducts = new ArrayList<>();

    public OrderResponse(Order order, List<Product> orderProducts) {
        setId(order.getId());
        setDescription(order.getDescription());
        setQty(order.getQty());
        setPrice(order.getPrice());
        setOrderDate(order.getOrderDate());
        setModifiedDate(order.getModifiedDate());
        setOrderProducts(orderProducts);
    }
}
