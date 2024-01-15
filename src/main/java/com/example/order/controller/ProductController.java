package com.example.order.controller;

import com.example.order.dto.Product;
import com.example.order.dto.Vendor;
import com.example.order.exceptions.ResourceNotFoundException;
import com.example.order.service.ProductService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return new ResponseEntity(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity getProductDetails(@PathVariable String productId) throws ResourceNotFoundException {
        Product product = productService.getProduct(Long.parseLong(productId));
        return new ResponseEntity(product, HttpStatus.OK);
    }

    @PostMapping("/{productId}/vendors")
    public ResponseEntity<Product> addVendor(@PathVariable String productId,
                                             @RequestBody Vendor vendor) throws ResourceNotFoundException {
        Product product = productService.addVendor(Long.parseLong(productId), vendor);
        return new ResponseEntity(product, HttpStatus.OK);
    }

    @DeleteMapping("/vendors/{vendorId}")
    public ResponseEntity removeVendor(@PathVariable String vendorId) throws ResourceNotFoundException {
        productService.removeVendor(Long.parseLong(vendorId));
        return ResponseEntity.noContent().build();
    }
}
