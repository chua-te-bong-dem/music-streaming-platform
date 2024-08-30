package com.example.demo.service;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.model.Permission;

import java.util.List;

public interface PermissionService {
    int addPermission(PermissionRequest request);
    Permission getPermission(int id);
    List<Permission> getAllPermission();
    int updatePermission(int id, PermissionRequest request);
    int deletePermission(int id);
}
