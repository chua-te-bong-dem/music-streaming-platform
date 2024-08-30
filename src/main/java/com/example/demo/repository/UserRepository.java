package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<String> findByEmail(String email);
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Long id);
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.addresses WHERE u.username = :username")
    Optional<User> findByUsernameWithAddresses(@Param("username") String username);
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles LEFT JOIN FETCH u.addresses WHERE u.username = :username")
    Optional<User> findByUsernameWithRolesAndAddresses(@Param("username") String username);
    @Query("SELECT DISTINCT u FROM User u JOIN FETCH u.roles LEFT JOIN FETCH u.addresses WHERE u.id = :id")
    Optional<User> findByIdWithRolesAndAddresses(@Param("id") Long id);
    @Query("SELECT u.id FROM User u")
    List<Long> findAllIds(Pageable pageable);
    @EntityGraph(attributePaths = {"roles", "addresses"})
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN :ids")
    List<User> findAllByIdsAndSort(@Param("ids") List<Long> ids, Sort sort);
    @Query("SELECT u.id FROM User u")
    List<Long> findAllIds();
    @EntityGraph(attributePaths = {"roles", "addresses"})
    @Query("SELECT DISTINCT u FROM User u WHERE u.id IN :ids")
    Page<User> findAllByIdsAndSort(@Param("ids") List<Long> ids, Pageable pageable);
}
