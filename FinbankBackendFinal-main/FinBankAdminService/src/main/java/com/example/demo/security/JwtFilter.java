package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Locale;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Extracted Token: " + token);
            try {
                username = jwtUtil.extractUsername(token);
                System.out.println("Extracted Username: " + username);
            } catch (Exception e) {
                System.out.println("Exception extracting username: " + e.getMessage());
            }
        }

        if (username != null && jwtUtil.validateToken(token)) {

            String role = jwtUtil.extractRole(token);
            if (role != null) {
                role = role.trim();
                if (role.startsWith("ROLE_")) {
                    role = role.substring("ROLE_".length());
                }
                role = role.toUpperCase(Locale.ROOT);
            }
            System.out.println("Extracted Role: " + role);

            // Spring Security expects roles to be prefixed with ROLE_
            var authorities = Collections
                    .singletonList(
                            new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role));

            System.out.println("Authorities set: " + authorities);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities);

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
