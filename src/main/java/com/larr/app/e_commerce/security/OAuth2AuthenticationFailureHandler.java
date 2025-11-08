package com.larr.app.e_commerce.security;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom failure handler for OAuth2 authentication errors.
 * 
 * This handler is called when OAuth2 login fails at any stage:
 * - User denies permission
 * - Invalid credentials
 * - Token exchange fails
 * - User info retrieval fails
 * - Email not provided by OAuth2 provider
 * 
 * Responsibilities:
 * 1. Log detailed error information
 * 2. Extract user-friendly error message
 * 3. Redirect to login page with error message
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        // Log detailed error for debugging
        logger.error("==========================================================");
        logger.error("OAuth2 Authentication failed");
        logger.error("Exception Type: {}", exception.getClass().getName());
        logger.error("Exception Message: {}", exception.getMessage());

        // Log the cause if available
        if (exception.getCause() != null) {
            logger.error("Exception Cause: {}", exception.getCause().getClass().getName());
            logger.error("Cause Message: {}", exception.getCause().getMessage());
        }

        // Log the full stack trace for debugging
        logger.error("Stack Trace:", exception);
        logger.error("=============================================================");

        // Extract user-friendly error message
        String errorMessage = getUserFriendlyMessage(exception);

        // Log the final message we're showing to user
        logger.info("User will see message: {}", errorMessage);

        // URL encode the error message to safely pass in query parameter
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // Build redirect URL with error message
        String targetUrl = UriComponentsBuilder.fromUriString("/login")
                .queryParam("error", "oauth2")
                .queryParam("message", encodedError)
                .build()
                .toUriString();

        // Log redirect for debugging
        logger.info("Redirecting to: {}", targetUrl);

        // Redirect user to login page with error
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    /**
     * Extract user-friendly error message from exception.
     * 
     * This method analyzes the exception message, type, and cause
     * to determine what went wrong and return an appropriate
     * user-friendly message.
     * 
     * @param exception The authentication exception
     * @return User-friendly error message
     */
    private String getUserFriendlyMessage(AuthenticationException exception) {

        // Get exception details
        String exceptionType = exception.getClass().getSimpleName();
        String message = exception.getMessage();

        if (message == null) {
            message = "";
        }

        // Convert to lowercase for case-insensitive matching
        String lowerMessage = message.toLowerCase();

        logger.debug("Analyzing exception - Type: {}, Message: {}", exceptionType, message);

        // Access Denied - User cancelled login
        if (containsAny(lowerMessage, "access_denied", "access denied")) {
            return "You cancelled the login process. Please try again if you'd like to login";
        }

        // Invalid Client - Wrong client ID or secret
        if (containsAny(lowerMessage, "unauthorized_client", "unauthorized client", "invalid_client",
                "invalid client")) {
            return "Login service is not properly configured. Please contact support";
        }

        // Invalid Grant - Authorization code expired or invalid
        if (containsAny(lowerMessage, "invalid_grant", "invalid grant", "authorization code")) {
            return "Login session expired or invalid. Please try again";
        }

        // Invalid Request - Malformed request
        if (containsAny(lowerMessage, "invalid_request", "invalid request", "malformed")) {
            return "Invalid login request. Please try again or contact support";
        }

        // Server Error - Provider's server issue
        if (containsAny(lowerMessage, "server_error", "server error", "internal server error", "500", "502", "503")) {
            return "The authentication provider encounter an error. Please try again later.";
        }

        // Temporarily Unavailable - Provider is down
        if (containsAny(lowerMessage, "temporarily_unavailable", "temporarily unavailable", "service unavailable",
                "503")) {
            return "The authentication service is temporarily unavailable. Please try in a few minutes";
        }
        // Invalid Token - Token is invalid
        if (containsAny(lowerMessage, "invalid_token", "invalid token", "token invalid")) {
            return "Authentication token is invalid. Please try logging in again.";
        }

        // Unsupported Response Type
        if (containsAny(lowerMessage, "unsupported_response_type", "unsupported response")) {
            return "Login configuration error. Please contact support.";
        }

        // Invalid Scope
        if (containsAny(lowerMessage, "invalid_scope", "invalid scope")) {
            return "Invalid login permissions requested. Please contact support.";
        }

        // Email not found
        if (containsAny(lowerMessage, "email not found", "email not provided",
                "no email", "missing email")) {
            return "Email not provided by the authentication provider. Please ensure your account has a public email address.";
        }

        // Account disabled
        if (containsAny(lowerMessage, "account has been disabled", "account disabled",
                "inactive", "account not active")) {
            return "Your account has been disabled. Please contact support.";
        }

        // Registration failed
        if (containsAny(lowerMessage, "registration failed", "failed to register",
                "failed to create", "cannot create account")) {
            return "Failed to create your account. Please try again or contact support.";
        }

        // Update failed
        if (containsAny(lowerMessage, "update failed", "failed to update",
                "cannot update")) {
            return "Failed to update your account information. Please try again.";
        }

        // User info error
        if (containsAny(lowerMessage, "user info", "user information", "userinfo",
                "failed to load user")) {
            return "Failed to load user information from the provider. Please try again.";
        }

        // User processing error
        if (containsAny(lowerMessage, "user processing", "processing error",
                "failed to process user")) {
            return "Failed to process user information. Please try again or contact support.";
        }

        // Provider not supported
        if (containsAny(lowerMessage, "not supported", "unsupported provider")) {
            // Return the original message as it's already user-friendly
            return message;
        }
        // ============================================================
        // Check for our custom error messages
        // These are thrown by our CustomOAuth2UserService
        // ============================================================

        // Email not found
        if (containsAny(lowerMessage, "email not found", "email not provided",
                "no email", "missing email")) {
            return "Email not provided by the authentication provider. Please ensure your account has a public email address.";
        }

        // Account disabled
        if (containsAny(lowerMessage, "account has been disabled", "account disabled",
                "inactive", "account not active")) {
            return "Your account has been disabled. Please contact support.";
        }

        // Registration failed
        if (containsAny(lowerMessage, "registration failed", "failed to register",
                "failed to create", "cannot create account")) {
            return "Failed to create your account. Please try again or contact support.";
        }

        // Update failed
        if (containsAny(lowerMessage, "update failed", "failed to update",
                "cannot update")) {
            return "Failed to update your account information. Please try again.";
        }

        // User info error
        if (containsAny(lowerMessage, "user info", "user information", "userinfo",
                "failed to load user")) {
            return "Failed to load user information from the provider. Please try again.";
        }

        // User processing error
        if (containsAny(lowerMessage, "user processing", "processing error",
                "failed to process user")) {
            return "Failed to process user information. Please try again or contact support.";
        }

        // Provider not supported
        if (containsAny(lowerMessage, "not supported", "unsupported provider")) {
            // Return the original message as it's already user-friendly
            return message;
        }

        // ============================================================
        // Check exception cause for additional context
        // ============================================================

        if (exception.getCause() != null) {
            String causeMessage = exception.getCause().getMessage();
            if (causeMessage != null) {
                String lowerCause = causeMessage.toLowerCase();

                // Network errors
                if (containsAny(lowerCause, "connection", "timeout", "unreachable", "network")) {
                    return "Network error occurred. Please check your connection and try again.";
                }

                // Database errors
                if (containsAny(lowerCause, "database", "sql", "jdbc", "connection pool")) {
                    return "System error occurred. Please try again later or contact support.";
                }
            }
        }

        // Check if message looks user-friendly
        if (isUserFriendly(message)) {
            return message;
        }

        // Fallback to generic error message
        return "Authentication failed. Please try again or use a different login method.";

    }

    /**
     * Check if a string contains any given substrings
     * 
     * @param text
     * @param substrings
     * @return true if any substring is found
     */

    private boolean containsAny(String text, String... substrings) {
        for (String substring : substrings) {
            if (text.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a message is user friendly
     * 
     * @param message
     * @return
     */
    private boolean isUserFriendly(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        // Too long
        if (message.length() > 150) {
            return false;
        }

        // Convert to lowercase for checking
        String lower = message.toLowerCase();

        // Contains technical terms
        String[] technicalTerms = {
                "exception", "error", "null", "stack", "trace",
                "java.", "org.", "com.", "springframework",
                "at ", "caused by", "suppressed"
        };

        for (String term : technicalTerms) {
            if (lower.contains(term)) {
                return false;
            }
        }

        // Looks user-friendly
        return true;
    }

}
