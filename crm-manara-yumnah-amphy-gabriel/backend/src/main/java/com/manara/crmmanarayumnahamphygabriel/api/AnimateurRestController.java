package com.manara.crmmanarayumnahamphygabriel.api;

import com.manara.crmmanarayumnahamphygabriel.dto.IncidentReportForm;
import com.manara.crmmanarayumnahamphygabriel.model.*;
import com.manara.crmmanarayumnahamphygabriel.service.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/animateur")
public class AnimateurRestController {

    private final AnimateurDashboardService dashboardService;
    private final AnimateurActiviteService activiteService;
    private final AnimateurService animateurService;
    private final UserService userService;
    private final PresenceService presenceService;
    private final SessionService sessionService;
    private final InscriptionService inscriptionService;
    private final EnfantService enfantService;
    private final MessageEnfantService messageEnfantService;
    private final IncidentHistoryService incidentHistoryService;
    private final IncidentReportService incidentReportService;

    public AnimateurRestController(
            AnimateurDashboardService dashboardService,
            AnimateurActiviteService activiteService,
            AnimateurService animateurService,
            UserService userService,
            PresenceService presenceService,
            SessionService sessionService,
            InscriptionService inscriptionService,
            EnfantService enfantService,
            MessageEnfantService messageEnfantService,
            IncidentHistoryService incidentHistoryService,
            IncidentReportService incidentReportService
    ) {
        this.dashboardService = dashboardService;
        this.activiteService = activiteService;
        this.animateurService = animateurService;
        this.userService = userService;
        this.presenceService = presenceService;
        this.sessionService = sessionService;
        this.inscriptionService = inscriptionService;
        this.enfantService = enfantService;
        this.messageEnfantService = messageEnfantService;
        this.incidentHistoryService = incidentHistoryService;
        this.incidentReportService = incidentReportService;
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private Animateur resolveAnimateur(UserDetails userDetails) {
        Optional<User> opt = userService.findUserByEmail(userDetails.getUsername());
        if (opt.isEmpty()) return null;
        return animateurService.getByUser(opt.get());
    }

    // ─── DASHBOARD ───────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    public ResponseEntity<AnimateurDashboardDTO> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        Optional<User> opt = userService.findUserByEmail(userDetails.getUsername());
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        User user = opt.get();
        Animateur animateur = animateurService.getByUser(user);
        if (animateur == null) return ResponseEntity.notFound().build();

        AnimateurDashboardService.DashboardStats stats =
                dashboardService.getStats(animateur.getId());

        return ResponseEntity.ok(new AnimateurDashboardDTO(
                user.getPrenom(),
                stats.sessionsActives(),
                stats.enfantsInscrits(),
                stats.prochaineSession()
        ));
    }

    // ─── ACTIVITÉS ───────────────────────────────────────────────────────────

    @GetMapping("/activites")
    public ResponseEntity<Map<String, List<ActiviteItemDTO>>> getActivites(
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        Map<String, List<AnimateurActiviteService.ActiviteItem>> raw =
                activiteService.getHistoriqueGroupeParJour(animateur.getId());

        Map<String, List<ActiviteItemDTO>> dto = new LinkedHashMap<>();
        raw.forEach((jour, items) ->
                dto.put(jour, items.stream().map(ActiviteItemDTO::from).toList()));

        return ResponseEntity.ok(dto);
    }

    // ─── PRÉSENCES : historique ───────────────────────────────────────────────

