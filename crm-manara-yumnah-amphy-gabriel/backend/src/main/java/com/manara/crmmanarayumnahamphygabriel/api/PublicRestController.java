package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.model.Event;
import com.manara.crmmanarayumnahamphygabriel.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Dans AccueilController.java — ajoute ces 2 endpoints API
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PublicRestController {

    @Autowired
    EventService eventService;

    @GetMapping("/evenements")
    public ResponseEntity<List<Event>> getEvents() {
        return ResponseEntity.ok(eventService.getAll());
    }
}