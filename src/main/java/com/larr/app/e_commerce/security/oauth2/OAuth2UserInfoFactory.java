package com.larr.app.e_commerce.security.oauth2;

import java.util.Map;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

/**
 * Factory class for creating OAuth2UserInfo objects.
 * 
 * This factory uses the Factory Design Pattern to create the appropriate
 * OAuth2UserInfo implementation based on the OAuth2 provider.
 * 
 * Why do we need this?
 * - Different OAuth2 providers return data in different formats
 * - We need to create the right parser for each provider
 * - Centralizes provider-specific logic in one place
 * 
 * This is called from CustomOAuth2UserService during login process.
 */
public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("github")) {
            return new GithubOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("Login with " + registrationId + " is not supported");
        }
    }
}
