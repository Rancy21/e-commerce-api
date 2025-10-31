package com.larr.app.e_commerce.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.PasswordResetToken;
import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.repository.PasswordResetRepository;
import com.larr.app.e_commerce.repository.UserRepository;

/**
 * Service class for handling password reset functionality.
 * 
 * Responsibilities:
 * 1. Generate password reset tokens
 * 2. Send reset emails to users
 * 3. Validate reset tokens
 * 4. Update user passwords
 */
@Service
public class PasswordResetService {
    @Autowired
    private UserRepository uRepository;

    @Autowired
    private PasswordResetRepository tokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void CreatePasswordResetToken(String email) {
        User user = uRepository.findUserByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Delete any existing token for this user
        tokenRepository.deleteByUser(user);

        // Generate new token
        String token = UUID.randomUUID().toString();

        // Create token entity
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(24));

        // Save token
        tokenRepository.save(resetToken);

        // Send email
        sendResetEmail(email, token);
    }

    // Send password reset email to user
    public void sendResetEmail(String email, String token) {
        // build the reset link
        String resetLink = "http://localhost:8080/reset-password.html?token=" + token; // reset-password.html must be
                                                                                       // created for this link to work

        // Create email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Clik the link to reset your password: " + resetLink
                + "\n\nThis link will expire in 24 hours");

        // send the email
        mailSender.send(message);
    }

    // Validate if a reset token is valid and not expired
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return false;
        }

        return resetToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    // Reset user's password using a valid token.
    public void resetPassword(String token, String newPassword) {
        // find the token in the database
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        // Check if the token has expired
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token Expired");
        }

        // Get the user associated with this token
        User user = resetToken.getUser();

        // Hash the new password
        user.setPassword(passwordEncoder.encode(newPassword));

        // Save the updated user in the database
        uRepository.save(user);

        // Delete used token
        tokenRepository.delete(resetToken);

    }

}
