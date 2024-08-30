package com.example.demo.service.impl;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Permission;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public int addPermission(PermissionRequest request) {
        Permission permission = Permission.builder()
                .name(request.getName())
                .build();
        permissionRepository.save(permission);
        return permission.getId();
    }

    @Override
    public Permission getPermission(int id) {
        return getById(id);
    }

    @Override
    public List<Permission> getAllPermission() {
        return permissionRepository.findAll();
    }

    @Override
    public int updatePermission(int id, PermissionRequest request) {
        Permission permission = getById(id);
        permission.setName(request.getName());
        permissionRepository.save(permission);
        return id;
    }

    @Override
    public int deletePermission(int id) {
        Permission permission = getById(id);
        permissionRepository.delete(permission);
        return id;
    }

    private Permission getById(int id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }
}
