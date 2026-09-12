package com.manara.crmmanarayumnahamphygabriel.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnimateurActiviteService {

    private final JdbcTemplate jdbcTemplate;

    public AnimateurActiviteService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ActiviteItem> getHistorique(Integer animateurId) {
        List<ActiviteItem> incidents = loadIncidents(animateurId);
        List<ActiviteItem> sessions = loadSessions(animateurId);

        return List.of(incidents, sessions)
                .stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ActiviteItem::dateHeure).reversed())
                .collect(Collectors.toList());
    }

    public Map<String, List<ActiviteItem>> getHistoriqueGroupeParJour(Integer animateurId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        return getHistorique(animateurId).stream()
                .collect(Collectors.groupingBy(
                        item -> {
                            LocalDate date = item.dateHeure().toLocalDate();
                            if (date.equals(LocalDate.now())) {
                                return "Aujourd'hui";
                            }
                            if (date.equals(LocalDate.now().minusDays(1))) {
                                return "Hier";
                            }
                            return date.format(formatter);
                        },
                        java.util.LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private List<ActiviteItem> loadIncidents(Integer animateurId) {
        String sql = """
                SELECT
                    i.id AS ref_id,
                    i.date_heure AS event_date,
                    'INCIDENT' AS type_evenement,
                    i.description AS contenu,
                    i.gravite AS niveau,
                    e.prenom AS enfant_prenom,
                    e.nom AS enfant_nom
                FROM incidents i
                INNER JOIN enfants e ON e.id = i.enfant_id
                WHERE i.animateur_id = ?
                ORDER BY i.date_heure DESC
                LIMIT 20
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("event_date");
            String gravite = rs.getString("niveau");
            String icon = switch (gravite) {
                case "Haute" -> "bi-exclamation-triangle-fill";
                case "Moyenne" -> "bi-exclamation-circle-fill";
                default -> "bi-info-circle-fill";
            };
            String borderClass = switch (gravite) {
                case "Haute" -> "danger";
                case "Moyenne" -> "warning";
                default -> "primary";
            };
            String badgeClass = switch (gravite) {
                case "Haute" -> "danger";
                case "Moyenne" -> "warning";
                default -> "primary";
            };

            String enfant = rs.getString("enfant_prenom") + " " + rs.getString("enfant_nom");

            return new ActiviteItem(
                    "Incident déclaré",
                    "Incident concernant <strong>" + enfant + "</strong> : " + rs.getString("contenu"),
                    gravite,
                    icon,
                    borderClass,
                    badgeClass,
                    ts.toLocalDateTime(),
                    "/animateur/incidents/report"
            );
        }, animateurId);
    }

    private List<ActiviteItem> loadSessions(Integer animateurId) {
        String sql = """
                SELECT
                    s.id AS ref_id,
                    s.date_debut,
                    s.heure_debut,
                    s.statut,
                    a.titre
                FROM sessions s
                INNER JOIN activites a ON a.id = s.activite_id
                WHERE s.animateur_id = ?
                ORDER BY s.date_debut DESC, s.heure_debut DESC
                LIMIT 20
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            LocalDate dateDebut = rs.getDate("date_debut").toLocalDate();
            LocalDateTime dateHeure = dateDebut.atTime(rs.getTime("heure_debut").toLocalTime());
            String statut = rs.getString("statut");
            String titre = rs.getString("titre");

            String icon = switch (statut) {
                case "Annulée" -> "bi-x-circle-fill";
                case "Terminée" -> "bi-check-circle-fill";
                case "En cours" -> "bi-play-circle-fill";
                default -> "bi-calendar-event-fill";
            };

            String borderClass = switch (statut) {
                case "Annulée" -> "danger";
                case "Terminée" -> "success";
                case "En cours" -> "primary";
                default -> "secondary";
            };

            String badgeClass = borderClass;

            return new ActiviteItem(
                    "Session " + statut,
                    "L'activité <strong>" + titre + "</strong> est marquée comme <strong>" + statut + "</strong>.",
                    statut,
                    icon,
                    borderClass,
                    badgeClass,
                    dateHeure,
                    "/animateur/planning"
            );
        }, animateurId);
    }

    public record ActiviteItem(
            String titre,
            String contenuHtml,
            String badgeLabel,
            String iconClass,
            String borderClass,
            String badgeClass,
            LocalDateTime dateHeure,
            String detailsUrl
    ) {
        public String heureLabel() {
            return dateHeure.format(DateTimeFormatter.ofPattern("HH:mm"));
        }
    }
}