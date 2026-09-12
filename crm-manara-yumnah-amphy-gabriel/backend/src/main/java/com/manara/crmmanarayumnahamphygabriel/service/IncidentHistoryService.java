package com.manara.crmmanarayumnahamphygabriel.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class IncidentHistoryService {

    private final JdbcTemplate jdbcTemplate;

    public IncidentHistoryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── ANIMATEUR ──────────────────────────────────────────
    public List<IncidentHistoryItem> getIncidentsByAnimateur(Integer animateurId) {
        String sql = """
                SELECT
                    i.id,
                    i.description,
                    i.gravite,
                    i.date_heure,
                    i.vu_par_parent,
                    e.prenom AS enfant_prenom,
                    e.nom AS enfant_nom,
                    a.titre AS activite_titre,
                    s.date_debut,
                    s.heure_debut
                FROM incidents i
                INNER JOIN enfants e ON e.id = i.enfant_id
                INNER JOIN sessions s ON s.id = i.session_id
                INNER JOIN activites a ON a.id = s.activite_id
                WHERE i.animateur_id = ?
                ORDER BY i.date_heure DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("date_heure");
            LocalDateTime dateHeure = timestamp != null ? timestamp.toLocalDateTime() : null;
            return new IncidentHistoryItem(
                    rs.getInt("id"),
                    rs.getString("enfant_prenom") + " " + rs.getString("enfant_nom"),
                    rs.getString("activite_titre"),
                    rs.getString("description"),
                    rs.getString("gravite"),
                    dateHeure,
                    rs.getBoolean("vu_par_parent")
            );
        }, animateurId);
    }

    // ── PARENT ─────────────────────────────────────────────
    public List<IncidentHistoryItem> getIncidentsByParent(Integer parentId) {
        String sql = """
                SELECT
                    i.id,
                    i.description,
                    i.gravite,
                    i.date_heure,
                    i.vu_par_parent,
                    e.prenom AS enfant_prenom,
                    e.nom AS enfant_nom,
                    a.titre AS activite_titre,
                    s.date_debut,
                    s.heure_debut
                FROM incidents i
                INNER JOIN enfants e ON e.id = i.enfant_id
                INNER JOIN sessions s ON s.id = i.session_id
                INNER JOIN activites a ON a.id = s.activite_id
                INNER JOIN parents p ON p.id = e.parent_id
                WHERE p.id = ?
                ORDER BY i.date_heure DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("date_heure");
            LocalDateTime dateHeure = timestamp != null ? timestamp.toLocalDateTime() : null;
            return new IncidentHistoryItem(
                    rs.getInt("id"),
                    rs.getString("enfant_prenom") + " " + rs.getString("enfant_nom"),
                    rs.getString("activite_titre"),
                    rs.getString("description"),
                    rs.getString("gravite"),
                    dateHeure,
                    rs.getBoolean("vu_par_parent")
            );
        }, parentId);
    }

    // ── ADMIN ──────────────────────────────────────────────
    public List<IncidentHistoryItem> getAllIncidents() {
        String sql = """
                SELECT
                    i.id,
                    i.description,
                    i.gravite,
                    i.date_heure,
                    i.vu_par_parent,
                    e.prenom AS enfant_prenom,
                    e.nom AS enfant_nom,
                    a.titre AS activite_titre,
                    s.date_debut,
                    s.heure_debut
                FROM incidents i
                INNER JOIN enfants e ON e.id = i.enfant_id
                INNER JOIN sessions s ON s.id = i.session_id
                INNER JOIN activites a ON a.id = s.activite_id
                ORDER BY i.date_heure DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("date_heure");
            LocalDateTime dateHeure = timestamp != null ? timestamp.toLocalDateTime() : null;
            return new IncidentHistoryItem(
                    rs.getInt("id"),
                    rs.getString("enfant_prenom") + " " + rs.getString("enfant_nom"),
                    rs.getString("activite_titre"),
                    rs.getString("description"),
                    rs.getString("gravite"),
                    dateHeure,
                    rs.getBoolean("vu_par_parent")
            );
        });
    }

    // ── MARQUER VU PAR PARENT ──────────────────────────────
    public void marquerVuParParent(Integer incidentId) {
        jdbcTemplate.update(
                "UPDATE incidents SET vu_par_parent = true WHERE id = ?",
                incidentId
        );
    }

    public record IncidentHistoryItem(
            Integer id,
            String enfantNomComplet,
            String activiteTitre,
            String description,
            String gravite,
            LocalDateTime dateHeure,
            Boolean vuParParent
    ) {
        public String getDateLabel() {
            if (dateHeure == null) return "";
            return dateHeure.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        public String getBadgeClass() {
            return switch (gravite) {
                case "Haute" -> "danger";
                case "Moyenne" -> "warning";
                default -> "primary";
            };
        }

        public String getEtatParentLabel() {
            return Boolean.TRUE.equals(vuParParent) ? "Vu" : "Non vu";
        }

        public String getEtatParentClass() {
            return Boolean.TRUE.equals(vuParParent) ? "success" : "secondary";
        }
    }
}