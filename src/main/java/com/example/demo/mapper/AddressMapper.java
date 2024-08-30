package com.example.demo.mapper;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.model.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    Address toAddress(AddressRequest request);
    void updateAddress(@MappingTarget Address address, AddressRequest request);
}
