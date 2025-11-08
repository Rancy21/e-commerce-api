package com.larr.app.e_commerce.security.oauth2;

import java.util.Map;

/**
 * OAuth2UserInfo implementation for GitHub.
 * 
 * Extracts user information from GitHub's OAuth2 user response.
 * 
 * GitHub's user endpoint returns data in this format:
 * {
 * "id": 12345678, // User ID (Integer)
 * "login": "johndoe", // GitHub username
 * "name": "John Doe", // Full name (can be null)
 * "email": "john@gmail.com", // Email (can be null)
 * "avatar_url": "https://...", // Profile picture URL
 * "bio": "Developer", // User bio
 * "location": "San Francisco", // Location
 * "company": "Acme Inc" // Company
 * }
 * 
 * Important: GitHub email can be null if:
 * - User hasn't set a public email
 * - App didn't request user:email scope
 */

public class GithubOAuth2UserInfo extends OAuth2UserInfo {

    // Get Github's unique identifier
    public GithubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    // Get Github's unique identifier
    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    // Get user's full name or username if name is not set
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    // Get user's email from Github
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    // Get user's profile picture from Github
    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }

}