    @GetMapping("/presences/historique")
    public ResponseEntity<List<PresenceDTO>> getHistoriquePresences(
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        List<PresenceDTO> list = presenceService
                .getHistoriqueByAnimateur(animateur.getId())
                .stream()
                .map(PresenceDTO::from)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ─── PRÉSENCES : participants d'une session ───────────────────────────────

    @GetMapping("/sessions/{sessionId}/participants")
    public ResponseEntity<List<PresenceService.PresenceItem>> getParticipants(
            @PathVariable Integer sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(presenceService.getEnfantsBySession(sessionId));
    }

    // ─── PRÉSENCES : enregistrer l'appel ─────────────────────────────────────

    @PostMapping("/sessions/{sessionId}/presences")
    public ResponseEntity<Void> savePresences(
            @PathVariable Integer sessionId,
            @RequestBody List<PresenceSaveRequest> requests,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        requests.forEach(r ->
                presenceService.savePresence(r.inscriptionId(), r.statut(), r.note()));

        return ResponseEntity.ok().build();
    }

    // ─── SESSIONS de l'animateur ─────────────────────────────────────────────

    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDTO>> getSessions(
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        List<SessionDTO> list = sessionService.getByAnimateur(animateur)
                .stream()
                .map(SessionDTO::from)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ─── ZONE ENFANT : détail ─────────────────────────────────────────────────

    @GetMapping("/sessions/{sessionId}/zone-enfant")
    public ResponseEntity<ZoneEnfantDTO> getZoneEnfant(
            @PathVariable Integer sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        Session session = sessionService.getById(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        String titreActivite = session.getActivite() != null
                ? session.getActivite().getTitre() : "Activité";

        List<Inscription> inscriptions = inscriptionService.getBySession(session);
        List<EnfantSimpleDTO> enfants = inscriptions.stream()
                .map(i -> new EnfantSimpleDTO(
                        i.getEnfant().getId(),
                        i.getEnfant().getPrenom() + " " + i.getEnfant().getNom()))
                .toList();

        List<MessageEnfantDTO> messages = messageEnfantService.getBySession(session)
                .stream().map(MessageEnfantDTO::from).toList();

        return ResponseEntity.ok(new ZoneEnfantDTO(
                sessionId, titreActivite,
                session.getDateDebut() != null ? session.getDateDebut().toString() : "",
                enfants, messages));
    }

    // ─── ZONE ENFANT : poster ─────────────────────────────────────────────────

    @PostMapping("/sessions/{sessionId}/zone-enfant/poster")
    public ResponseEntity<Void> posterMessage(
            @PathVariable Integer sessionId,
            @RequestParam("contenu") String contenu,
            @RequestParam(name = "enfantIdStr", required = false, defaultValue = "") String enfantIdStr,
            @RequestParam(name = "fichier", required = false) MultipartFile fichier,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        Session session = sessionService.getById(sessionId);
        if (session == null) return ResponseEntity.notFound().build();

        Enfant enfant = null;
        if (enfantIdStr != null && !enfantIdStr.isBlank() && !enfantIdStr.equalsIgnoreCase("null")) {
            try { enfant = enfantService.getById(Integer.parseInt(enfantIdStr.trim())); }
            catch (NumberFormatException ignored) {}
        }

        MultipartFile fichierValide = (fichier != null && !fichier.isEmpty()) ? fichier : null;
        messageEnfantService.poster(contenu, fichierValide, session, enfant, animateur);

        return ResponseEntity.ok().build();
    }

    // ─── ZONE ENFANT : supprimer message ─────────────────────────────────────

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> supprimerMessage(
            @PathVariable Integer messageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        messageEnfantService.delete(messageId);
        return ResponseEntity.ok().build();
    }

    // ─── INCIDENTS : historique ───────────────────────────────────────────────

    @GetMapping("/incidents/history")
    public ResponseEntity<List<IncidentHistoryDTO>> getIncidentHistory(
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        List<IncidentHistoryDTO> list = incidentHistoryService
                .getIncidentsByAnimateur(animateur.getId())
                .stream()
                .map(IncidentHistoryDTO::from)
                .toList();

        return ResponseEntity.ok(list);
    }

    // ─── INCIDENTS : données formulaire de signalement ───────────────────────

    @GetMapping("/incidents/form-data")
    public ResponseEntity<IncidentFormDataDTO> getIncidentFormData(
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        return ResponseEntity.ok(new IncidentFormDataDTO(
                incidentReportService.getEnfantsByAnimateur(animateur.getId()),
                incidentReportService.getSessionsByAnimateur(animateur.getId())
        ));
    }

    // ─── INCIDENTS : soumettre un signalement ────────────────────────────────

    @PostMapping("/incidents/report")
    public ResponseEntity<Void> reportIncident(
            @RequestBody @Valid IncidentReportForm form,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        incidentReportService.reportIncident(
                animateur.getUser().getId(),
                animateur.getId(),
                form);

        return ResponseEntity.ok().build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DTOs
    // ═══════════════════════════════════════════════════════════════════════

    public record AnimateurDashboardDTO(
            String prenomAnimateur, int sessionsActives,
            int enfantsInscrits, String prochaineSession) {}

    public record ActiviteItemDTO(
            String titre, String contenuHtml, String badgeLabel,
            String iconClass, String borderClass, String badgeClass,
            String dateHeure, String heureLabel, String detailsUrl) {
        public static ActiviteItemDTO from(AnimateurActiviteService.ActiviteItem item) {
            return new ActiviteItemDTO(
                    item.titre(), item.contenuHtml(), item.badgeLabel(),
                    item.iconClass(), item.borderClass(), item.badgeClass(),
                    item.dateHeure().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    item.heureLabel(), item.detailsUrl());
        }
    }

    public record PresenceDTO(
            Integer id, String enfantNom, String activiteTitre,
            String sessionDate, String statut, String noteAnimateur) {
        public static PresenceDTO from(Presence p) {
            Inscription insc = p.getInscription();
            String enfant = insc.getEnfant().getPrenom() + " " + insc.getEnfant().getNom();
            String activite = insc.getSession().getActivite() != null
                    ? insc.getSession().getActivite().getTitre() : "—";
            String date = insc.getSession().getDateDebut() != null
                    ? insc.getSession().getDateDebut().toString() : "—";
            return new PresenceDTO(p.getId(), enfant, activite, date,
                    p.getStatut(),
                    p.getNoteAnimateur() != null && !p.getNoteAnimateur().isEmpty()
                            ? p.getNoteAnimateur() : "—");
        }
    }

    public record PresenceSaveRequest(Integer inscriptionId, String statut, String note) {}

    public record SessionDTO(
            Integer id, String titre, String dateDebut, String dateFin,
            String heureDebut, String heureFin, String lieu, String statut) {
        public static SessionDTO from(Session s) {
            return new SessionDTO(s.getId(),
                    s.getActivite() != null ? s.getActivite().getTitre() : "—",
                    s.getDateDebut() != null ? s.getDateDebut().toString() : null,
                    s.getDateFin() != null ? s.getDateFin().toString() : null,
                    s.getHeureDebut() != null ? s.getHeureDebut().toString() : null,
                    s.getHeureFin() != null ? s.getHeureFin().toString() : null,
                    s.getLieu(), s.getStatut());
        }
    }

    public record EnfantSimpleDTO(Integer id, String nomComplet) {}

    public record MessageEnfantDTO(
            Integer id, String contenu, String fichierNom, String fichierChemin,
            String dateEnvoi, String enfantNom, boolean lu) {
        public static MessageEnfantDTO from(MessageEnfant m) {
            String enfantNom = m.getEnfant() != null
                    ? m.getEnfant().getPrenom() + " " + m.getEnfant().getNom() : null;
            return new MessageEnfantDTO(m.getId(), m.getContenu(),
                    m.getFichierNom(), m.getFichierChemin(),
                    m.getDateEnvoi() != null
                            ? m.getDateEnvoi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "—",
                    enfantNom, m.isLu());
        }
    }

    public record ZoneEnfantDTO(
            Integer sessionId, String titreActivite, String dateDebut,
            List<EnfantSimpleDTO> enfants, List<MessageEnfantDTO> messages) {}

    public record IncidentHistoryDTO(
            Integer id, String enfantNomComplet, String activiteTitre,
            String description, String gravite, String dateLabel,
            String badgeClass, String etatParentLabel, String etatParentClass) {
        public static IncidentHistoryDTO from(IncidentHistoryService.IncidentHistoryItem item) {
            return new IncidentHistoryDTO(
                    item.id(), item.enfantNomComplet(), item.activiteTitre(),
                    item.description(), item.gravite(),
                    item.getDateLabel(), item.getBadgeClass(),
                    item.getEtatParentLabel(), item.getEtatParentClass());
        }
    }
    @DeleteMapping("/incidents/{id}")
    public ResponseEntity<Void> deleteIncident(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Animateur animateur = resolveAnimateur(userDetails);
        if (animateur == null) return ResponseEntity.notFound().build();

        incidentReportService.deleteIncident(id, animateur.getId());
        return ResponseEntity.ok().build();
    }

    public record IncidentFormDataDTO(
            List<IncidentReportService.EnfantOption> enfants,
            List<IncidentReportService.SessionOption> sessions) {}
}