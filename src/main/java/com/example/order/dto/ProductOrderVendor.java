package com.example.order.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product_order_vendors",
    indexes = @Index(name = "order_prod_vendor_unique_indx",
        columnList = "order_id , product_id, vendor_id", unique = true))
public class ProductOrderVendor implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "product_id", nullable = false)
    private long productId;
    @Column(name = "vendor_id")
    private long vendorId;
    @Column(name = "order_id", nullable = false)
    private long orderId;

    @Column(name = "quantity", nullable = true)
    private int qty;

    public ProductOrderVendor(Order order, Product product, Vendor vendor) {
        setOrderId(order.getId());
        setProductId(product.getId());
        if (vendor != null) {
            setVendorId(vendor.getId());
            setQty(vendor.getQty());
        }
    }

    public ProductOrderVendor() {

    }
}
