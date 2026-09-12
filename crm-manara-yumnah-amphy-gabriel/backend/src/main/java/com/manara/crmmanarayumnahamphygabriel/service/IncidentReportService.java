package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.dto.IncidentReportForm;
import com.manara.crmmanarayumnahamphygabriel.repository.ParentRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Service
public class IncidentReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParentRepository parentRepository;

    // ─────────────────────────────
    // ENFANTS
    // ─────────────────────────────
    public List<EnfantOption> getEnfantsByAnimateur(Integer animateurId) {

        String sql = """
            SELECT e.id, e.prenom, e.nom, i.session_id
            FROM enfants e
            INNER JOIN inscriptions i ON i.enfant_id = e.id
            INNER JOIN sessions s ON s.id = i.session_id
            WHERE s.animateur_id = ?
            ORDER BY e.prenom, e.nom
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new EnfantOption(
                        rs.getInt("id"),
                        rs.getString("prenom"),
                        rs.getString("nom"),
                        rs.getInt("session_id")
                ),
                animateurId);
    }

    // ─────────────────────────────
    // SESSIONS
    // ─────────────────────────────
    public List<SessionOption> getSessionsByAnimateur(Integer animateurId) {

        String sql = """
            SELECT s.id, a.titre, s.date_debut, s.heure_debut, s.lieu
            FROM sessions s
            INNER JOIN activites a ON a.id = s.activite_id
            WHERE s.animateur_id = ?
            ORDER BY s.date_debut DESC
        """;

        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new SessionOption(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        Objects.toString(rs.getDate("date_debut"), ""),
                        Objects.toString(rs.getTime("heure_debut"), ""),
                        rs.getString("lieu")
                ),
                animateurId);
    }

    // ─────────────────────────────
    // REPORT INCIDENT
    // ─────────────────────────────
    @Transactional
    public void reportIncident(Integer senderUserId,
                               Integer animateurId,
                               IncidentReportForm form) {

        validateOwnership(animateurId, form.getSessionId(), form.getEnfantId());

        Integer incidentId = insertIncident(animateurId, form);

        String message = "Incident #" + incidentId + " - " + form.getDescription();

        // ─────────────────────────────
        // EMAIL ADMINS
        // ─────────────────────────────
        if ("admin".equals(form.getDestinataire()) || "all".equals(form.getDestinataire())) {

            userRepository.findAll().stream()
                    .filter(u -> "ROLE_ADMIN".equals(u.getRole()))
                    .forEach(admin ->
                            notificationService.notifyIncident(
                                    admin,
                                    "incident #" + incidentId,
                                    form.getGravite()
                            )
                    );
        }

        // ─────────────────────────────
        // EMAIL PARENT
        // ─────────────────────────────
        if ("parent".equals(form.getDestinataire()) || "all".equals(form.getDestinataire())) {

            Integer parentUserId = jdbcTemplate.query("""
                    SELECT u.id
                    FROM enfants e
                    INNER JOIN parents p ON p.id = e.parent_id
                    INNER JOIN users u ON u.id = p.user_id
                    WHERE e.id = ?
                """,
                    rs -> rs.next() ? rs.getInt(1) : null,
                    form.getEnfantId()
            );

            if (parentUserId != null) {
                userRepository.findById(parentUserId).ifPresent(parentUser ->
                        notificationService.notifyIncident(
                                parentUser,
                                "votre enfant",
                                form.getGravite()
                        )
                );
            }
        }
    }

    // ─────────────────────────────
    // VALIDATION
    // ─────────────────────────────
    private void validateOwnership(Integer animateurId, Integer sessionId, Integer enfantId) {

        String sql = """
            SELECT COUNT(*)
            FROM inscriptions i
            INNER JOIN sessions s ON s.id = i.session_id
            WHERE s.id = ?
              AND s.animateur_id = ?
              AND i.enfant_id = ?
        """;

        Integer count = jdbcTemplate.queryForObject(sql,
                Integer.class,
                sessionId,
                animateurId,
                enfantId);

        if (count == null || count == 0) {
            throw new IllegalArgumentException("Enfant non valide pour cette session");
        }
    }

    // ─────────────────────────────
    // INSERT INCIDENT
    // ─────────────────────────────
    private Integer insertIncident(Integer animateurId, IncidentReportForm form) {

        String sql = """
            INSERT INTO incidents
            (enfant_id, session_id, animateur_id, description, gravite, mesures_prises, vu_par_parent)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, form.getEnfantId());
            ps.setInt(2, form.getSessionId());
            ps.setInt(3, animateurId);
            ps.setString(4, form.getDescription());
            ps.setString(5, form.getGravite());
            ps.setString(6, null);
            ps.setBoolean(7, false);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().intValue();
    }

    // ─────────────────────────────
    // RECORDS
    // ─────────────────────────────
    public record EnfantOption(Integer id, String prenom, String nom, Integer sessionId) {
        public String getLabel() {
            return prenom + " " + nom;
        }
    }

    public record SessionOption(Integer id, String titre, String dateDebut, String heureDebut, String lieu) {
        public String getLabel() {
            return titre + " - " + dateDebut;
        }
    }
    @Transactional
    public void deleteIncident(Integer incidentId, Integer animateurId) {
        // Vérifie que l'incident appartient bien à cet animateur
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM incidents WHERE id = ? AND animateur_id = ?",
                Integer.class, incidentId, animateurId
        );
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Incident introuvable ou accès refusé");
        }
        jdbcTemplate.update("DELETE FROM incidents WHERE id = ?", incidentId);
    }
}