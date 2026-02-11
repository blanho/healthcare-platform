package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.CreateUserRequest;
import com.healthcare.auth.api.dto.UpdateUserRequest;
import com.healthcare.auth.api.dto.UserResponse;
import com.healthcare.auth.domain.Role;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.exception.DuplicateUserException;
import com.healthcare.auth.exception.UserNotFoundException;
import com.healthcare.auth.repository.RoleRepository;
import com.healthcare.auth.repository.UserRepository;
import com.healthcare.common.api.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creating user");

        validateUniqueUsername(request.username());
        validateUniqueEmail(request.email());

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .build();

        if (request.emailVerified()) {
            user.verifyEmail();
        }

        if (request.mustChangePassword()) {
            user.requirePasswordChange();
        }

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.roles());
            roles.forEach(user::addRole);
        } else {

            roleRepository.findByName(Role.PATIENT).ifPresent(user::addRole);
        }

        user = userRepository.save(user);
        log.info("Created user with ID: {}", user.getId());

        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
            .filter(u -> !u.isDeleted())
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .filter(u -> !u.isDeleted())
            .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .filter(u -> !u.isDeleted())
            .map(this::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.debug("Updating user with ID: {}", id);

        User user = findUserOrThrow(id);

        if (request.email() != null && !request.email().equals(user.getEmail())) {
            validateUniqueEmail(request.email());
            user.setEmail(request.email());
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        user = userRepository.save(user);
        log.info("Updated user with ID: {}", id);

        return toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Deleting user with ID: {}", id);

        User user = findUserOrThrow(id);
        user.markAsDeleted();
        user.deactivate();

        userRepository.save(user);
        log.info("Soft deleted user with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsers(Pageable pageable) {
        log.debug("Listing users with pageable: {}", pageable);

        Page<User> page = userRepository.findByDeletedFalse(pageable);
        Page<UserResponse> responsePage = page.map(this::toResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> listUsersByRole(String roleName, Pageable pageable) {
        log.debug("Listing users by role: {}", roleName);

        Page<User> page = userRepository.findByRoleName(roleName, pageable);
        Page<UserResponse> responsePage = page.map(this::toResponse);

        return PageResponse.from(responsePage);
    }

    @Override
    @Transactional
    public UserResponse assignRoles(UUID userId, Set<String> roleNames) {
        log.debug("Assigning roles {} to user: {}", roleNames, userId);

        User user = findUserOrThrow(userId);
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        roles.forEach(user::addRole);

        user = userRepository.save(user);
        log.info("Assigned roles {} to user: {}", roleNames, userId);

        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse removeRoles(UUID userId, Set<String> roleNames) {
        log.debug("Removing roles {} from user: {}", roleNames, userId);

        User user = findUserOrThrow(userId);
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        roles.forEach(user::removeRole);

        user = userRepository.save(user);
        log.info("Removed roles {} from user: {}", roleNames, userId);

        return toResponse(user);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        log.debug("Activating user: {}", id);

        User user = findUserOrThrow(id);
        user.activate();
        userRepository.save(user);

        log.info("Activated user: {}", id);
    }

    @Override
    @Transactional
    public void deactivateUser(UUID id) {
        log.debug("Deactivating user: {}", id);

        User user = findUserOrThrow(id);
        user.deactivate();
        userRepository.save(user);

        log.info("Deactivated user: {}", id);
    }

    @Override
    @Transactional
    public void suspendUser(UUID id) {
        log.debug("Suspending user: {}", id);

        User user = findUserOrThrow(id);
        user.suspend();
        userRepository.save(user);

        log.info("Suspended user: {}", id);
    }

    @Override
    @Transactional
    public void unlockUser(UUID id) {
        log.debug("Unlocking user: {}", id);

        User user = findUserOrThrow(id);
        user.unlock();
        userRepository.save(user);

        log.info("Unlocked user: {}", id);
    }

    @Override
    @Transactional
    public void changePassword(UUID id, String currentPassword, String newPassword) {
        log.debug("Changing password for user: {}", id);

        User user = findUserOrThrow(id);

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new com.healthcare.auth.exception.AuthenticationException("Current password is incorrect");
        }

        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Changed password for user: {}", id);
    }

    @Override
    @Transactional
    public void resetPassword(UUID id, String newPassword) {
        log.debug("Resetting password for user: {}", id);

        User user = findUserOrThrow(id);
        user.changePassword(passwordEncoder.encode(newPassword));
        user.requirePasswordChange();
        userRepository.save(user);

        log.info("Reset password for user: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserEntityByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, String resource, String action) {
        return userRepository.findById(userId)
            .map(user -> user.hasPermission(resource, action))
            .orElse(false);
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
            .filter(u -> !u.isDeleted())
            .orElseThrow(() -> UserNotFoundException.byId(id));
    }

    private void validateUniqueUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw DuplicateUserException.usernameExists(username);
        }
    }

    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateUserException.emailExists(email);
        }
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getStatus().name(),
            user.isEmailVerified(),
            user.isMfaEnabled(),
            user.getPatientId(),
            user.getProviderId(),
            user.getRoleNames(),
            user.getAllPermissions(),
            user.getLastLoginAt(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
