package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages_enfant")
public class MessageEnfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    // Nom du fichier uploadé (optionnel)
    @Column(name = "fichier_nom")
    private String fichierNom;

    // Chemin de stockage sur le serveur
    @Column(name = "fichier_chemin")
    private String fichierChemin;

    @Column(name = "date_envoi", nullable = false)
    private LocalDateTime dateEnvoi;

    // Lié à une session (obligatoire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    // Lié à un enfant spécifique (optionnel — null = message pour toute la session)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enfant_id")
    private Enfant enfant;

    // Animateur qui a posté
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animateur_id", nullable = false)
    private Animateur animateur;

    @Column(name = "lu", nullable = false)
    private boolean lu = false;

    public MessageEnfant() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public String getFichierNom() { return fichierNom; }
    public void setFichierNom(String fichierNom) { this.fichierNom = fichierNom; }

    public String getFichierChemin() { return fichierChemin; }
    public void setFichierChemin(String fichierChemin) { this.fichierChemin = fichierChemin; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public Session getSession() { return session; }
    public void setSession(Session session) { this.session = session; }

    public Enfant getEnfant() { return enfant; }
    public void setEnfant(Enfant enfant) { this.enfant = enfant; }

    public Animateur getAnimateur() { return animateur; }
    public void setAnimateur(Animateur animateur) { this.animateur = animateur; }

    public boolean isLu() { return lu; }
    public void setLu(boolean lu) { this.lu = lu; }
}