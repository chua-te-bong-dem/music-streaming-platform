package com.example.demo.service;

import com.example.demo.dto.request.RoleRequest;
import com.example.demo.model.Role;

import java.util.List;

public interface RoleService {
    int addRole(RoleRequest request);
    Role getRole(int id);
    List<Role> getAllRole();
    int updateRole(int id, RoleRequest request);
    int deleteRole(int id);
    int addPermissionToRole(String roleName, String permissionName);
}
