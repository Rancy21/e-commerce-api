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
import com.larr.app.e_commerce.service.CustomOAuth2UserService;

import jakarta.servlet.DispatcherType;

/**
 * Main security configuration combining JWT and OAuth2.
 * 
 * This configuration supports TWO authentication methods:
 * 1. Email/Password login with JWT tokens
 * 2. OAuth2 login (Google, GitHub) with JWT tokens
 * 
 * Both methods result in JWT tokens stored in HTTP-only cookies.
 * All subsequent requests use JWT for authentication (stateless).
 * 
 * @Configuration marks this as a Spring configuration class
 * @EnableMethodSecurity enables method-level security (@PreAuthorize)
 */
@Configuration // Marks this as a Spring configuration class
@EnableMethodSecurity // Enables mehtod-level security (eg., @PreAuthorize)
public class WebSecurityConfig {

    // Service for laoding user details from database
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    // custom AuthenticationEntryPoint for handling unauthorized access
    @Autowired
    private AuthEntryPointJwt authEntryPoint;

    // Custom success handler for OAuth2 login that generates JWT tokens.
    @Autowired
    private OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;

    /**
     * Custom failure handler for OAuth2 login errors.
     */
    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    // Define a bean for your custom JWT filter
    @Bean
    public JwtFilter authenticationJwtTokenFilter() {
        return new JwtFilter();
    }

    // Service that processes user data from OAuth2 providers
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    // Define authentication provider bean
    // It tells Spring Security to retrieve user from database and validate password
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

        // Allow request from your frontend server (adjust port as needed)
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

    /**
     * Main security filter chain configuration.
     * 
     * This is where we configure:
     * - URL access rules (public vs authenticated)
     * - JWT filter integration
     * - OAuth2 login configuration
     * - Session management (stateless)
     * 
     * @param http HttpSecurity builder
     * @return SecurityFilterChain configured security chain
     * @throws Exception if configuration fails
     */
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
                        .requestMatchers("/", "/auth.html", "/reset-password.html",
                                "/forgot-password.html", "/login**", "/home.html")
                        .permitAll()
                        // Allow public API endpoints for login/register
                        .requestMatchers("/api/auth/**").permitAll()
                        // OAuth2 authorization endpoints (Google, Github login)
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                        // Allow Spring's error endpoint
                        .requestMatchers("/error").permitAll()
                        // Require authentication for everything else
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        // Custom login page URL
                        .loginPage("/login")

                        // Configure user info enpoint
                        .userInfoEndpoint(userInfo -> userInfo
                                // User our custom service to process OAuth2 user data
                                .userService(customOAuth2UserService))
                        // Use custom success handler that generates JWT
                        .successHandler(oauth2SuccessHandler)
                        // Custom failure Handler
                        .failureHandler(oAuth2AuthenticationFailureHandler));

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