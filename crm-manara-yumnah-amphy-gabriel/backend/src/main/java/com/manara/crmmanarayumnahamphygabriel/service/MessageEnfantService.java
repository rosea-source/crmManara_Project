package com.manara.crmmanarayumnahamphygabriel.service;

import com.manara.crmmanarayumnahamphygabriel.model.*;
import com.manara.crmmanarayumnahamphygabriel.repository.MessageEnfantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageEnfantService {

    @Autowired
    private MessageEnfantRepository messageEnfantRepository;

    @Autowired
    private NotificationService notificationService;

    private final String UPLOAD_DIR = "uploads/messages/";

    public List<MessageEnfant> getBySession(Session session) {
        return messageEnfantRepository.findBySessionOrderByDateEnvoiDesc(session);
    }

    public List<MessageEnfant> getForEnfantInSession(Session session, Enfant enfant) {
        return messageEnfantRepository.findBySessionAndEnfantOrGlobal(session, enfant);
    }

    public List<MessageEnfant> getAllForEnfant(Enfant enfant, List<Session> sessions) {
        return messageEnfantRepository.findAllForEnfant(enfant, sessions);
    }

    public long countNonLus(Enfant enfant) {
        return messageEnfantRepository.countByEnfantAndLuFalse(enfant);
    }

    // ─────────────────────────────
    // POST MESSAGE + EMAIL
    // ─────────────────────────────
    public MessageEnfant poster(String contenu,
                                MultipartFile fichier,
                                Session session,
                                Enfant enfant,
                                Animateur animateur) throws IOException {

        MessageEnfant msg = new MessageEnfant();
        msg.setContenu(contenu);
        msg.setSession(session);
        msg.setEnfant(enfant);
        msg.setAnimateur(animateur);
        msg.setDateEnvoi(LocalDateTime.now());

        // fichier optionnel
        if (fichier != null && !fichier.isEmpty()) {
            String ext = getExtension(fichier.getOriginalFilename());
            String nomFichier = UUID.randomUUID() + ext;

            Path dossier = Paths.get(UPLOAD_DIR);
            Files.createDirectories(dossier);

            Files.copy(fichier.getInputStream(),
                    dossier.resolve(nomFichier),
                    StandardCopyOption.REPLACE_EXISTING);

            msg.setFichierNom(fichier.getOriginalFilename());
            msg.setFichierChemin(UPLOAD_DIR + nomFichier);
        }

        MessageEnfant saved = messageEnfantRepository.save(msg);

        // ─────────────────────────────
        // EMAIL NOTIFICATION
        // ─────────────────────────────
        if (enfant != null && enfant.getParent() != null) {
            notificationService.notifyMessageEnfant(
                    enfant.getParent().getUser(),
                    animateur.getUser().getNom(),
                    session.getActivite().getTitre(),
                    enfant.getPrenom(),
                    false
            );
        }

        return saved;
    }

    public void marquerLu(Integer messageId) {
        messageEnfantRepository.findById(messageId).ifPresent(m -> {
            m.setLu(true);
            messageEnfantRepository.save(m);
        });
    }

    public void delete(Integer id) {
        messageEnfantRepository.deleteById(id);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}