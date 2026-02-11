package com.healthcare.auth.api;

import com.healthcare.auth.api.dto.ChangePasswordRequest;
import com.healthcare.auth.api.dto.CreateUserRequest;
import com.healthcare.auth.api.dto.UpdateUserRequest;
import com.healthcare.auth.api.dto.UserResponse;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.domain.Role;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.service.UserService;
import com.healthcare.common.api.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User account management operations")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user (admin only)")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        log.debug("Creating user");

        UserResponse response = userService.createUser(request);

        return ResponseEntity
            .created(URI.create("/api/v1/users/" + response.id()))
            .body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List users", description = "List all users (admin only)")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        log.debug("Listing users");

        PageResponse<UserResponse> response = userService.listUsers(pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List users by role", description = "List users with specific role (admin only)")
    public ResponseEntity<PageResponse<UserResponse>> listUsersByRole(
            @PathVariable String roleName,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.debug("Listing users by role: {}", roleName);

        PageResponse<UserResponse> response = userService.listUsersByRole(roleName, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Get user", description = "Get user by ID")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        log.debug("Getting user: {}", id);

        UserResponse response = userService.getUserById(id)
            .orElseThrow(() -> UserNotFoundException.byId(id));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    @Operation(summary = "Update user", description = "Update user profile")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.debug("Updating user: {}", id);

        UserResponse response = userService.updateUser(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete user (admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.debug("Deleting user: {}", id);

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign roles", description = "Assign roles to user (admin only)")
    public ResponseEntity<UserResponse> assignRoles(
            @PathVariable UUID id,
            @RequestBody Set<String> roleNames
    ) {
        log.debug("Assigning roles {} to user: {}", roleNames, id);

        UserResponse response = userService.assignRoles(id, roleNames);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove roles", description = "Remove roles from user (admin only)")
    public ResponseEntity<UserResponse> removeRoles(
            @PathVariable UUID id,
            @RequestBody Set<String> roleNames
    ) {
        log.debug("Removing roles {} from user: {}", roleNames, id);

        UserResponse response = userService.removeRoles(id, roleNames);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activate user account (admin only)")
    public ResponseEntity<Void> activateUser(@PathVariable UUID id) {
        log.debug("Activating user: {}", id);

        userService.activateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivate user account (admin only)")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        log.debug("Deactivating user: {}", id);

        userService.deactivateUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Suspend user", description = "Suspend user account (admin only)")
    public ResponseEntity<Void> suspendUser(@PathVariable UUID id) {
        log.debug("Suspending user: {}", id);

        userService.suspendUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unlock user", description = "Unlock locked user account (admin only)")
    public ResponseEntity<Void> unlockUser(@PathVariable UUID id) {
        log.debug("Unlocking user: {}", id);

        userService.unlockUser(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/change-password")
    @PreAuthorize("#id == authentication.principal.id")
    @Operation(summary = "Change password", description = "Change own password")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        log.debug("Changing password for user: {}", id);

        userService.changePassword(id, request.currentPassword(), request.newPassword());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset password", description = "Reset user password (admin only)")
    public ResponseEntity<Void> resetPassword(
            @PathVariable UUID id,
            @RequestParam String newPassword
    ) {
        log.debug("Resetting password for user: {}", id);

        userService.resetPassword(id, newPassword);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get currently authenticated user")
    public ResponseEntity<UserResponse> getCurrentUser(
            @AuthenticationPrincipal AuthenticatedUser currentUser
    ) {
        log.debug("Getting current user: {}", currentUser.getId());

        UserResponse response = userService.getUserById(currentUser.getId())
            .orElseThrow(() -> UserNotFoundException.byId(currentUser.getId()));

        return ResponseEntity.ok(response);
    }
}
