package com.example.order.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
@EqualsAndHashCode(exclude = {"orderProducts"})
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String description;

    @Column(name = "quantity")
    private int qty;
    private double price;
    private LocalDate orderDate;
    private LocalDateTime modifiedDate;

    @Transient
    private List<Product> orderProducts = new ArrayList<>();

    public Order() {

    }

}
