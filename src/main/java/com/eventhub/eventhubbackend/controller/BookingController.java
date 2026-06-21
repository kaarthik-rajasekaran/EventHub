package com.eventhub.eventhubbackend.controller;

import com.eventhub.eventhubbackend.dto.BookingRequest;
import com.eventhub.eventhubbackend.dto.BookingResponse;
import com.eventhub.eventhubbackend.entity.Booking;
import com.eventhub.eventhubbackend.entity.Event;
import com.eventhub.eventhubbackend.entity.User;
import com.eventhub.eventhubbackend.repository.BookingRepository;
import com.eventhub.eventhubbackend.repository.EventRepository;
import com.eventhub.eventhubbackend.repository.UserRepository;
import com.eventhub.eventhubbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public String createBooking(
            @RequestBody BookingRequest request,
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

        Event event =
                eventRepository
                        .findById(request.getEventId())
                        .orElse(null);

        if (event == null) {
            return "Event not found";
        }

        if (event.getAvailableTickets()
                < request.getQuantity()) {

            return "Not enough tickets available";
        }

        int totalCost =
                event.getPrice()
                        * request.getQuantity();

        if (user.getCredits() == null) {
            user.setCredits(1000);
        }

        if (user.getCredits() < totalCost) {
            return "Insufficient credits";
        }

        user.setCredits(
                user.getCredits()
                        - totalCost);

        userRepository.save(user);

        event.setAvailableTickets(
                event.getAvailableTickets()
                        - request.getQuantity());

        eventRepository.save(event);

        Booking booking = new Booking();

        booking.setUserEmail(email);

        booking.setEventId(
                request.getEventId());

        booking.setQuantity(
                request.getQuantity());

        booking.setBookingDate(
                LocalDateTime.now());

        bookingRepository.save(booking);

        try {

            emailService.sendTicketEmail(
                    email,
                    event.getTitle(),
                    event.getVenue(),
                    event.getEventDate().toString(),
                    booking.getQuantity(),
                    event.getImageUrl(),
                    booking.getId());

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "Booking successful for "
                + email;
    }

    @GetMapping("/my")
    public Object myBookings(
            @RequestAttribute(
                    value = "email",
                    required = false)
            String email) {

        if (email == null) {
            return "Login required";
        }

        List<Booking> bookings =
                bookingRepository
                        .findByUserEmail(email);

        List<BookingResponse> response =
                new ArrayList<>();

        for (Booking booking : bookings) {

            Event event =
                    eventRepository
                            .findById(
                                    booking.getEventId())
                            .orElse(null);

            BookingResponse dto =
                    new BookingResponse();

            dto.setBookingId(
                    booking.getId());

            dto.setQuantity(
                    booking.getQuantity());

            dto.setBookingDate(
                    booking.getBookingDate());

            if (event != null) {

                dto.setEventTitle(
                        event.getTitle());

                dto.setVenue(
                        event.getVenue());
            }

            response.add(dto);
        }

        return response;
    }

    @GetMapping("/{bookingId}")
    public Object getBooking(
            @PathVariable Long bookingId) {

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElse(null);

        if (booking == null) {
            return "Booking not found";
        }

        Event event =
                eventRepository
                        .findById(
                                booking.getEventId())
                        .orElse(null);

        BookingResponse dto =
                new BookingResponse();

        dto.setBookingId(
                booking.getId());

        dto.setQuantity(
                booking.getQuantity());

        dto.setBookingDate(
                booking.getBookingDate());

        if (event != null) {

            dto.setEventTitle(
                    event.getTitle());

            dto.setVenue(
                    event.getVenue());
        }

        return dto;
    }

    @DeleteMapping("/{bookingId}")
    public String cancelBooking(
            @PathVariable Long bookingId,
            @RequestAttribute(
                    value = "email",
                    required = false)
            String email) {

        if (email == null) {
            return "Login required";
        }

        Booking booking =
                bookingRepository
                        .findById(bookingId)
                        .orElse(null);

        if (booking == null) {
            return "Booking not found";
        }

        if (!booking.getUserEmail()
                .equals(email)) {

            return "Unauthorized";
        }

        Event event =
                eventRepository
                        .findById(
                                booking.getEventId())
                        .orElse(null);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        int refundAmount = 0;

        if (event != null) {

            event.setAvailableTickets(
                    event.getAvailableTickets()
                            + booking.getQuantity());

            eventRepository.save(event);

            if (user != null) {

                refundAmount =
                        event.getPrice()
                                * booking.getQuantity();

                user.setCredits(
                        user.getCredits()
                                + refundAmount);

                userRepository.save(user);
            }
        }

        bookingRepository.delete(booking);

        return "Booking cancelled. Refund of "
                + refundAmount
                + " credits processed.";
    }
}