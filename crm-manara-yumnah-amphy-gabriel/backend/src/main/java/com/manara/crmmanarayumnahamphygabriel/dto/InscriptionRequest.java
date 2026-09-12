package com.manara.crmmanarayumnahamphygabriel.dto;

import jakarta.validation.constraints.NotNull;

public class InscriptionRequest {

    @NotNull(message = "Session obligatoire")
    private Integer sessionId;

    @NotNull(message = "Veuillez sélectionner un enfant")
    private Integer enfantId;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getEnfantId() {
        return enfantId;
    }

    public void setEnfantId(Integer enfantId) {
        this.enfantId = enfantId;
    }
}