package com.example.demo.repository;

import com.example.demo.constant.PermissionName;
import com.example.demo.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(PermissionName name);
}
