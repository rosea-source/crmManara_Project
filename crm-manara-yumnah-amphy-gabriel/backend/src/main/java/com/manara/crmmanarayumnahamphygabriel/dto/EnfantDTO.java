package com.manara.crmmanarayumnahamphygabriel.dto;

import java.time.LocalDate;

public class EnfantDTO {

    private Integer id;
    private String prenom;
    private String nom;
    private LocalDate dateNaissance;
    private String allergies;
    private String notesMedicales;

    public EnfantDTO() {}

    public EnfantDTO(Integer id, String prenom, String nom, LocalDate dateNaissance,
                     String allergies, String notesMedicales) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.dateNaissance = dateNaissance;
        this.allergies = allergies;
        this.notesMedicales = notesMedicales;
    }

    public Integer getId() { return id; }
    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getAllergies() { return allergies; }
    public String getNotesMedicales() { return notesMedicales; }

    public void setId(Integer id) { this.id = id; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setNotesMedicales(String notesMedicales) { this.notesMedicales = notesMedicales; }
}