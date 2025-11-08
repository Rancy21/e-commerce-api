package com.larr.app.e_commerce.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.security.jwt.JwtUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom success handler for OAuth2 authentication.
 * 
 * This handler is called after successful OAuth2 login (Google, GitHub).
 * 
 * Responsibilities:
 * 1. Extract user information from OAuth2 authentication
 * 2. Generate JWT token for the OAuth2 user
 * 3. Set JWT token as HTTP-only cookie
 * 4. Redirect to frontend dashboard
 * 
 * This ensures OAuth2 users get JWT tokens just like email/password users.
 * 
 * @Component marks this as a Spring-managed bean
 */

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    // utility for generating and managing tokens
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Handle successful OAuth2 authentication.
     * 
     * Flow:
     * 1. User logs in with Google/GitHub
     * 2. OAuth2 authentication completes
     * 3. This method is called
     * 4. Generate JWT token for the user
     * 5. Set token as cookie
     * 6. Redirect to dashboard
     * 
     * @param request        HTTP request
     * @param response       HTTP response
     * @param authentication Spring Security authentication object containing OAuth2
     *                       user
     * @throws IOException      if redirect fails
     * @throws ServletException if request handling fails
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // Extract our CustomOAuth2User from authentication
        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oauth2User.getUser();

        // Generate JWT token using the user's email
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user.getEmail());

        // Add JWT cookie to response
        response.addHeader("Set-Cookie", jwtCookie.toString());

        // Build redirect URL to frontend
        String targetUrl = UriComponentsBuilder.fromUriString("/index").queryParam("auth", "success").build()
                .toString();

        // Redirect to target URL
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
