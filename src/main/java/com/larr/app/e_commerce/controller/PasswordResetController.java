package com.larr.app.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.service.PasswordResetService;

/**
 * REST controller for handling password reset operations.
 * Provides endpoints for requesting a password reset, validating reset tokens,
 * and completing the password reset process.
 */
@RestController
@RequestMapping(value = "/api/auth")
public class PasswordResetController {
    @Autowired
    private PasswordResetService passwordResetService;

    /*
     * Initiate password reset. Trigger the creation of a reset token, sending a
     * reset link via email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        try {
            passwordResetService.CreatePasswordResetToken(email);
            return ResponseEntity.ok("Reset link sent to email");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Ckecks if the provided token is valid and not expired
    @GetMapping(value = "/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = passwordResetService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }

    /**
     * Endpoint to complete the password reset.
     * Uses the provided token to verify the request and updates the user's password
     * with the new value.
     */
    @PostMapping(value = "/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

}
