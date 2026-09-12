package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "presences")
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inscription_id", nullable = false)
    private Inscription inscription;

    @Column(nullable = false)
    private String statut = "Présent";

    @Column(name = "note_animateur", length = 255)
    private String noteAnimateur;

    public Presence() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Inscription getInscription() { return inscription; }
    public void setInscription(Inscription inscription) { this.inscription = inscription; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getNoteAnimateur() { return noteAnimateur; }
    public void setNoteAnimateur(String noteAnimateur) { this.noteAnimateur = noteAnimateur; }
}