package com.manara.crmmanarayumnahamphygabriel.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "activites")
public class Activite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String titre;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int ageMin;

    @Column(nullable = false)
    private int ageMax;

    public Activite() {}

    public Activite(String titre, String description, int ageMin, int ageMax) {
        this.titre = titre;
        this.description = description;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
    }

    // Getters & Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAgeMin() { return ageMin; }
    public void setAgeMin(int ageMin) { this.ageMin = ageMin; }

    public int getAgeMax() { return ageMax; }
    public void setAgeMax(int ageMax) { this.ageMax = ageMax; }
}