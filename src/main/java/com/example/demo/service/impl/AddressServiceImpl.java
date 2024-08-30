package com.example.demo.service.impl;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.model.Address;
import com.example.demo.repository.AddressRepository;
import com.example.demo.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public long addAddress(AddressRequest request) {
        Address address = addressMapper.toAddress(request);
        addressRepository.save(address);
        return address.getId();
    }

    @Override
    public Address getAddress(Long id) {
        return getById(id);
    }

    @Override
    public List<Address> getAllAddress() {
        return addressRepository.findAll();
    }

    @Override
    public long updateAddress(Long id, AddressRequest request) {
        Address address = getById(id);
        addressMapper.updateAddress(address, request);
        addressRepository.save(address);
        return address.getId();
    }

    @Override
    public long deleteAddress(Long id) {
        addressRepository.deleteById(id);
        return id;
    }

    private Address getById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }
}
