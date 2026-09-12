package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "enfants")
public class Enfant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private Parent parent;

    @Column(length = 255)
    private String allergies;

    @Column(name = "notes_medicales", columnDefinition = "TEXT")
    private String notesMedicales;

    public Enfant(){}

    public Enfant(String nom, String prenom, LocalDate dateNaissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
    }

    public Integer getId() { return id; }

    public String getNom() { return nom; }

    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }

    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }

    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Parent getParent() { return parent; }

    public void setParent(Parent parent) { this.parent = parent; }

    public String getAllergies() { return allergies; }

    public void setAllergies(String allergies) { this.allergies = allergies; }

    public String getNotesMedicales() { return notesMedicales; }

    public void setNotesMedicales(String notesMedicales) { this.notesMedicales = notesMedicales; }

    public int calculerAge() {
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}