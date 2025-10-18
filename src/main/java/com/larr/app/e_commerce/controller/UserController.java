
package com.larr.app.e_commerce.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.larr.app.e_commerce.model.User;
import com.larr.app.e_commerce.model.UserRole;
import com.larr.app.e_commerce.security.jwt.JwtUtils;
import com.larr.app.e_commerce.service.UserService;

// import lombok.val;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    // User registration
    @PostMapping(value = "/api/auth/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        user.setRole(UserRole.USER);
        User savedUser = service.saveUser(user);
        if (savedUser != null) {
            return ResponseEntity.ok(savedUser);
        } else {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }
    }

    // Update user full name
    @PutMapping(value = "/api/users/updateName/{email}")
    public ResponseEntity<?> updateUserName(@RequestParam String fullname, @PathVariable String email) {
        User existingUser = service.getUser(email).isPresent() ? service.getUser(email).get()
                : null;
        if (existingUser != null) {
            existingUser.setFullname(fullname);
            return ResponseEntity.ok(service.saveUser(existingUser));
        } else {
            return new ResponseEntity<>("user not found or does exist", HttpStatus.NOT_FOUND);
        }
    }

    // Update user password
    @PutMapping(value = "/api/users/updatePassword/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUserPassword(@RequestParam String password, @PathVariable String email) {
        User existingUser = service.getUser(email).isPresent() ? service.getUser(email).get() : null;

        if (existingUser != null) {
            // verify if new password is the same as the previous one
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            if (encoder.matches(password, existingUser.getPassword())) {
                return new ResponseEntity<>("Please provide a new password", HttpStatus.CONFLICT);
            } else {
                String newHashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                existingUser.setPassword(newHashedPassword);
                return ResponseEntity.ok(service.saveUser(existingUser));
            }

        } else {
            return new ResponseEntity<>("user not found or does not exist", HttpStatus.NOT_FOUND);
        }
    }

    // Delete User (Set isActive to false)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(value = "/api/users/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String userEmail) {
        if (service.getUser(userEmail).isPresent()) {
            User existingUser = service.getUser(userEmail).get();
            existingUser.setActive(false);
            return ResponseEntity.ok(service.saveUser(existingUser));
        } else {
            return new ResponseEntity<>("user not found or does not exist", HttpStatus.NOT_FOUND);
        }
    }

    // User authentication
    @PutMapping(value = "/api/auth/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        // Authenticate the user credentials using Spring Security's
        // AuthenticationManager.
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Extract UserDetails from the authentication principal
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate a JWT cookie containing the user's token for subsequent
        // authenticated requests.
        ResponseCookie cookie = jwtUtils.generateJwtCookie(userDetails.getUsername());

        // Return HTTP 200 OK with the Set-Cookie header for the JWT and full user
        // details in the body.
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(service.getUser(userDetails.getUsername()).get());
    }
    // Forgot Password Implementation (with a mailing system)
    // Third-party authentication(With google only)

}
