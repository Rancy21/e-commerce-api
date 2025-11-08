package com.larr.app.e_commerce.service;

import java.util.Optional;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.model.UserRole;
import com.larr.app.e_commerce.repository.UserRepository;
import com.larr.app.e_commerce.security.CustomOAuth2User;
import com.larr.app.e_commerce.security.oauth2.OAuth2UserInfo;
import com.larr.app.e_commerce.security.oauth2.OAuth2UserInfoFactory;

import jakarta.transaction.Transactional;

/*
 * User service that processes user data from OAuth2 providers.
 * Responsibilities:
 * 1. Fetch user info from the OAuth2 provider
 * 2. Extract relevant user data (email, name, picture)
 * 3. Create new user or update existing user in database
 * 4. Return a CustomOAuth2User object for Spring Securit
 */

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    // private Logger logger =
    // LoggerFactory.getLogger(CustomOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    // Load user information from OAuth2 provider
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    // Process OAuth2 user data and save/update in the database
    @Transactional
    public OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getAuth2UserInfo(registrationId, oauth2User.getAttributes());

        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }
        Optional<User> userOptional = userRepository.findUserByEmail(userInfo.getEmail());
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, userInfo);
        } else {
            user = registerNewUser(userRequest, userInfo);
        }

        return new CustomOAuth2User(user, oauth2User.getAttributes());
    }

    // Register a new user in the database
    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo userInfo) {
        User user = new User();
        user.setProvider(userRequest.getClientRegistration().getRegistrationId());
        user.setProviderId(userInfo.getId());
        user.setFullname(userInfo.getName());
        user.setEmail(userInfo.getEmail());
        user.setRole(UserRole.USER);
        user.setPassword(null);
        return userRepository.save(user);

    }

    // Update existing user information
    private User updateExistingUser(User user, OAuth2UserInfo userInfo) {
        user.setFullname(userInfo.getName());
        return userRepository.save(user);
    }

}
