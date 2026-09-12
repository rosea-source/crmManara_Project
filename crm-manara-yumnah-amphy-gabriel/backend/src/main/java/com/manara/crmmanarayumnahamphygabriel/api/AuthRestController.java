package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.model.User;
import com.manara.crmmanarayumnahamphygabriel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthRestController {
    @Autowired
    UserService userService;

    // Dans AuthRestController.java
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            return ResponseEntity.status(401).body("Utilisateur introuvable");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("prenom", user.getPrenom());
        response.put("nom", user.getNom());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            userService.register(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Utilisateur créé avec succès");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur serveur");
        }
    }
}