package com.eventhub.eventhubbackend.controller;

import com.eventhub.eventhubbackend.entity.Event;
import com.eventhub.eventhubbackend.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public Object createEvent(
            @RequestBody Event event,
            @RequestAttribute(value = "role", required = false)
            String role) {

        if (role == null) {
            return "Login required";
        }

        if (!role.equals("ADMIN")) {
            return "Admin access required";
        }

        event.setAvailableTickets(
                event.getCapacity());

        return eventRepository.save(event);
    }

    @GetMapping
    public List<Event> getAllEvents() {

        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public Object getEventById(
            @PathVariable Long id) {

        return eventRepository
                .findById(id)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public Object updateEvent(
            @PathVariable Long id,
            @RequestBody Event updatedEvent,
            @RequestAttribute(value = "role", required = false)
            String role) {

        if (role == null) {
            return "Login required";
        }

        if (!role.equals("ADMIN")) {
            return "Admin access required";
        }

        Event existingEvent =
                eventRepository.findById(id)
                        .orElse(null);

        if (existingEvent == null) {
            return "Event not found";
        }

        existingEvent.setTitle(
                updatedEvent.getTitle());

        existingEvent.setDescription(
                updatedEvent.getDescription());

        existingEvent.setVenue(
                updatedEvent.getVenue());

        existingEvent.setEventDate(
                updatedEvent.getEventDate());

        existingEvent.setCapacity(
                updatedEvent.getCapacity());

        existingEvent.setAvailableTickets(
                updatedEvent.getAvailableTickets());

        existingEvent.setPrice(
                updatedEvent.getPrice());

        existingEvent.setImageUrl(
                updatedEvent.getImageUrl());

        eventRepository.save(existingEvent);

        return existingEvent;
    }

    @DeleteMapping("/{id}")
    public String deleteEvent(
            @PathVariable Long id,
            @RequestAttribute(value = "role", required = false)
            String role) {

        if (role == null) {
            return "Login required";
        }

        if (!role.equals("ADMIN")) {
            return "Admin access required";
        }

        eventRepository.deleteById(id);

        return "Event deleted";
    }
}