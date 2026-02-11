package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.CreateUserRequest;
import com.healthcare.auth.api.dto.UpdateUserRequest;
import com.healthcare.auth.api.dto.UserResponse;
import com.healthcare.auth.domain.User;
import com.healthcare.common.api.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    Optional<UserResponse> getUserById(UUID id);

    Optional<UserResponse> getUserByUsername(String username);

    Optional<UserResponse> getUserByEmail(String email);

    UserResponse updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    PageResponse<UserResponse> listUsers(Pageable pageable);

    PageResponse<UserResponse> listUsersByRole(String roleName, Pageable pageable);

    UserResponse assignRoles(UUID userId, Set<String> roleNames);

    UserResponse removeRoles(UUID userId, Set<String> roleNames);

    void activateUser(UUID id);

    void deactivateUser(UUID id);

    void suspendUser(UUID id);

    void unlockUser(UUID id);

    void changePassword(UUID id, String currentPassword, String newPassword);

    void resetPassword(UUID id, String newPassword);

    Optional<User> findUserEntityByUsernameOrEmail(String usernameOrEmail);

    boolean hasPermission(UUID userId, String resource, String action);
}
