package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="sessions")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "activite_id", nullable = false)
    private Activite activite;

    @ManyToOne
    @JoinColumn(name = "animateur_id", nullable = false)
    @JsonIgnore
    private Animateur animateur;

    @Column(name="date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name="date_fin")
    private LocalDate dateFin;

    @Column(name="heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name="heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(length = 100)
    private String lieu;

    @Column(name = "capacite_max")
    private Integer capaciteMax = 15;

    @Column(length = 20)
    private String statut = "Prévue";

    public Session() {
    }

    public Session(Activite activite, Animateur animateur,
                   LocalDate dateDebut, LocalDate dateFin,
                   LocalTime heureDebut, LocalTime heureFin,
                   String lieu, Integer capaciteMax, String statut) {

        this.activite = activite;
        this.animateur = animateur;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
        this.lieu = lieu;
        this.capaciteMax = capaciteMax;
        this.statut = statut;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Activite getActivite() {
        return activite;
    }

    public void setActivite(Activite activite) {
        this.activite = activite;
    }

    public Animateur getAnimateur() {
        return animateur;
    }

    public void setAnimateur(Animateur animateur) {
        this.animateur = animateur;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
}