package com.example.order.repository;

import com.example.order.dto.ProductOrderVendor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOrderVendorRepository extends JpaRepository<ProductOrderVendor, Long> {

    List<ProductOrderVendor> getProductOrderVendorsByOrderId(long orderId);

    Long deleteProductOrderVendorsByOrderIdAndProductId(long orderId, long productId);

    @Modifying
    @Query("delete from ProductOrderVendor p where p.orderId = :orderId")
    void deleteProductOrderVendorMappingByOrderId(@Param("orderId") long orderId);


    @Modifying
    @Query("update ProductOrderVendor p set p.vendorId = :updatedVendorId , p.qty = :qty " +
        "where p.orderId = :orderId " +
        "and p.productId = :productId " +
        "and p.vendorId = :paramVendorId ")
    void updateVendorMappingEntities(@Param("orderId") long orderId,
                                     @Param("productId") long productId,
                                     @Param("updatedVendorId") long updatedVendorId,
                                     @Param("paramVendorId") long paramVendorId,
                                     @Param("qty") int qty);


}
