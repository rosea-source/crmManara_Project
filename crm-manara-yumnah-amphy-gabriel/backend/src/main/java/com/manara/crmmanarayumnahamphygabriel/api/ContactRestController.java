package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.service.NotificationService;
import com.manara.crmmanarayumnahamphygabriel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactRestController {

    @Autowired private UserService userService;
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> sendContact(
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            String sujet = payload.get("sujet");
            String message = payload.get("message");

            if (sujet == null || sujet.isBlank() ||
                    message == null || message.isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Sujet et message requis."));
            }

            notificationService.sendContactMessage(user, sujet, message);
            return ResponseEntity.ok(Map.of("message", "Message envoyé."));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}