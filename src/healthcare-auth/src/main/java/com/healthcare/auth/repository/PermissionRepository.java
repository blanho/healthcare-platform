package com.healthcare.auth.repository;

import com.healthcare.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByName(String name);

    Set<Permission> findByNameIn(Set<String> names);

    List<Permission> findByResource(String resource);

    Optional<Permission> findByResourceAndAction(String resource, String action);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT p.resource FROM Permission p ORDER BY p.resource")
    List<String> findAllResources();

    @Query("SELECT p.action FROM Permission p WHERE p.resource = :resource ORDER BY p.action")
    List<String> findActionsByResource(String resource);
}
