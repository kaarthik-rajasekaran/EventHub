package com.eventhub.eventhubbackend.controller;

import com.eventhub.eventhubbackend.entity.Event;
import com.eventhub.eventhubbackend.entity.User;
import com.eventhub.eventhubbackend.repository.BookingRepository;
import com.eventhub.eventhubbackend.repository.EventRepository;
import com.eventhub.eventhubbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {

        Map<String, Object> stats =
                new HashMap<>();

        stats.put(
                "users",
                userRepository.count());

        stats.put(
                "events",
                eventRepository.count());

        stats.put(
                "bookings",
                bookingRepository.count());

        List<Event> events =
                eventRepository.findAll();

        int totalTicketsSold = 0;

        String mostPopularEvent =
                "No Events";

        int highestSold = 0;

        for (Event event : events) {

            int sold =
                    event.getCapacity()
                            - event.getAvailableTickets();

            totalTicketsSold += sold;

            if (sold > highestSold) {

                highestSold = sold;

                mostPopularEvent =
                        event.getTitle();
            }
        }

        stats.put(
                "ticketsSold",
                totalTicketsSold);

        stats.put(
                "mostPopularEvent",
                mostPopularEvent);

        return stats;
    }

    @GetMapping("/users")
    public Object getAllUsers(
            @RequestAttribute(
                    value = "role",
                    required = false)
            String role) {

        if (role == null) {
            return "Login required";
        }

        if (!role.equals("ADMIN")) {
            return "Admin access required";
        }

        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/credits")
    public String updateCredits(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            @RequestAttribute(
                    value = "role",
                    required = false)
            String role) {

        if (role == null) {
            return "Login required";
        }

        if (!role.equals("ADMIN")) {
            return "Admin access required";
        }

        User user =
                userRepository.findById(id)
                        .orElse(null);

        if (user == null) {
            return "User not found";
        }

        Integer credits =
                request.get("credits");

        user.setCredits(credits);

        userRepository.save(user);

        return "Credits updated";
    }
}