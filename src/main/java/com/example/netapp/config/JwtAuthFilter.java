package com.example.netapp.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.netapp.repository.UserRepository;
import com.example.netapp.services.JwtService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository repo;

    public JwtAuthFilter(JwtService jwt, UserRepository repo) {
        this.jwt = jwt;
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            String email = jwt.extractEmail(token);
            Long userId = jwt.extractUserId(token);

            repo.findByEmail(email).ifPresent(user -> {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(email, null, List.of());
                auth.setDetails(userId);
                SecurityContextHolder.getContext().setAuthentication(auth);
            });
        }

        chain.doFilter(req, res);
    }
}