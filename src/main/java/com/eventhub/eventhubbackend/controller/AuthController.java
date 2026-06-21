package com.eventhub.eventhubbackend.controller;

import com.eventhub.eventhubbackend.dto.LoginRequest;
import com.eventhub.eventhubbackend.dto.RegisterRequest;
import com.eventhub.eventhubbackend.entity.User;
import com.eventhub.eventhubbackend.repository.UserRepository;
import com.eventhub.eventhubbackend.security.JwtService;
import com.eventhub.eventhubbackend.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PostMapping("/register")
    public String register(
            @RequestBody RegisterRequest request) {

        if (userRepository
                .findByEmail(request.getEmail())
                .isPresent()) {

            return "Email already exists";
        }

        User user = new User();

        user.setName(
                request.getName());

        user.setEmail(
                request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()));

        user.setRole("USER");

        user.setCredits(1000);

        userRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody LoginRequest request) {

        User user =
                userRepository
                        .findByEmail(
                                request.getEmail())
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Invalid email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword());

        if (!passwordMatches) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            "Invalid email or password");
        }

        String token =
                jwtService.generateToken(
                        user.getEmail(),
                        user.getRole());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(
            @RequestBody Map<String, String> body) {

        String email =
                body.get("email");

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return "If the email exists, a reset link has been sent";
        }

        String resetToken =
                UUID.randomUUID()
                        .toString();

        user.setResetToken(
                resetToken);

        user.setResetTokenExpiry(
                LocalDateTime.now()
                        .plusMinutes(30));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                email,
                resetToken);

        return "If the email exists, a reset link has been sent";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestBody Map<String, String> body) {

        String token =
                body.get("token");

        String newPassword =
                body.get("password");

        User user =
                userRepository
                        .findByResetToken(token)
                        .orElse(null);

        if (user == null) {

            return "Invalid reset token";
        }

        if (user.getResetTokenExpiry() == null ||
                user.getResetTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            return "Reset token expired";
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword));

        user.setResetToken(null);

        user.setResetTokenExpiry(null);

        userRepository.save(user);

        return "Password reset successful";
    }
}