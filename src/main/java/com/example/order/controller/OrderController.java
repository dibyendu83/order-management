package com.example.order.controller;

import com.example.order.dto.Order;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.Product;
import com.example.order.dto.Vendor;
import com.example.order.exceptions.InputValidationException;
import com.example.order.exceptions.ResourceNotFoundException;
import com.example.order.service.OrderService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    //order related api
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody Order order) throws InputValidationException {
        Order createdOrder = orderService.createOrder(order);
        return new ResponseEntity(createdOrder, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity modifyOrder(@RequestBody Order order) throws ResourceNotFoundException {
        OrderResponse createdOrder = orderService.updateOrder(order);
        return new ResponseEntity(createdOrder, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable String id) throws ResourceNotFoundException {
        return new ResponseEntity(orderService.getOrder(Long.parseLong(id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteOrder(@PathVariable String id) throws ResourceNotFoundException {
        orderService.deleteOrder(Long.parseLong(id));
        return ResponseEntity.noContent().build();
    }

    // Product related api

    @PostMapping("/{orderId}/products")
    public ResponseEntity<OrderResponse> addProduct(@PathVariable String orderId,
                                                    @RequestBody List<Product> products) throws ResourceNotFoundException {

        OrderResponse order = orderService.addProductToOrder(Long.parseLong(orderId), products);
        return new ResponseEntity(order, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity removeProduct(@PathVariable String orderId,
                                        @PathVariable String productId) throws ResourceNotFoundException {
        OrderResponse order = orderService.removeProductFromOrder(Long.parseLong(orderId), Long.parseLong(productId));
        return new ResponseEntity(order, HttpStatus.OK);
    }

    // vendor related api
    @PostMapping("/{orderId}/products/{productId}/vendors")
    public ResponseEntity<OrderResponse> addVendor(@PathVariable String orderId,
                                                   @PathVariable String productId,
                                                   @RequestBody Vendor vendor) throws ResourceNotFoundException {

        OrderResponse order = orderService.addVendorToExistingOrderProduct(Long.parseLong(orderId), Long.parseLong(productId), vendor);
        return new ResponseEntity(order, HttpStatus.OK);
    }

    @DeleteMapping("/{orderId}/products/{productId}/vendors/{vendorId}")
    public ResponseEntity removeVendor(@PathVariable String orderId, @PathVariable String productId,
                                       @PathVariable String vendorId) throws ResourceNotFoundException {

        OrderResponse order = orderService.removeVendorFromExistingOrderProduct(Long.parseLong(orderId),
            Long.parseLong(productId), Long.parseLong(vendorId));
        return new ResponseEntity(order, HttpStatus.OK);
    }
}
