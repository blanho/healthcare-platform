package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.LoginRequest;
import com.healthcare.auth.api.dto.RegisterRequest;
import com.healthcare.auth.api.dto.TokenResponse;
import com.healthcare.auth.config.AuthenticatedUser;
import com.healthcare.auth.config.JwtTokenProvider;
import com.healthcare.auth.domain.RefreshToken;
import com.healthcare.auth.domain.Role;
import com.healthcare.auth.domain.User;
import com.healthcare.auth.exception.AuthenticationException;
import com.healthcare.auth.exception.DuplicateUserException;
import com.healthcare.auth.repository.RefreshTokenRepository;
import com.healthcare.auth.repository.RoleRepository;
import com.healthcare.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public TokenResponse login(LoginRequest request, String ipAddress, String userAgent) {
        log.debug("Processing login attempt");

        User user = userRepository.findByUsernameOrEmail(request.usernameOrEmail())
            .orElseThrow(AuthenticationException::invalidCredentials);

        if (user.isLocked()) {
            throw AuthenticationException.accountLocked();
        }

        if (!user.canLogin()) {
            throw AuthenticationException.accountInactive();
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.recordFailedLogin();
            User savedUser = userRepository.save(user);

            savedUser.getDomainEvents().forEach(eventPublisher::publishEvent);
            savedUser.clearDomainEvents();
            throw AuthenticationException.invalidCredentials();
        }

        user.recordSuccessfulLogin(ipAddress);
        User savedUser = userRepository.save(user);

        savedUser.getDomainEvents().forEach(eventPublisher::publishEvent);
        savedUser.clearDomainEvents();

        return generateTokens(savedUser, userAgent, ipAddress);
    }

    @Override
    @Transactional
    public TokenResponse register(RegisterRequest request) {
        log.debug("Processing registration");

        if (userRepository.existsByUsername(request.username())) {
            throw DuplicateUserException.usernameExists(request.username());
        }
        if (userRepository.existsByEmail(request.email())) {
            throw DuplicateUserException.emailExists(request.email());
        }

        User user = User.builder()
            .username(request.username())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .build();

        if (request.roles() != null && !request.roles().isEmpty()) {
            Set<Role> roles = roleRepository.findByNameIn(request.roles());
            roles.forEach(user::addRole);
        } else {

            roleRepository.findByName(Role.PATIENT).ifPresent(user::addRole);
        }

        user = userRepository.save(user);
        log.info("Registered new user with ID: {}", user.getId());

        return generateTokens(user, null, null);
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(String refreshToken) {
        log.debug("Refreshing token");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw AuthenticationException.refreshTokenInvalid();
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw AuthenticationException.refreshTokenInvalid();
        }

        String tokenHash = hashToken(refreshToken);
        RefreshToken storedToken = refreshTokenRepository
            .findValidToken(tokenHash, Instant.now())
            .orElseThrow(AuthenticationException::refreshTokenInvalid);

        storedToken.revoke();
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();

        if (!user.canLogin()) {
            throw AuthenticationException.accountInactive();
        }

        return generateTokens(user, storedToken.getUserAgent(), storedToken.getIpAddress());
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        log.debug("Logging out user");

        String tokenHash = hashToken(refreshToken);
        refreshTokenRepository.findByTokenHash(tokenHash)
            .ifPresent(token -> {
                token.revoke();
                refreshTokenRepository.save(token);
                log.info("Revoked refresh token for user: {}", token.getUser().getId());
            });
    }

    @Override
    @Transactional
    public void logoutAllDevices(UUID userId) {
        log.debug("Logging out user from all devices: {}", userId);

        int revokedCount = refreshTokenRepository.revokeAllUserTokens(userId, Instant.now());
        log.info("Revoked {} refresh tokens for user: {}", revokedCount, userId);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token) && jwtTokenProvider.isAccessToken(token);
    }

    private TokenResponse generateTokens(User user, String userAgent, String ipAddress) {
        AuthenticatedUser authUser = new AuthenticatedUser(user);

        String accessToken = jwtTokenProvider.generateAccessToken(authUser);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        RefreshToken tokenEntity = new RefreshToken(
            user,
            hashToken(refreshToken),
            jwtTokenProvider.getRefreshTokenExpirationInstant(),
            userAgent,
            ipAddress
        );
        refreshTokenRepository.save(tokenEntity);

        return TokenResponse.of(
            accessToken,
            refreshToken,
            jwtTokenProvider.getAccessTokenExpirationSeconds(),
            user.getRoleNames(),
            user.getAllPermissions()
        );
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
