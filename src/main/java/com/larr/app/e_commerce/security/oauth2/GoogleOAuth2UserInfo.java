package com.larr.app.e_commerce.security.oauth2;

import java.util.Map;

/**
 * OAuth2UserInfo implementation for Google.
 * 
 * Extracts user information from Google's OAuth2 userinfo response.
 * 
 * Google's userinfo endpoint returns data in this format:
 * {
 * "sub": "1234567890", // User ID
 * "name": "John Doe", // Full name
 * "given_name": "John", // First name
 * "family_name": "Doe", // Last name
 * "picture": "https://...", // Profile picture URL
 * "email": "john@gmail.com", // Email
 * "email_verified": true, // Email verification status
 * "locale": "en" // User's locale
 * }
 * 
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    // Get Google's unique identifier
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    // Get user's full name from Google
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    // Get user's email from Google
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    // Get user's profile picture URL from Google
    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

}
