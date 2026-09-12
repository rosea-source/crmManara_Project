package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.Inscription;
import com.manara.crmmanarayumnahamphygabriel.model.Presence;
import com.manara.crmmanarayumnahamphygabriel.repository.InscriptionRepository;
import com.manara.crmmanarayumnahamphygabriel.repository.PresenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PresenceService {

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    // ── Utilisé pour charger la liste des participants ──
    private final JdbcTemplate jdbcTemplate;

    public PresenceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ── Liste des enfants d'une session ──────────────────
    public List<PresenceItem> getEnfantsBySession(Integer sessionId) {
        String sql = """
                SELECT i.id as inscription_id,
                       e.prenom,
                       e.nom
                FROM inscriptions i
                JOIN enfants e ON e.id = i.enfant_id
                WHERE i.session_id = ?
                ORDER BY e.nom ASC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new PresenceItem(
                        rs.getInt("inscription_id"),
                        rs.getString("prenom") + " " + rs.getString("nom")
                ), sessionId);
    }

    // ── Sauvegarder une présence ─────────────────────────
    public void savePresence(Integer inscriptionId, String statut, String note) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new RuntimeException("Inscription introuvable : " + inscriptionId));

        Presence presence = new Presence();
        presence.setInscription(inscription);
        presence.setStatut(statut != null ? statut : "Présent");
        presence.setNoteAnimateur(note != null ? note : "");

        presenceRepository.save(presence);
    }

    // ── Historique pour un animateur ─────────────────────
    public List<Presence> getHistoriqueByAnimateur(Integer animateurId) {
        return presenceRepository.findByAnimateurId(animateurId);
    }

    // ── Historique pour une session ──────────────────────
    public List<Presence> getBySession(Integer sessionId) {
        return presenceRepository.findBySessionId(sessionId);
    }
    public long countByEnfant(Integer enfantId) {
        String sql = """
            SELECT COUNT(*) FROM presences p
            JOIN inscriptions i ON i.id = p.inscription_id
            WHERE i.enfant_id = ?
            """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, enfantId);
        return count != null ? count : 0;
    }

    public long countAbsencesByEnfant(Integer enfantId) {
        String sql = """
            SELECT COUNT(*) FROM presences p
            JOIN inscriptions i ON i.id = p.inscription_id
            WHERE i.enfant_id = ?
            AND p.statut = 'Absent'
            """;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, enfantId);
        return count != null ? count : 0;
    }

    public record PresenceItem(Integer inscriptionId, String nomComplet) {}
}