package com.example.demo.service;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.model.Address;

import java.util.List;

public interface AddressService {
    long addAddress(AddressRequest request);
    Address getAddress(Long id);
    List<Address> getAllAddress();
    long updateAddress(Long id, AddressRequest request);
    long deleteAddress(Long id);
}
