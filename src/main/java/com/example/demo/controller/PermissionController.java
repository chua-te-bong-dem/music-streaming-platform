package com.example.demo.controller;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.dto.response.ResponseData;
import com.example.demo.model.Permission;
import com.example.demo.service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@Tag(name = "Permission Controller")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping("")
    public ResponseData<Integer> addPermission(@Valid @RequestBody PermissionRequest request) {
        return new ResponseData<>(HttpStatus.CREATED.value(),
                "Create permission success",
                permissionService.addPermission(request));
    }

    @GetMapping("/{permissionId}")
    public ResponseData<Permission> getPermission(@PathVariable @Min(1) int permissionId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get permission success",
                permissionService.getPermission(permissionId));
    }

    @GetMapping("/all")
    public ResponseData<List<Permission>> getAllPermissions() {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Get all permissions success",
                permissionService.getAllPermission());
    }

    @PutMapping("/{permissionId}")
    public ResponseData<Integer> updatePermission(@PathVariable @Min(1) int permissionId,
                                                  @Valid @RequestBody PermissionRequest request) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Update permission success",
                permissionService.updatePermission(permissionId, request));
    }

    @DeleteMapping("/{permissionId}")
    public ResponseData<Integer> deletePermission(@PathVariable @Min(1) int permissionId) {
        return new ResponseData<>(HttpStatus.OK.value(),
                "Delete permission success",
                permissionService.deletePermission(permissionId));
    }
}
