package com.example.demo.service.impl;

import com.example.demo.constant.PermissionName;
import com.example.demo.constant.RoleName;
import com.example.demo.dto.request.RoleRequest;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Permission;
import com.example.demo.model.Role;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public int addRole(RoleRequest request) {
        Role role = Role.builder()
                .name(request.getName())
                .build();
        roleRepository.save(role);
        return role.getId();
    }

    @Override
    public Role getRole(int id) {
        return getById(id);
    }

    @Override
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public int updateRole(int id, RoleRequest request) {
        Role role = getById(id);
        role.setName(request.getName());
        roleRepository.save(role);
        return id;
    }

    @Override
    public int deleteRole(int id) {
        Role role = getById(id);
        roleRepository.delete(role);
        return id;
    }

    @Override
    public int addPermissionToRole(String roleName, String permissionName) {
        Role role = roleRepository.findByName(RoleName.valueOf(roleName))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
        Permission permission = permissionRepository.findByName(PermissionName.valueOf(permissionName))
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        role.savePermission(permission);

        roleRepository.save(role);

        return role.getId();
    }

    private Role getById(int id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }
}