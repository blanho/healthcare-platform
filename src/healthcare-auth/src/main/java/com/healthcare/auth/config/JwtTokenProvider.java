package com.healthcare.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.accessTokenExpiration());

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        claims.put("userId", user.getId().toString());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles());
        claims.put("permissions", user.getPermissions());

        if (user.getPatientId() != null) {
            claims.put("patientId", user.getPatientId().toString());
        }
        if (user.getProviderId() != null) {
            claims.put("providerId", user.getProviderId().toString());
        }

        return Jwts.builder()
            .claims(claims)
            .subject(user.getUsername())
            .issuer(jwtProperties.issuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey())
            .compact();
    }

    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(jwtProperties.refreshTokenExpiration());

        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("userId", userId.toString());
        claims.put("tokenId", UUID.randomUUID().toString());

        return Jwts.builder()
            .claims(claims)
            .subject(userId.toString())
            .issuer(jwtProperties.issuer())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(getSigningKey())
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SignatureException e) {
            log.warn("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty");
        }
        return false;
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public UUID getUserId(String token) {
        String userId = getClaims(token).get("userId", String.class);
        return UUID.fromString(userId);
    }

    @SuppressWarnings("unchecked")
    public Set<String> getRoles(String token) {
        return Set.copyOf(getClaims(token).get("roles", java.util.List.class));
    }

    @SuppressWarnings("unchecked")
    public Set<String> getPermissions(String token) {
        return Set.copyOf(getClaims(token).get("permissions", java.util.List.class));
    }

    public String getTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }

    public boolean isAccessToken(String token) {
        return "access".equals(getTokenType(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(getTokenType(token));
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.accessTokenExpiration();
    }

    public long getRefreshTokenExpirationSeconds() {
        return jwtProperties.refreshTokenExpiration();
    }

    public Instant getRefreshTokenExpirationInstant() {
        return Instant.now().plusSeconds(jwtProperties.refreshTokenExpiration());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .requireIssuer(jwtProperties.issuer())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.secret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
