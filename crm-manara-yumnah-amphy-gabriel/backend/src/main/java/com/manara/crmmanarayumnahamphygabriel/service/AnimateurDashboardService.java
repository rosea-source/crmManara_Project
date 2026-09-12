package com.manara.crmmanarayumnahamphygabriel.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AnimateurDashboardService {

    private final JdbcTemplate jdbcTemplate;

    public AnimateurDashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardStats getStats(Integer animateurId) {
        int sessionsActives = countSessionsActives(animateurId);
        int enfantsInscrits = countEnfantsInscritsSessionsActives(animateurId);
        String prochaineSession = findProchaineSessionLabel(animateurId);

        return new DashboardStats(sessionsActives, enfantsInscrits, prochaineSession);
    }

    private int countSessionsActives(Integer animateurId) {
        // "Active" = statut En cours OU Prévue, sans condition de date
        String sql = """
                SELECT COUNT(*)
                FROM sessions s
                WHERE s.animateur_id = ?
                  AND s.statut IN ('En cours', 'Prévue')
                """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, animateurId);
        return result != null ? result : 0;
    }

    private int countEnfantsInscritsSessionsActives(Integer animateurId) {
        // Enfants inscrits dans les sessions En cours ou Prévue
        String sql = """
                SELECT COUNT(i.id)
                FROM inscriptions i
                INNER JOIN sessions s ON s.id = i.session_id
                WHERE s.animateur_id = ?
                  AND s.statut IN ('En cours', 'Prévue')
                """;

        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, animateurId);
        return result != null ? result : 0;
    }

    private String findProchaineSessionLabel(Integer animateurId) {
        // Prochaine session = la plus proche dans le futur (ou aujourd'hui) parmi Prévue et En cours
        String sql = """
                SELECT s.date_debut, s.heure_debut, a.titre
                FROM sessions s
                INNER JOIN activites a ON a.id = s.activite_id
                WHERE s.animateur_id = ?
                  AND s.statut IN ('En cours', 'Prévue')
                  AND (
                        s.date_debut > CURDATE()
                        OR (s.date_debut = CURDATE() AND s.heure_debut >= CURTIME())
                  )
                ORDER BY s.date_debut ASC, s.heure_debut ASC
                LIMIT 1
                """;

        List<ProchaineSessionRow> rows = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new ProchaineSessionRow(
                        rs.getDate("date_debut").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getString("titre")
                ),
                animateurId
        );

        if (rows.isEmpty()) {
            return "Aucune session";
        }

        ProchaineSessionRow row = rows.get(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return row.titre() + " - " + row.dateDebut().format(dateFormatter)
                + " à " + row.heureDebut().format(timeFormatter);
    }

    public record DashboardStats(int sessionsActives, int enfantsInscrits, String prochaineSession) {}

    private record ProchaineSessionRow(LocalDate dateDebut, LocalTime heureDebut, String titre) {}
}