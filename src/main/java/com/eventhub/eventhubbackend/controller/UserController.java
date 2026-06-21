package com.eventhub.eventhubbackend.controller;

import com.eventhub.eventhubbackend.entity.User;
import com.eventhub.eventhubbackend.repository.BookingRepository;
import com.eventhub.eventhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/api/me")
    public String me(
            @RequestAttribute(
                    value = "email",
                    required = false)
            String email) {

        if (email == null) {
            return "No JWT supplied";
        }

        return "Logged in as: " + email;
    }

    @GetMapping("/api/users/profile")
    public Object getProfile(
            @RequestAttribute(
                    value = "email",
                    required = false)
            String email) {

        if (email == null) {
            return "Login required";
        }

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {
            return "User not found";
        }

        Map<String, Object> profile =
                new HashMap<>();

        profile.put(
                "name",
                user.getName());

        profile.put(
                "email",
                user.getEmail());

        profile.put(
                "role",
                user.getRole());

        profile.put(
                "credits",
                user.getCredits());

        profile.put(
                "totalBookings",
                bookingRepository
                        .findByUserEmail(email)
                        .size());

        return profile;
    }
}