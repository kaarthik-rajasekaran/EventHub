package com.eventhub.eventhubbackend.repository;

import com.eventhub.eventhubbackend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}