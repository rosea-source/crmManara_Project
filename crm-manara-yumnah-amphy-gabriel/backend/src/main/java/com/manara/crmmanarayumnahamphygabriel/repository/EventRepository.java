package com.manara.crmmanarayumnahamphygabriel.repository;

import com.manara.crmmanarayumnahamphygabriel.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}