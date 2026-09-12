package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private EmailService emailService;

    // ─────────────────────────────
    // 1. BIENVENUE INSCRIPTION
    // ─────────────────────────────
    @Async
    public void notifyWelcome(User user, String prenom) {
        String subject = "Bienvenue sur Manara 🎉";
        String body =
                "Bonjour " + prenom + ",\n\n" +
                        "Bienvenue sur la plateforme Manara !\n" +
                        "Nous sommes ravis de vous compter parmi nous.\n\n" +
                        "Vous pouvez désormais suivre les activités et la progression de vos enfants.\n\n" +
                        "À très bientôt,\nL'équipe Manara";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // ─────────────────────────────
    // 2. INSCRIPTION ENFANT ACTIVITÉ
    // ─────────────────────────────
    @Async
    public void notifyInscription(User user,
                                  String enfant,
                                  String activite,
                                  String dateDebut,
                                  String dateFin,
                                  String heureDebut,
                                  String heureFin,
                                  String lieu) {

        String subject = "Inscription confirmée 🎉 — " + activite;

        String lieuInfo    = (lieu != null && !lieu.isBlank())       ? lieu    : "Non précisé";
        String dateFinInfo = (dateFin != null && !dateFin.isBlank()) ? dateFin : dateDebut;

        String body =
                "Bonjour,\n\n" +
                        "Bonne nouvelle ! " + enfant + " a été inscrit à l'activité suivante :\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  Activité  : " + activite   + "\n" +
                        "  Début     : " + dateDebut  + "\n" +
                        "  Fin       : " + dateFinInfo + "\n" +
                        "  Horaires  : " + heureDebut + " → " + heureFin + "\n" +
                        "  Lieu      : " + lieuInfo   + "\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Vous pouvez suivre son évolution directement sur la plateforme.\n\n" +
                        "À bientôt,\nL'équipe Manara";

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // ─────────────────────────────
    // 3. INCIDENT
    // ─────────────────────────────
    @Async
    public void notifyIncident(User user, String enfant, String gravite) {
        String subject = "Incident signalé ⚠️";
        String body =
                "Bonjour,\n\n" +
                        "Un incident de gravité " + gravite +
                        " a été signalé pour " + enfant + ".\n\n" +
                        "Veuillez consulter la plateforme pour plus de détails.\n\n" +
                        "Manara";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // ─────────────────────────────
    // 4. MESSAGE ENFANT
    // ─────────────────────────────
    @Async
    public void notifyMessageEnfant(User user,
                                    String animateurNom,
                                    String sessionTitre,
                                    String enfantNom,
                                    boolean global) {
        String subject = "Nouveau message de l'animateur 💬";
        String cible = global ? "la session " + sessionTitre : enfantNom;
        String body =
                "Bonjour,\n\n" +
                        "Un nouveau message a été publié par " + animateurNom + ".\n\n" +
                        "Concernant : " + cible + ".\n\n" +
                        "Veuillez vous connecter à la plateforme pour consulter le contenu.\n\n" +
                        "Manara";
        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // ─────────────────────────────
    // 5. CONTACT ADMIN
    // ─────────────────────────────
    @Async
    public void sendContactMessage(User user, String sujet, String message) {
        String body =
                "- Nouveau message de contact\n\n" +
                        "-- Utilisateur : " + user.getNom() + " " + user.getPrenom() + "\n" +
                        "-- Email : " + user.getEmail() + "\n\n" +
                        "-- Sujet : " + sujet + "\n\n" +
                        "-- Message :\n" + message;
        emailService.sendEmail("adminmanara@gmail.com", "Contact: " + sujet, body);
    }

    // ─────────────────────────────
    // 6. ASSIGNATION SESSION ANIMATEUR ← nouveau
    // ─────────────────────────────
    @Async
    public void notifySessionAssignee(User animateurUser,
                                      String activiteTitre,
                                      String dateDebut,
                                      String dateFin,
                                      String heureDebut,
                                      String heureFin,
                                      String lieu,
                                      Integer capaciteMax) {
        String subject = "📅 Nouvelle session assignée — " + activiteTitre;

        String lieuInfo   = (lieu != null && !lieu.isBlank()) ? lieu : "Non précisé";
        String dateFinInfo = (dateFin != null && !dateFin.isBlank()) ? dateFin : dateDebut;
        String capaciteInfo = (capaciteMax != null) ? capaciteMax + " enfants" : "Non précisée";

        String body =
                "Bonjour " + animateurUser.getPrenom() + ",\n\n" +
                        "Une nouvelle session vous a été assignée sur la plateforme Manara.\n\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "  Activité  : " + activiteTitre + "\n" +
                        "  Début     : " + dateDebut + "\n" +
                        "  Fin       : " + dateFinInfo + "\n" +
                        "  Horaires  : " + heureDebut + " → " + heureFin + "\n" +
                        "  Lieu      : " + lieuInfo + "\n" +
                        "  Capacité  : " + capaciteInfo + "\n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                        "Connectez-vous à la plateforme pour consulter les détails et gérer les présences.\n\n" +
                        "À bientôt,\nL'équipe Manara";

        emailService.sendEmail(animateurUser.getEmail(), subject, body);
    }
}