package com.healthcare.auth.config;

import com.healthcare.auth.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final UUID patientId;
    private final UUID providerId;

    public AuthenticatedUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();
        this.enabled = user.canLogin();
        this.accountNonLocked = !user.isLocked();
        this.credentialsNonExpired = !user.isMustChangePassword();
        this.roles = user.getRoleNames();
        this.permissions = user.getAllPermissions();
        this.patientId = user.getPatientId();
        this.providerId = user.getProviderId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public boolean hasPermission(String resource, String action) {
        return permissions.contains(resource + ":" + action);
    }

    public boolean isPatient() {
        return patientId != null;
    }

    public boolean isProvider() {
        return providerId != null;
    }
}
