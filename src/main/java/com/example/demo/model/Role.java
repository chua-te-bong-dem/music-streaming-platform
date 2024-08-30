package com.example.demo.model;

import com.example.demo.constant.RoleEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true)
    @Enumerated(EnumType.STRING)
    private RoleEnum name;

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private Set<Permission> permissions = new HashSet<>();

    public void savePermission(Permission permission) {
        if (permission != null) {
            if (this.permissions == null) {
                this.permissions = new HashSet<>();
            }
            if (permission.getRoles() == null) {
                permission.setRoles(new HashSet<>());
            }
            this.permissions.add(permission);
            permission.getRoles().add(this);
        }
    }
}
