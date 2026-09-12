package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.dto.InscriptionRequest;
import com.manara.crmmanarayumnahamphygabriel.model.*;
import com.manara.crmmanarayumnahamphygabriel.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parent")
// ← @CrossOrigin retiré : conflit avec SecurityConfig.cors() qui gère déjà allowedOrigins
public class ParentRestController {

    @Autowired private UserService userService;
    @Autowired private EnfantService enfantService;
    @Autowired private InscriptionService inscriptionService;
    @Autowired private ParentService parentService;
    @Autowired private ActiviteService activiteService;
    @Autowired private SessionService sessionService;
    @Autowired private NotificationService notificationService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private MessageEnfantService messageEnfantService;
    @Autowired private IncidentHistoryService incidentHistoryService;
    @Autowired private PresenceService presenceService;

    // ─── DASHBOARD ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            Parent parent = parentService.getByUser(user);
            List<Enfant> enfants = enfantService.getByParentId(parent.getId());

            List<Inscription> inscriptions = enfants.stream()
                    .flatMap(e -> inscriptionService.getByEnfant(e).stream())
                    .toList();

            Map<String, Object> data = new HashMap<>();
            data.put("email", email);
            data.put("nbr_enfants", enfants.size());
            data.put("nbr_inscription", inscriptions.size());
            // ← on n'expose PAS l'entité Parent directement (risque de boucle JPA)
            data.put("enfants", enfants.stream().map(e -> Map.of(
                    "id", e.getId(),
                    "prenom", e.getPrenom(),
                    "nom", e.getNom()
            )).toList());
            data.put("inscriptions", inscriptions.stream().map(i -> Map.of(
                    "id", i.getId(),
                    "enfant", Map.of(
                            "id", i.getEnfant().getId(),
                            "prenom", i.getEnfant().getPrenom(),
                            "nom", i.getEnfant().getNom()
                    ),
                    "session", Map.of(
                            "id", i.getSession().getId(),
                            "dateDebut", i.getSession().getDateDebut() != null
                                    ? i.getSession().getDateDebut().toString() : "",
                            "activite", Map.of(
                                    "id", i.getSession().getActivite().getId(),
                                    "titre", i.getSession().getActivite().getTitre()
                            )
                    ),
                    "statutPaiement", i.getStatutPaiement() != null ? i.getStatutPaiement() : ""
            )).toList());

            return ResponseEntity.ok(data);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── ENFANTS ──────────────────────────────────────────────────────────────

