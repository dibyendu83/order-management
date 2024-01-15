package com.example.order.service;

import com.example.order.dto.Product;
import com.example.order.dto.Vendor;
import com.example.order.exceptions.ResourceNotFoundException;
import com.example.order.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    public Vendor addVendorToProduct(Product product, Vendor vendor) {
        vendor.setProduct(product);
        return vendorRepository.save(vendor);
    }

    public void removeVendorFromProduct(long vendorId) throws ResourceNotFoundException {
        Vendor vendor = getVendor(vendorId);
        vendorRepository.delete(vendor);
    }

    private Vendor getVendor(long vendorId) throws ResourceNotFoundException {
        return vendorRepository
            .findById(vendorId)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found Vendor with id = " + vendorId));
    }
}
