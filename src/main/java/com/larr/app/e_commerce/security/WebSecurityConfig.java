package com.larr.app.e_commerce.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.larr.app.e_commerce.security.jwt.JwtFilter;
import com.larr.app.e_commerce.security.service.UserDetailsServiceImpl;

import jakarta.servlet.DispatcherType;

@Configuration // Marks this as a Spring configuration class
@EnableMethodSecurity // Enables mehtod-level security (eg., @PreAuthorize)
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // custom AuthenticationEntryPoint for handling unauthorized access
    @Autowired
    private AuthEntryPointJwt authEntryPoint;

    // Define a bean for your custom JWT filter
    @Bean
    public JwtFilter authenticationJwtTokenFilter() {
        return new JwtFilter();
    }

    // Define authentication provider bean
    // It tells Spring Security retrive user from database and validate password
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        // Set the password encoder
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Define password encoder bean for password hashing and validation
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Expose AuthenticationManager as a bean
    // Needed for performing authentication manually (e.g, in UserController)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authconfig) throws Exception {
        return authconfig.getAuthenticationManager();
    }

    // Configure Cross-Origin Resource Sharing (CORS) for frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow request from your frontend server
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // Allow sending cookies / credentials
        configuration.setAllowCredentials(true);

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));

        // Allow specific headers in requests
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));

        // Apply the configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Define the main security filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // disable CSRF as it is not needed for stateless API
                // use custom 401 handler
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
                // no HTTP session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow internal dispatcher types( error and forwared requests))
                        .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()
                        // Allow public resources and routes
                        .requestMatchers("/", "/index.html", "/auth.html").permitAll()
                        // Allow public API endpoints for login/register
                        .requestMatchers("/api/auth/**").permitAll()
                        // Allow Spring's error endpoint
                        .requestMatchers("/error").permitAll()
                        // Require authentication for everything else
                        .anyRequest().authenticated());

        // Register the authentication provider for user loading and password check
        http.authenticationProvider(authenticationProvider());

        // Add JWT filter before Spring's built-in authentication filter
        // Ensure that JWT token are proccessed before email/password
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Enable and apply CORS configuration
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Build and return the configured security chain
        return http.build();
    }

}