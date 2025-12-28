package com.example.netapp.controllers;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.netapp.dto.requests.LoginRequest;
import com.example.netapp.dto.requests.SignupRequest;
import com.example.netapp.dto.responses.LoginResponse;
import com.example.netapp.dto.responses.SignupResponse;
import com.example.netapp.dto.responses.TokenResponse;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.repository.UserRepository;
import com.example.netapp.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthController(UserRepository repo, PasswordEncoder encoder, JwtService jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @PostMapping("/signup")
    public SignupResponse signup(@RequestBody SignupRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(req.email());
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));

        repo.save(user);
        return new SignupResponse("success" , user);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        UserEntity user = repo.findByEmail(req.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwt.generateToken(user.getUserId(),user.getRole() ,user.getEmail());
        TokenResponse token_res = new TokenResponse(token);
        return new LoginResponse(user ,token);
    }
}