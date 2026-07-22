package com.projectbyPranayChavan.JournalApp.filter;

import com.projectbyPranayChavan.JournalApp.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Check if Authorization header contains a Bearer token
        if (authorizationHeader != null &&
                authorizationHeader.startsWith("Bearer ")) {

            // Remove "Bearer " and get only the JWT token
            jwt = authorizationHeader.substring(7);

            // Extract username from JWT
            username = jwtUtil.extractUsername(jwt);
        }

        // If username was successfully extracted from JWT
        if (username != null) {

            // Load user details from database
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            // Validate JWT token
            if (jwtUtil.validateToken(jwt)) {

                // Create authenticated user object
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Add request details
                auth.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // Store authentication in Spring Security context
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(auth);
            }
        }

        // Continue to the next filter
        chain.doFilter(request, response);
    }
}