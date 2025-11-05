package com.larr.app.e_commerce.security.service;

// import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Fetch user from database
        User user = repo.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Convert the user's role into GranteAuthority
        List<GrantedAuthority> authorities = Collections
                .singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        String password = user.getPassword();

        // For OAuth2 users (Google, Github), password is null
        if (password == null || password.isEmpty()) {
            // We use a placeholder that will never match any input
            password = "{noop}OAUTH2_USER_NO_PASSWORD";
        }

        // Return Spring Security's User object
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(password).authorities(authorities).build();
    }

}
