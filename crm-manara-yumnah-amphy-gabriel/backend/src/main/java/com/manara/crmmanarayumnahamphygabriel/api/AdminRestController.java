package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.model.*;
import com.manara.crmmanarayumnahamphygabriel.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminRestController {
    @Autowired
    private AdminDashboardService adminDashboardService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ActiviteService activiteService;

    @Autowired
    private InscriptionService inscriptionService;

    @Autowired
    private UserService userService;
    @Autowired
    private AnimateurService animateurService;

    @Autowired
    private EventService eventService; // Assure-toi que ce service existe côté Java

    @Autowired
    private IncidentHistoryService incidentHistoryService;

    @Autowired
    private ParentService parentService;
    @Autowired
    private EnfantService enfantService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private NotificationService notificationService;
    // ================= SESSIONS =================

    @GetMapping("/sessions")
    public List<Session> getAllSessions() {
        return sessionService.getAll();
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable Integer id) {

        Session s = sessionService.getById(id);

        if (s == null) {
            return ResponseEntity.notFound().build();
        }

        long inscrits = inscriptionService.countBySession(s);

        Map<String, Object> map = new HashMap<>();
        map.put("id", s.getId());
        map.put("lieu", s.getLieu());
        map.put("dateDebut", s.getDateDebut());
        map.put("dateFin", s.getDateFin());
        map.put("heureDebut", s.getHeureDebut());
        map.put("heureFin", s.getHeureFin());
        map.put("capaciteMax", s.getCapaciteMax());
        map.put("statut", s.getStatut());

        map.put("activiteTitre", s.getActivite() != null ? s.getActivite().getTitre() : "");
        map.put("animateurNom",
                s.getAnimateur() != null && s.getAnimateur().getUser() != null
                        ? s.getAnimateur().getUser().getPrenom() + " " + s.getAnimateur().getUser().getNom()
                        : ""
        );

        map.put("inscrits", inscrits);

        return ResponseEntity.ok(map);
    }
    // ─── REMPLACE dans AdminRestController.java ───────────────────────────────
// L'ancien endpoint retournait void sans ResponseEntity → erreur silencieuse

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé"));
        } catch (Exception e) {
            // Contrainte FK (parent lié à enfants, animateur lié à sessions, etc.)
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Impossible de supprimer : cet utilisateur a des données liées. " +
                                    "Supprimez d'abord ses enfants / sessions."));
        }
    }

    @GetMapping("/enfants/{id}")
    public ResponseEntity<Map<String, Object>> getEnfantDetail(@PathVariable Integer id) {
        Enfant e = enfantService.getById(id);
        if (e == null) return ResponseEntity.notFound().build();

        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("nom", e.getNom());
        map.put("prenom", e.getPrenom());
        map.put("dateNaissance", e.getDateNaissance() != null ? e.getDateNaissance().toString() : "");
        map.put("parentUserId", e.getParent() != null && e.getParent().getUser() != null
                ? e.getParent().getUser().getId() : null);

        List<Map<String, Object>> inscriptions = inscriptionService.getByEnfant(e)
                .stream().map(insc -> {
                    Map<String, Object> im = new HashMap<>();
                    im.put("activiteTitre", insc.getSession().getActivite() != null
                            ? insc.getSession().getActivite().getTitre() : "");
                    im.put("dateDebut", insc.getSession().getDateDebut() != null
                            ? insc.getSession().getDateDebut().toString() : "");
                    im.put("heureDebut", insc.getSession().getHeureDebut() != null
                            ? insc.getSession().getHeureDebut().toString() : "");
                    im.put("heureFin", insc.getSession().getHeureFin() != null
                            ? insc.getSession().getHeureFin().toString() : "");
                    im.put("lieu", insc.getSession().getLieu());
                    im.put("statutPaiement", insc.getStatutPaiement());
                    return im;
                }).collect(java.util.stream.Collectors.toList());

        map.put("inscriptions", inscriptions);
        return ResponseEntity.ok(map);
    }


    @PostMapping("/sessions")
    public ResponseEntity<?> createSession(@RequestBody Map<String, Object> payload) {
        try {
            // ── Récupération des champs ───────────────────────────────────────
            Integer activiteId  = payload.get("activiteId")  != null ? ((Number) payload.get("activiteId")).intValue()  : null;
            Integer animateurId = payload.get("animateurId") != null ? ((Number) payload.get("animateurId")).intValue() : null;
            String dateDebut    = (String) payload.get("dateDebut");
            String dateFin      = (String) payload.get("dateFin");
            String heureDebut   = (String) payload.get("heureDebut");
            String heureFin     = (String) payload.get("heureFin");
            String lieu         = (String) payload.get("lieu");
            Integer capaciteMax = payload.get("capaciteMax") != null ? ((Number) payload.get("capaciteMax")).intValue() : null;
            String statut       = payload.get("statut") != null ? (String) payload.get("statut") : "Prévue";

            if (activiteId == null || animateurId == null || dateDebut == null || heureDebut == null) {
                return ResponseEntity.badRequest().body("Champs obligatoires manquants.");
            }

            // ── Construction de la session ────────────────────────────────────
            Session session = new Session();
            session.setActivite(activiteService.getById(activiteId));
            session.setAnimateur(animateurService.getById(animateurId));
            session.setDateDebut(java.time.LocalDate.parse(dateDebut));
            session.setDateFin(dateFin != null && !dateFin.isBlank() ? java.time.LocalDate.parse(dateFin) : null);
            session.setHeureDebut(java.time.LocalTime.parse(heureDebut));
            session.setHeureFin(java.time.LocalTime.parse(heureFin));
            session.setLieu(lieu);
            session.setCapaciteMax(capaciteMax);
            session.setStatut(statut);

            Session saved = sessionService.save(session);

            // ── Email à l'animateur ───────────────────────────────────────────
            try {
                Animateur animateur = animateurService.getById(animateurId);
                if (animateur != null && animateur.getUser() != null) {
                    String activiteTitre = saved.getActivite() != null
                            ? saved.getActivite().getTitre() : "—";
                    notificationService.notifySessionAssignee(
                            animateur.getUser(),
                            activiteTitre,
                            dateDebut,
                            dateFin,
                            heureDebut,
                            heureFin,
                            lieu,
                            capaciteMax
                    );
                }
            } catch (Exception emailEx) {
                // On ne bloque pas la création si l'email échoue
                System.err.println("Email animateur non envoyé : " + emailEx.getMessage());
            }

            return ResponseEntity.ok(saved.getId());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }
    @PutMapping("/sessions/{id}")
    public ResponseEntity<?> updateSession(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        try {
            Session existing = sessionService.getById(id);
            if (existing == null) return ResponseEntity.notFound().build();

            // Champs simples
            if (payload.get("lieu") != null)
                existing.setLieu((String) payload.get("lieu"));
            if (payload.get("statut") != null)
                existing.setStatut((String) payload.get("statut"));
            if (payload.get("dateDebut") != null && !((String) payload.get("dateDebut")).isEmpty())
                existing.setDateDebut(java.time.LocalDate.parse((String) payload.get("dateDebut")));
            if (payload.get("dateFin") != null && !((String) payload.get("dateFin")).isEmpty())
                existing.setDateFin(java.time.LocalDate.parse((String) payload.get("dateFin")));
            if (payload.get("heureDebut") != null && !((String) payload.get("heureDebut")).isEmpty())
                existing.setHeureDebut(java.time.LocalTime.parse((String) payload.get("heureDebut")));
            if (payload.get("heureFin") != null && !((String) payload.get("heureFin")).isEmpty())
                existing.setHeureFin(java.time.LocalTime.parse((String) payload.get("heureFin")));
            if (payload.get("capaciteMax") != null)
                existing.setCapaciteMax(((Number) payload.get("capaciteMax")).intValue());

            //  Animateur — était ignoré avant car Session n'a pas de champ animateurId
            if (payload.get("animateurId") != null) {
                Integer animateurId = ((Number) payload.get("animateurId")).intValue();
                existing.setAnimateur(animateurService.getById(animateurId));
            }

            // Activité — même problème
            if (payload.get("activiteId") != null) {
                Integer activiteId = ((Number) payload.get("activiteId")).intValue();
                existing.setActivite(activiteService.getById(activiteId));
            }

            return ResponseEntity.ok(sessionService.save(existing));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/sessions/{id}")
    public void deleteSession(@PathVariable Integer id) {
        sessionService.delete(id);
    }
    @GetMapping("/sessions/list")
    public ResponseEntity<List<Map<String, Object>>> getSessionsList() {
        return ResponseEntity.ok(
                sessionService.getAll().stream().map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("lieu", s.getLieu());
                    map.put("dateDebut", s.getDateDebut() != null ? s.getDateDebut().toString() : "");
                    map.put("dateFin", s.getDateFin() != null ? s.getDateFin().toString() : "");
                    map.put("heureDebut", s.getHeureDebut() != null ? s.getHeureDebut().toString() : "");
                    map.put("heureFin", s.getHeureFin() != null ? s.getHeureFin().toString() : "");
                    map.put("capaciteMax", s.getCapaciteMax());
                    map.put("statut", s.getStatut());
                    map.put("activiteTitre", s.getActivite() != null ? s.getActivite().getTitre() : "");
                    map.put("activiteId", s.getActivite() != null ? s.getActivite().getId() : null);
                    map.put("animateurNom", s.getAnimateur() != null && s.getAnimateur().getUser() != null
                            ? s.getAnimateur().getUser().getPrenom() + " " + s.getAnimateur().getUser().getNom() : "");
                    map.put("animateurId", s.getAnimateur() != null ? s.getAnimateur().getId() : null);
                    return map;
                }).collect(java.util.stream.Collectors.toList())
        );
    }
    @GetMapping("/sessions/{id}/attentes")
    public ResponseEntity<Map<String, Object>> getAttentes(@PathVariable Integer id) {
        Session session = sessionService.getById(id);
        if (session == null) return ResponseEntity.notFound().build();

        List<Map<String, Object>> inscriptions = inscriptionService.getWaitingBySession(session)
                .stream().map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", i.getId());
                    map.put("statutPaiement", i.getStatutPaiement());
                    map.put("enfantPrenom", i.getEnfant() != null ? i.getEnfant().getPrenom() : "");
                    map.put("enfantNom", i.getEnfant() != null ? i.getEnfant().getNom() : "");
                    map.put("enfantAge", i.getEnfant() != null && i.getEnfant().getDateNaissance() != null
                            ? java.time.Period.between(i.getEnfant().getDateNaissance(), java.time.LocalDate.now()).getYears() + " ans" : "-");
                    map.put("parentNom", i.getEnfant() != null && i.getEnfant().getParent() != null
                            && i.getEnfant().getParent().getUser() != null
                            ? i.getEnfant().getParent().getUser().getPrenom() + " " + i.getEnfant().getParent().getUser().getNom() : "");
                    map.put("activiteTitre", i.getSession() != null && i.getSession().getActivite() != null
                            ? i.getSession().getActivite().getTitre() : "");
                    return map;
                }).collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", id);
        result.put("activiteTitre", session.getActivite() != null ? session.getActivite().getTitre() : "");
        result.put("inscriptions", inscriptions);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/sessions/{id}/inscrits")
    public ResponseEntity<Map<String, Object>> getInscrits(@PathVariable Integer id) {
        Session session = sessionService.getById(id);
        if (session == null) return ResponseEntity.notFound().build();

        long inscrits = inscriptionService.countBySession(session);
        int capacite = session.getCapaciteMax() != null ? session.getCapaciteMax() : 0;
        int pourcentage = capacite > 0 ? (int)((inscrits * 100) / capacite) : 0;

        List<Map<String, Object>> inscriptions = inscriptionService.getBySession(session)
                .stream().map(i -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", i.getId());
                    map.put("statutPaiement", i.getStatutPaiement());
                    map.put("enfantPrenom", i.getEnfant() != null ? i.getEnfant().getPrenom() : "");
                    map.put("enfantNom", i.getEnfant() != null ? i.getEnfant().getNom() : "");
                    map.put("parentNom", i.getEnfant() != null && i.getEnfant().getParent() != null
                            && i.getEnfant().getParent().getUser() != null
                            ? i.getEnfant().getParent().getUser().getPrenom() + " " + i.getEnfant().getParent().getUser().getNom() : "");
                    return map;
                }).collect(java.util.stream.Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("sessionId", id);
        result.put("activiteTitre", session.getActivite() != null ? session.getActivite().getTitre() : "");
        result.put("capaciteMax", capacite);
        result.put("inscrits", inscrits);
        result.put("pourcentage", pourcentage);
        result.put("inscriptions", inscriptions);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/inscriptions/{id}/accept")
    public ResponseEntity<?> accepterInscription(@PathVariable Integer id) {
        try {
            Inscription ins = inscriptionService.accepterInscription(id);
            return ResponseEntity.ok(ins);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/inscriptions/{id}/refuse")
    public ResponseEntity<?> refuserInscription(@PathVariable Integer id) {
        inscriptionService.delete(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/animateurs/list")
    public ResponseEntity<List<Map<String, Object>>> getAnimateurs() {
        return ResponseEntity.ok(
                animateurService.getAll().stream().map(a -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", a.getId());
                    map.put("nom", a.getUser() != null ? a.getUser().getPrenom() + " " + a.getUser().getNom() : "");
                    return map;
                }).collect(java.util.stream.Collectors.toList())
        );
    }

    // ================= ACTIVITES =================

    @GetMapping("/activites")
    public List<Activite> getAllActivites() {
        return activiteService.getAll();
    }

    @PostMapping("/activites")
    public Activite createActivite(@RequestBody Activite activite) {
        return activiteService.save(activite);
    }

    @PutMapping("/activites/{id}")
    public Activite updateActivite(@PathVariable Integer id, @RequestBody Activite activite) {
        Activite existing = activiteService.getById(id);

        if (existing == null) {
            throw new RuntimeException("Activite not found");
        }

        existing.setTitre(activite.getTitre());
        existing.setDescription(activite.getDescription());
        existing.setAgeMin(activite.getAgeMin());
        existing.setAgeMax(activite.getAgeMax());

        return activiteService.save(existing);
    }

    @DeleteMapping("/activites/{id}")
    public void deleteActivite(@PathVariable Integer id) {
        activiteService.delete(id);
    }

    // ================= INSCRIPTIONS =================

    @GetMapping("/inscriptions")
    public List<Inscription> getAllInscriptions() {
        return inscriptionService.getAll();
    }


    // ================= USERS =================

    @GetMapping("/users/list")
    public ResponseEntity<List<Map<String, Object>>> getUsersList() {
        return ResponseEntity.ok(
                userService.getAll().stream().map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("prenom", u.getPrenom());
                    map.put("nom", u.getNom());
                    map.put("email", u.getEmail());
                    map.put("telephone", u.getTelephone());
                    map.put("role", u.getRole());
                    return map;
                }).collect(java.util.stream.Collectors.toList())
        );
    }

    @GetMapping("/users/{id}/detail")
    public ResponseEntity<Map<String, Object>> getUserDetail(@PathVariable Integer id) {
        User u = userService.getById(id);
        if (u == null) return ResponseEntity.notFound().build();

        Map<String, Object> map = new HashMap<>();
        map.put("id", u.getId());
        map.put("prenom", u.getPrenom());
        map.put("nom", u.getNom());
        map.put("email", u.getEmail());
        map.put("telephone", u.getTelephone());
        map.put("role", u.getRole());

        if ("ROLE_PARENT".equals(u.getRole())) {
            Parent parent = parentService.getByUser(u);
            if (parent != null) {
                map.put("adresse", parent.getAdresse());
                map.put("parentId", parent.getId());
                List<Map<String, Object>> enfants = enfantService.getByParentId(parent.getId())
                        .stream().map(e -> {
                            Map<String, Object> em = new HashMap<>();
                            em.put("id", e.getId());
                            em.put("prenom", e.getPrenom());
                            em.put("nom", e.getNom());
                            em.put("dateNaissance", e.getDateNaissance() != null ? e.getDateNaissance().toString() : "");
                            em.put("nbSessions", inscriptionService.countByEnfant(e));
                            return em;
                        }).collect(java.util.stream.Collectors.toList());
                map.put("enfants", enfants);
            }
        }

        if ("ROLE_ANIMATEUR".equals(u.getRole())) {
            Animateur animateur = animateurService.getByUser(u);
            if (animateur != null) {
                map.put("diplome", animateur.getDiplome());
                map.put("specialite", animateur.getSpecialite());
                map.put("dateEmbauche", animateur.getDateEmbauche() != null ? animateur.getDateEmbauche().toString() : "");
                List<Map<String, Object>> sessions = sessionService.getByAnimateur(animateur)
                        .stream().map(s -> {
                            Map<String, Object> sm = new HashMap<>();
                            sm.put("id", s.getId());
                            sm.put("activiteTitre", s.getActivite() != null ? s.getActivite().getTitre() : "");
                            sm.put("dateDebut", s.getDateDebut() != null ? s.getDateDebut().toString() : "");
                            sm.put("lieu", s.getLieu());
                            sm.put("statut", s.getStatut());
                            return sm;
                        }).collect(java.util.stream.Collectors.toList());
                map.put("sessions", sessions);
            }
        }

        return ResponseEntity.ok(map);
    }

    @PostMapping("/users/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            userService.createUser(user);
            return ResponseEntity.ok("Utilisateur créé");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/users/{id}/update")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {
        User user = userService.getById(id);
        if (user == null) return ResponseEntity.notFound().build();

        user.setPrenom((String) payload.get("prenom"));
        user.setNom((String) payload.get("nom"));
        user.setEmail((String) payload.get("email"));
        user.setTelephone((String) payload.get("telephone"));
        user.setRole((String) payload.get("role"));

        String password = (String) payload.get("password");
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userService.save(user);
        return ResponseEntity.ok("Mis à jour");
    }
    // Dans AdminRestController.java
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("usersCount", adminDashboardService.countUsers());
        data.put("activitiesCount", adminDashboardService.countActivities());
        data.put("sessionsCount", adminDashboardService.countSessions());
        data.put("animateursCount", adminDashboardService.countAnimateurs());
        data.put("prevues", adminDashboardService.countPrevues());
        data.put("enCours", adminDashboardService.countEnCours());
        data.put("terminees", adminDashboardService.countTerminees());
        data.put("annulees", adminDashboardService.countAnnulees());
        data.put("enfantsCount", adminDashboardService.countEnfants());
        data.put("parentsCount", adminDashboardService.countParents());
        data.put("lastUpdate", adminDashboardService.lastUpdate());
        // Sessions simplifiées — pas d'objets imbriqués
        List<Map<String, Object>> recentSessions = adminDashboardService
                .getRecentSessions()
                .stream()
                .map(s -> {
                    Map<String, Object> session = new HashMap<>();
                    session.put("dateDebut", s.getDateDebut().toString());
                    session.put("statut", s.getStatut());
                    session.put("activiteTitre", s.getActivite() != null ? s.getActivite().getTitre() : "");
                    session.put("animateurNom", s.getAnimateur() != null && s.getAnimateur().getUser() != null
                            ? s.getAnimateur().getUser().getNom() : "");
                    return session;
                })
                .collect(java.util.stream.Collectors.toList());

        data.put("recentSessions", recentSessions);
        return ResponseEntity.ok(data);
    }
    // ================= ÉVÉNEMENTS =================

    @GetMapping("/events")
    public List<Event> getAllEvents() {
        return eventService.getAll();
    }

    @PostMapping("/events")
    public Event saveEvent(@RequestBody Event event) {
        return eventService.save(event);
    }
    // Dans AdminRestController.java
    @PostMapping("/events/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        String uploadDir = "uploads/events/";
        new File(uploadDir).mkdirs();

        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path path = Paths.get(uploadDir + filename);
        Files.write(path, file.getBytes());

        return ResponseEntity.ok("/uploads/events/" + filename);
    }

    @DeleteMapping("/events/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

// ================= INCIDENTS =================

    @GetMapping("/incidents")
    public List<IncidentHistoryService.IncidentHistoryItem> getAllIncidents() {
        // On réutilise directement ta méthode optimisée en SQL
        return incidentHistoryService.getAllIncidents();
    }

    @PostMapping("/incidents/{id}/vu")
    public ResponseEntity<Void> marquerIncidentVu(@PathVariable Integer id) {
        incidentHistoryService.marquerVuParParent(id);
        return ResponseEntity.ok().build();
    }

}