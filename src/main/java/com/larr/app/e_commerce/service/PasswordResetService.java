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

    public void sendResetEmail(String email, String token) {
        String resetLink = "http://localhost:8080/reset-password.html?token=" + token; // reset-password.html must be
                                                                                       // created for this link to work

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("Clik the link to reset your password: " + resetLink
                + "\n\nThis link will expire in 24 hours");

        mailSender.send(message);
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);

        if (resetToken.isEmpty()) {
            return false;
        }

        return resetToken.get().getExpiryDate().isAfter(LocalDateTime.now());
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid Token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token Expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));
        uRepository.save(user);

        // Delete used token
        tokenRepository.delete(resetToken);

    }

}
