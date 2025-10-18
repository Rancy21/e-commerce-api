package com.larr.app.e_commerce.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.larr.app.e_commerce.security.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Retrieve jwt token from the request
            String jwt = jwtUtils.getJwtFromCookie(request);

            // Check if the token exist and is valid
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // Extract user email from token
                String userEmail = jwtUtils.getEmailFromJwtToken(jwt);

                // Load user's full details from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // authentication object representing the user

                // attach the request details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store the authenticated user in SecurityContent for controllers to retrieve
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtValidationException ex) {
            logger.error("JWT error: {}", ex.getMessage());
            request.setAttribute("jwtErrorMessage", ex.getMessage());

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
