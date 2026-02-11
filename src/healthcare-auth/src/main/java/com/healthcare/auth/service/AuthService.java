package com.healthcare.auth.service;

import com.healthcare.auth.api.dto.LoginRequest;
import com.healthcare.auth.api.dto.RegisterRequest;
import com.healthcare.auth.api.dto.TokenResponse;

public interface AuthService {

    TokenResponse login(LoginRequest request, String ipAddress, String userAgent);

    TokenResponse register(RegisterRequest request);

    TokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);

    void logoutAllDevices(java.util.UUID userId);

    boolean validateToken(String token);
}
