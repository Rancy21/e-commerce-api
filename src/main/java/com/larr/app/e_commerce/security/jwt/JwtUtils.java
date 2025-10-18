package com.larr.app.e_commerce.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationsMs}")
    private int jwtExp;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Create a secret key for signing/verifying JWTs
    private SecretKey Key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // Create a signed JWT for a user
    public String generateJwtToken(String userEmail) {
        return Jwts.builder().subject(userEmail).issuedAt(new Date())
                .expiration(new Date((new Date().getTime() + jwtExp))).signWith(Key()).compact();
    }

    // Extracts JWT from incoming request
    public String getJwtFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "jwt");
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    // Creates an HTTP-only cookie with JWT
    public ResponseCookie generateJwtCookie(String userEmail) {
        String jwt = generateJwtToken(userEmail);
        return ResponseCookie.from("jwt", jwt).path("/").maxAge(jwtExp / 1000).httpOnly(true).secure(true)
                .sameSite("Lax").build();
    }

    // Clears the JWT cookie
    public ResponseCookie generateCleanJwtCookie() {
        return ResponseCookie.from("jwt", "").maxAge(0).httpOnly(true).secure(true).sameSite("Lax").build();
    }

    // Extracts user email from a token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser().verifyWith(Key()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // Verifies token’s integrity and validity
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(Key()).build().parse(authToken);
            return true;
        } catch (Exception e) {
            logger.error("Validation Token Error: {}", e.getMessage());
        }

        return false;
    }
}