    @GetMapping("/children")
    public ResponseEntity<?> listChildren(Authentication authentication) {
        try {
            Parent parent = getParent(authentication);
            List<Enfant> enfants = enfantService.getByParentId(parent.getId());

            return ResponseEntity.ok(Map.of(
                    "enfants", enfants.stream().map(e -> Map.of(
                            "id", e.getId(),
                            "prenom", e.getPrenom(),
                            "nom", e.getNom(),
                            "dateNaissance", e.getDateNaissance() != null ? e.getDateNaissance().toString() : "",
                            "allergies", e.getAllergies() != null ? e.getAllergies() : "",
                            "notesMedicales", e.getNotesMedicales() != null ? e.getNotesMedicales() : ""
                    )).toList(),
                    "nbr_enfants", enfants.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/children")
    public ResponseEntity<?> addChild(Authentication authentication,
                                      @RequestBody Enfant enfant) {
        try {
            Parent parent = getParent(authentication);
            enfant.setParent(parent);
            Enfant saved = enfantService.save(enfant);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", saved.getId(),
                    "prenom", saved.getPrenom(),
                    "nom", saved.getNom()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/children/{id}")
    public ResponseEntity<?> updateChild(@PathVariable int id,
                                         @RequestBody Enfant enfant) {
        try {
            Enfant existing = enfantService.getById(id);
            existing.setNom(enfant.getNom());
            existing.setPrenom(enfant.getPrenom());
            existing.setDateNaissance(enfant.getDateNaissance());
            existing.setAllergies(enfant.getAllergies());
            existing.setNotesMedicales(enfant.getNotesMedicales());
            Enfant saved = enfantService.save(existing);
            return ResponseEntity.ok(Map.of(
                    "id", saved.getId(),
                    "prenom", saved.getPrenom(),
                    "nom", saved.getNom()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── ACTIVITÉS ────────────────────────────────────────────────────────────

    @GetMapping("/activities")
    public ResponseEntity<?> listActivities() {
        return ResponseEntity.ok(activiteService.getAll());
    }

    @GetMapping("/activities/{id}/sessions")
    public ResponseEntity<?> getSessionsByActivite(@PathVariable Integer id) {
        List<Session> sessions = sessionService.getByActiviteId(id);
        return ResponseEntity.ok(sessions.stream().map(s -> {
            long inscrits = inscriptionService.countBySession(s);
            int capacite = s.getCapaciteMax() != null ? s.getCapaciteMax() : 0;
            int placesRestantes = (int) Math.max(0, capacite - inscrits);

            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", s.getId());
            map.put("dateDebut", s.getDateDebut() != null ? s.getDateDebut().toString() : "");
            map.put("dateFin", s.getDateFin() != null ? s.getDateFin().toString() : "");
            map.put("heureDebut", s.getHeureDebut() != null ? s.getHeureDebut().toString() : "");
            map.put("heureFin", s.getHeureFin() != null ? s.getHeureFin().toString() : "");
            map.put("lieu", s.getLieu() != null ? s.getLieu() : "");
            map.put("statut", s.getStatut() != null ? s.getStatut() : "");
            map.put("capaciteMax", placesRestantes); // places restantes, pas total
            map.put("activite", java.util.Map.of(
                    "id", s.getActivite().getId(),
                    "titre", s.getActivite().getTitre()
            ));
            return map;
        }).toList());
    }

    @GetMapping("/activities/sessions/{sessionId}/register")
    public ResponseEntity<?> getRegistrationData(@PathVariable Integer sessionId,
                                                 Authentication authentication) {
        try {
            Session session = sessionService.getById(sessionId);
            if (session == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Session introuvable"));
            }

            Parent parent = getParent(authentication);
            List<Enfant> enfants = enfantService.getByParentId(parent.getId());

            return ResponseEntity.ok(Map.of(
                    "session", Map.of(
                            "id", session.getId(),
                            "dateDebut", session.getDateDebut() != null ? session.getDateDebut().toString() : "",
                            "activite", Map.of("titre", session.getActivite().getTitre())
                    ),
                    "enfants", enfants.stream().map(e -> Map.of(
                            "id", e.getId(),
                            "prenom", e.getPrenom(),
                            "nom", e.getNom()
                    )).toList()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/activities/register")
    public ResponseEntity<?> register(@Valid @RequestBody InscriptionRequest request,
                                      Authentication authentication) {
        try {
            Session session = sessionService.getById(request.getSessionId());
            Enfant enfant = enfantService.getById(request.getEnfantId());

            if (session == null || enfant == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Données invalides"));
            }

            if (inscriptionService.exists(session.getId(), enfant.getId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Déjà inscrit !"));
            }

            Inscription ins = new Inscription();
            ins.setSession(session);
            ins.setEnfant(enfant);
            ins.setStatutPaiement("En attente");
            inscriptionService.save(ins);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Inscription créée"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── PLANNING ─────────────────────────────────────────────────────────────

    @GetMapping("/planning")
    public ResponseEntity<?> getPlanning(Authentication authentication) {
        try {
            Parent parent = getParent(authentication);
            List<Enfant> enfants = enfantService.getByParentId(parent.getId());

            List<Inscription> inscriptions = new ArrayList<>();
            for (Enfant e : enfants) {
                inscriptions.addAll(inscriptionService.getByEnfant(e));
            }

            return ResponseEntity.ok(Map.of(
                    "enfants", enfants.stream().map(e -> Map.of(
                            "id", e.getId(), "prenom", e.getPrenom(), "nom", e.getNom()
                    )).toList(),
                    "inscriptions", inscriptions.stream().map(i -> Map.of(
                            "id", i.getId(),
                            "enfant", Map.of(
                                    "id", i.getEnfant().getId(),
                                    "prenom", i.getEnfant().getPrenom(),
                                    "nom", i.getEnfant().getNom()
                            ),
                            "session", Map.of(
                                    "id", i.getSession().getId(),
                                    "dateDebut", i.getSession().getDateDebut() != null
                                            ? i.getSession().getDateDebut().toString() : "",
                                    // Champs ajoutés
                                    "dateFin", i.getSession().getDateFin() != null
                                            ? i.getSession().getDateFin().toString() : "",
                                    "heureDebut", i.getSession().getHeureDebut() != null
                                            ? i.getSession().getHeureDebut().toString() : "",
                                    "heureFin", i.getSession().getHeureFin() != null
                                            ? i.getSession().getHeureFin().toString() : "",
                                    "lieu", i.getSession().getLieu() != null
                                            ? i.getSession().getLieu() : "",
                                    "statut", i.getSession().getStatut() != null
                                            ? i.getSession().getStatut() : "",
                                    "activite", Map.of(
                                            "id", i.getSession().getActivite().getId(),
                                            "titre", i.getSession().getActivite().getTitre()
                                    )
                            ),
                            "statutPaiement", i.getStatutPaiement() != null ? i.getStatutPaiement() : ""
                    )).toList()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── MESSAGES ENFANT ──────────────────────────────────────────────────────

    @GetMapping("/enfants/{enfantId}/messages")
    public ResponseEntity<?> getMessagesEnfant(@PathVariable Integer enfantId,
                                               Authentication authentication) {
        try {
            Parent parent = getParent(authentication);
            Enfant enfant = enfantService.getById(enfantId);

            if (!enfant.getParent().getId().equals(parent.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Accès refusé"));
            }

            List<Session> sessions = inscriptionService.getByEnfant(enfant)
                    .stream().map(Inscription::getSession).toList();

            List<MessageEnfant> messages = messageEnfantService.getAllForEnfant(enfant, sessions);
            messages.forEach(m -> messageEnfantService.marquerLu(m.getId()));

            return ResponseEntity.ok(Map.of(
                    "enfant", Map.of(
                            "id", enfant.getId(),
                            "prenom", enfant.getPrenom(),
                            "nom", enfant.getNom()
                    ),
                    "sessions", sessions.stream().map(s -> Map.of(
                            "id", s.getId(),
                            "activite", Map.of(
                                    "id", s.getActivite().getId(),
                                    "titre", s.getActivite().getTitre()
                            )
                    )).toList(),
                    "messages", messages.stream().map(m -> {
                        Map<String, Object> msg = new java.util.HashMap<>();
                        msg.put("id", m.getId());
                        msg.put("contenu", m.getContenu());
                        msg.put("dateEnvoi", m.getDateEnvoi() != null ? m.getDateEnvoi().toString() : "");
                        msg.put("lu", m.isLu());
                        msg.put("fichierNom", m.getFichierNom() != null ? m.getFichierNom() : null);
                        msg.put("fichierChemin", m.getFichierChemin() != null ? m.getFichierChemin() : null);
                        // Session
                        if (m.getSession() != null) {
                            msg.put("session", Map.of(
                                    "id", m.getSession().getId(),
                                    "activite", Map.of(
                                            "id", m.getSession().getActivite().getId(),
                                            "titre", m.getSession().getActivite().getTitre()
                                    )
                            ));
                        }
                        // Animateur
                        if (m.getAnimateur() != null && m.getAnimateur().getUser() != null) {
                            msg.put("animateur", Map.of(
                                    "prenom", m.getAnimateur().getUser().getPrenom(),
                                    "nom", m.getAnimateur().getUser().getNom(),
                                    "email", m.getAnimateur().getUser().getEmail() != null
                                            ? m.getAnimateur().getUser().getEmail() : ""
                            ));
                        }
                        return msg;
                    }).toList(),
                    "totalPresences", presenceService.countByEnfant(enfantId),
                    "totalAbsences", presenceService.countAbsencesByEnfant(enfantId),
                    "totalSessions", (long) sessions.size()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── INCIDENTS ────────────────────────────────────────────────────────────

    @GetMapping("/incidents")
    public ResponseEntity<?> getIncidents(Authentication authentication) {
        try {
            Parent parent = getParent(authentication);
            List<IncidentHistoryService.IncidentHistoryItem> incidents =
                    incidentHistoryService.getIncidentsByParent(parent.getId());

            incidents.forEach(i -> incidentHistoryService.marquerVuParParent(i.id()));

            return ResponseEntity.ok(incidents);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/contact-admin/{enfantId}")
    public ResponseEntity<?> contactAdmin(@PathVariable Integer enfantId,
                                          Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findUserByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            Enfant enfant = enfantService.getById(enfantId);
            notificationService.notifyIncident(user, enfant.getPrenom(), "QUESTION PARENT");

            return ResponseEntity.ok(Map.of("message", "Message envoyé à l'administrateur"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    // ─── HELPER ───────────────────────────────────────────────────────────────

    private Parent getParent(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return parentService.getByUser(user);
    }
    @DeleteMapping("/children/{id}")
    public ResponseEntity<?> deleteChild(@PathVariable int id) {
        try {
            enfantService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Enfant supprimé"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}