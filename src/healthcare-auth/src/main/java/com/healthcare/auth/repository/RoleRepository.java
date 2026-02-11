package com.healthcare.auth.repository;

import com.healthcare.auth.domain.Role;
import com.healthcare.common.config.RedisCacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    @EntityGraph(attributePaths = {"permissions"})
    @Cacheable(value = RedisCacheConfig.CACHE_ROLES, key = "#name")
    Optional<Role> findByName(String name);

    @EntityGraph(attributePaths = {"permissions"})
    Set<Role> findByNameIn(Set<String> names);

    boolean existsByName(String name);

    @Cacheable(value = RedisCacheConfig.CACHE_ROLES, key = "'system-roles'")
    List<Role> findBySystemRoleTrue();

    List<Role> findBySystemRoleFalse();

    @Query("SELECT r FROM Role r WHERE r.name IN ('ROLE_DOCTOR', 'ROLE_NURSE', 'ROLE_LAB_TECH', 'ROLE_PHARMACIST')")
    List<Role> findProviderRoles();

    @Query("SELECT r FROM Role r JOIN r.permissions p WHERE p.name = :permissionName")
    List<Role> findByPermissionName(String permissionName);
}
