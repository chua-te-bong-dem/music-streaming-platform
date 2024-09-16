package com.example.demo.controller;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.dto.response.ResponseData;
import com.example.demo.model.Role;
import com.example.demo.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "Role Controller")
public class RoleController {
    private final RoleService roleService;

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Integer> addRole(@Valid @RequestBody RoleRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(),
                "Create role success",
                roleService.addRole(request));
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Role> getRole(@PathVariable @Min(1) int roleId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get role success",
                roleService.getRole(roleId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<List<Role>> getAllRoles() {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get all roles success",
                roleService.getAllRole());
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Integer> updateRole(@PathVariable @Min(1) int roleId,
                                            @Valid @RequestBody RoleRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Update role success",
                roleService.updateRole(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Integer> deleteRole(@PathVariable @Min(1) int roleId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Delete role success",
                roleService.deleteRole(roleId));
    }

    @PostMapping("/add-permission")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseData<Integer> addPermissionToRole(@RequestParam String roleName,
                                                     @RequestParam String permissionName) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Add permission to role success",
                roleService.addPermissionToRole(roleName, permissionName));
    }

}
