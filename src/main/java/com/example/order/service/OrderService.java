package com.example.order.service;

import com.example.order.dto.Order;
import com.example.order.dto.OrderResponse;
import com.example.order.dto.Product;
import com.example.order.dto.ProductOrderVendor;
import com.example.order.dto.Vendor;
import com.example.order.exceptions.InputValidationException;
import com.example.order.exceptions.ResourceNotFoundException;
import com.example.order.repository.OrderRepository;
import com.example.order.repository.ProductOrderVendorRepository;
import com.example.order.repository.ProductRepository;
import com.example.order.repository.VendorRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private VendorRepository vendorRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOrderVendorRepository productOrderVendorRepository;


    public Order createOrder(Order order) throws InputValidationException {
        if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
            throw new InputValidationException();
        }

        order.setOrderDate(LocalDate.now());
        List<Product> products = productService.saveAllProducts(order.getOrderProducts());
        Order createdOrder = orderRepository.save(order);

        //insert into order_product_vendors table
        createOrderProductVendorEntities(order, products);

        return createdOrder;
    }

    private void createOrderProductVendorEntities(Order order, List<Product> products) {
        List<ProductOrderVendor> productOrderVendors = createProductOrderVendorMapping(order, products);
        productOrderVendorRepository.saveAll(productOrderVendors);
    }

    public OrderResponse updateOrder(Order order) throws ResourceNotFoundException {
        orderRepository.findById(order.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + order.getId()));

        //delete the entries in order product vendor mapping table
        productOrderVendorRepository.deleteProductOrderVendorMappingByOrderId(order.getId());

        List<Product> products = productService.saveAllProducts(order.getOrderProducts());

        order.setModifiedDate(LocalDateTime.now());
        Order createdOrder = orderRepository.save(order);

        //insert into order_product_vendors table
        createOrderProductVendorEntities(createdOrder, products);
        return getOrder(createdOrder.getId());
    }

    private List<ProductOrderVendor> createProductOrderVendorMapping(Order order, List<Product> products) {

        List<ProductOrderVendor> productOrderVendors = new ArrayList<>();
        products.forEach(product -> {
            if (product.getVendors() != null && !product.getVendors().isEmpty()) {
                product.getVendors().forEach(vendor -> {
                    ProductOrderVendor productOrderVendor = new ProductOrderVendor(order, product, vendor);
                    productOrderVendors.add(productOrderVendor);
                });
            } else {
                ProductOrderVendor productOrderVendor = new ProductOrderVendor(order, product, null);
                productOrderVendors.add(productOrderVendor);
            }
        });
        return productOrderVendors;
    }

    public OrderResponse getOrder(long orderId) throws ResourceNotFoundException {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));

        List<ProductOrderVendor> productOrderVendors = productOrderVendorRepository.getProductOrderVendorsByOrderId(orderId);

        Map<Long, List<ProductOrderVendor>> orderVendorGroupByProduct = productOrderVendors.stream()
            .collect(Collectors.groupingBy(productOrderVendor -> productOrderVendor.getProductId()));

        List<Product> products = orderVendorGroupByProduct.entrySet()
            .stream()
            .map(entry -> {
                Product product = productRepository.getById(entry.getKey());
                Set<Vendor> vendors = entry.getValue().stream()
                    .map(mapping -> mapping.getVendorId())
                    .filter(vendorId -> vendorId != 0)
                    .map(vendorId -> vendorRepository.getById(vendorId))
                    .collect(Collectors.toSet());
                product.setVendors(vendors);
                return product;
            })
            .collect(Collectors.toList());

        OrderResponse orderResponse = new OrderResponse(order, products);
        return orderResponse;
    }

    public OrderResponse addProductToOrder(long orderId, List<Product> products) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));

        productService.saveAllProducts(products);
        order.setModifiedDate(LocalDateTime.now());
        orderRepository.save(order);

        //insert into order_product_vendors table
        createOrderProductVendorEntities(order, products);
        return getOrder(orderId);
    }

    public OrderResponse removeProductFromOrder(long orderId, long productId) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));

        productOrderVendorRepository.deleteProductOrderVendorsByOrderIdAndProductId(orderId, productId);
        order.setModifiedDate(LocalDateTime.now());
        orderRepository.save(order);
        return getOrder(orderId);
    }


    public void deleteOrder(long orderId) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));

        productOrderVendorRepository.deleteProductOrderVendorMappingByOrderId(orderId);
        orderRepository.delete(order);
    }

    public OrderResponse addVendorToExistingOrderProduct(long orderId, long productId, Vendor vendor) throws ResourceNotFoundException {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));
        Product product = productService.addVendor(productId, vendor);

        productOrderVendorRepository
            .updateVendorMappingEntities(orderId, productId, vendor.getId(), 0, vendor.getQty());

        order.setModifiedDate(LocalDateTime.now());
        orderRepository.save(order);
        return getOrder(orderId);
    }


    public OrderResponse removeVendorFromExistingOrderProduct(long orderId, long productId, long vendorId) throws ResourceNotFoundException {
        orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order Not Found with id = " + orderId));

        productService.getProduct(productId);
        vendorRepository.findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id = " + vendorId));

        productOrderVendorRepository.updateVendorMappingEntities(orderId, productId, 0, vendorId, 0);

        return getOrder(orderId);

    }

}
