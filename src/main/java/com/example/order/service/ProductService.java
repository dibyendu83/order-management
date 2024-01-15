package com.example.order.service;

import com.example.order.dto.Product;
import com.example.order.dto.Vendor;
import com.example.order.exceptions.ResourceNotFoundException;
import com.example.order.repository.ProductRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VendorService vendorService;

    public Product createProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        if (product.getVendors() != null && !product.getVendors().isEmpty()) {
            product.getVendors().forEach(v -> vendorService.addVendorToProduct(savedProduct, v));
        }
        return savedProduct;
    }

    public List<Product> saveAllProducts(List<Product> products) {
        productRepository.saveAll(products);
        products.stream().forEach(product -> {
            if (product.getVendors() != null && !product.getVendors().isEmpty()) {
                product.getVendors().forEach(v -> vendorService.addVendorToProduct(product, v));
            }
        });
        return products;
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(long productId) throws ResourceNotFoundException {
        return productRepository
            .findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found Product with id = " + productId));
    }


    public Product addVendor(long productId, Vendor vendor) throws ResourceNotFoundException {
        Product product = getProduct(productId);
        vendorService.addVendorToProduct(product, vendor);
        return productRepository.getById(productId);
    }

    public void removeVendor(long vendorId) throws ResourceNotFoundException {
        vendorService.removeVendorFromProduct(vendorId);
    }
}
