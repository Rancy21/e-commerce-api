package com.larr.app.e_commerce.security.oauth2;

import java.util.Map;

/**
 * Abstract base class for extracting user information from OAuth2 providers.
 * 
 * Problem: Different OAuth2 providers (Google, GitHub, Facebook) return
 * user data in different formats:
 * - Google uses "sub" for user ID, "picture" for profile image
 * - GitHub uses "id" for user ID, "avatar_url" for profile image
 * - Facebook uses different field names too
 * 
 * Solution: This abstract class provides a common interface to extract
 * user data regardless of the provider. Each provider has its own
 * implementation that knows how to parse that provider's data format.
 * 
 * Subclasses:
 * - GoogleOAuth2UserInfo
 * - GithubOAuth2UserInfo
 */

public abstract class OAuth2UserInfo {
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();
}
