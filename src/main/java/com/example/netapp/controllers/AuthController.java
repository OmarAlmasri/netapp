package com.example.netapp.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.netapp.dto.requests.LoginRequest;
import com.example.netapp.dto.requests.SignupRequest;
import com.example.netapp.dto.responses.LoginResponse;
import com.example.netapp.dto.responses.SignupResponse;
import com.example.netapp.dto.responses.TokenResponse;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.entity.UserRole;
import com.example.netapp.exceptions.HttpException;
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
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new HttpException(HttpStatus.BAD_REQUEST,"User already exists");
        }
        if(req.password() == null || req.username() == null)
        	throw new HttpException(HttpStatus.BAD_REQUEST,"the values for signin should be { email , username , password }");
        
        UserEntity user = new UserEntity();
        user.setEmail(req.email());
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));

        repo.save(user);
        return ResponseEntity.ok( new SignupResponse("success" , user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        UserEntity user = repo.findByEmail(req.email())
                .orElseThrow(() -> new HttpException(HttpStatus.BAD_REQUEST,"Invalid Email"));

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new HttpException(HttpStatus.BAD_REQUEST,"Invalid password");
        }

        String token = jwt.generateToken(user.getUserId(),user.getRole() ,user.getEmail(),user.getUsername());
        return ResponseEntity.ok(new LoginResponse(user ,token));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/signup-staff")
    public SignupResponse signup_staff(@RequestBody SignupRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new HttpException(HttpStatus.CONFLICT,"User already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(req.email());
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(UserRole.STAFF);

        repo.save(user);
        return new SignupResponse("success" , user);
    }
    
    @PostMapping("/signup-admin")
    public SignupResponse signup_admin(@RequestBody SignupRequest req) {
        if (repo.findByEmail(req.email()).isPresent()) {
            throw new HttpException(HttpStatus.CONFLICT,"User already exists");
        }

        UserEntity user = new UserEntity();
        user.setEmail(req.email());
        user.setUsername(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(UserRole.ADMIN);

        repo.save(user);
        return new SignupResponse("success" , user);
    } 
    
}