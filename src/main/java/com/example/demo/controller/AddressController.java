package com.example.demo.controller;

import com.example.demo.dto.request.AddressRequest;
import com.example.demo.dto.response.ResponseData;
import com.example.demo.model.Address;
import com.example.demo.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@Tag(name = "Address Controller")
public class AddressController {
    private final AddressService addressService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> addAddress(@Valid @RequestBody AddressRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(),
                "Create address success",
                addressService.addAddress(request));
    }

    @GetMapping("/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Address> getAddress(@PathVariable @Min(1) long addressId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get address success",
                addressService.getAddress(addressId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<List<Address>> getAllAddress() {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get all address success",
                addressService.getAllAddress());
    }

    @PutMapping("/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> updateAddress(@PathVariable @Min(1) long addressId,
                                         @Valid @RequestBody AddressRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Update address success",
                addressService.updateAddress(addressId, request));
    }

    @DeleteMapping("/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Long> deleteAddress(@PathVariable @Min(1) long addressId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Delete address success",
                addressService.deleteAddress(addressId));
    }
}
