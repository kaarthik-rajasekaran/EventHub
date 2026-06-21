package com.eventhub.eventhubbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String header =
                request.getHeader("Authorization");

        if (header != null &&
                header.startsWith("Bearer ")) {

            String token =
                    header.substring(7);

            try {

                String email =
                        jwtService.extractEmail(token);

                String role =
                        jwtService.extractRole(token);

                request.setAttribute(
                        "email",
                        email);

                request.setAttribute(
                        "role",
                        role);

            } catch (Exception e) {

                response.setStatus(401);

                response.getWriter()
                        .write("Invalid JWT");

                return;
            }
        }

        filterChain.doFilter(
                request,
                response);
    }
}