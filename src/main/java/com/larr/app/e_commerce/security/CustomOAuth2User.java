package com.larr.app.e_commerce.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.larr.app.e_commerce.model.User;

/**
 * Custom OAuth2User implementation that also implements UserDetails.
 * 
 * This dual implementation allows the same user object to work with:
 * 1. OAuth2 authentication (implements OAuth2User)
 * 2. JWT authentication (implements UserDetails)
 * 
 * This is the bridge between OAuth2 and JWT systems.
 */
public class CustomOAuth2User implements OAuth2User, UserDetails {
    private User user;

    /**
     * Attributes from the OAuth2 provider (Google, GitHub, etc.).
     * Contains raw data like: {sub: "123", email: "user@gmail.com", name: "John"}
     */
    private Map<String, Object> attributes;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // Get user's authorities (roles/permissions)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return user.getFullname();
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

}
