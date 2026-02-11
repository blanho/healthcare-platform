package com.healthcare.auth.repository;

import com.healthcare.auth.domain.User;
import com.healthcare.auth.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    @Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.deleted = false")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.status = :status AND u.deleted = false")
    Page<User> findByStatus(@Param("status") UserStatus status, Pageable pageable);

    Page<User> findByDeletedFalse(Pageable pageable);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deleted = false")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    Optional<User> findByPatientId(UUID patientId);

    Optional<User> findByProviderId(UUID providerId);

    @Query("SELECT u FROM User u WHERE u.lockedUntil IS NOT NULL AND u.lockedUntil < :now")
    Page<User> findLockedUsersPastLockout(@Param("now") Instant now, Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = NULL, u.failedLoginAttempts = 0 WHERE u.lockedUntil < :now")
    int unlockUsersPastLockout(@Param("now") Instant now);

    long countByStatus(UserStatus status);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.name = :roleName AND u.deleted = false")
    long countByRoleName(@Param("roleName") String roleName);
}
