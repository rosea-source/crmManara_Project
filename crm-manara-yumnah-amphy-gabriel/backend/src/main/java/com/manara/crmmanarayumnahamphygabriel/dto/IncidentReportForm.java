package com.manara.crmmanarayumnahamphygabriel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IncidentReportForm {

    @NotNull(message = "L'enfant est obligatoire")
    private Integer enfantId;

    @NotNull(message = "La session est obligatoire")
    private Integer sessionId;

    @NotBlank(message = "Le destinataire est obligatoire")
    private String destinataire;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 5, max = 1000, message = "La description doit contenir entre 5 et 1000 caractères")
    private String description;

    @NotBlank(message = "La gravité est obligatoire")
    private String gravite;

    public IncidentReportForm() {
    }

    public Integer getEnfantId() {
        return enfantId;
    }

    public void setEnfantId(Integer enfantId) {
        this.enfantId = enfantId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(String destinataire) {
        this.destinataire = destinataire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGravite() {
        return gravite;
    }

    public void setGravite(String gravite) {
        this.gravite = gravite;
    }
}