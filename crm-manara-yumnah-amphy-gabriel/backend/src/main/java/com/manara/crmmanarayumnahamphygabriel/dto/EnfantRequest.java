package com.manara.crmmanarayumnahamphygabriel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class EnfantRequest {

    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @NotNull
    private LocalDate dateNaissance;

    private String allergies;
    private String notesMedicales;

    public EnfantRequest() {}

    public String getPrenom() { return prenom; }
    public String getNom() { return nom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public String getAllergies() { return allergies; }
    public String getNotesMedicales() { return notesMedicales; }

    public void setPrenom(String prenom) { this.prenom = prenom; }
    public void setNom(String nom) { this.nom = nom; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public void setNotesMedicales(String notesMedicales) { this.notesMedicales = notesMedicales; }
}