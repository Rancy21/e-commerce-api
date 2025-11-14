package com.larr.app.e_commerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Controller for authentication-related endpoints.
 *
 * Handles:
 * - Login page display
 * - Home page after successful login
 * - Current user information retrieval
 * - Logout
 *
 * @Controller indicates this is a Spring MVC controller
 *             (returns view names, not JSON responses)
 */

@Controller
public class AuthController {
    @Autowired
    UserService userService;

    // Display the login page
    @GetMapping("/login")
    public String login() {
        return "auth.html"; // return auht.html
    }

    // Display page after successful login
    @GetMapping("/index")
    public String index() {
        return "index.html";
    }

    // REST endpoint to get current authenticatied user's information.
    @GetMapping("/api/user/me")
    @ResponseBody // this tells Spring to return JSON, not a view name
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        // Extract user from authentication
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.getUser(userDetails.getUsername()).get();

        // Return user as JSON
        return ResponseEntity.ok(user);

    }

    @GetMapping("/")
    public String home() {
        return "home.html"; // return home.html
    }

}