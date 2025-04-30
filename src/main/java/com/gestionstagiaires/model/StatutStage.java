package com.gestionstagiaires.model;

public enum StatutStage {
    EN_ATTENTE("En attente"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé"),
    EN_COURS("En cours"),
    TERMINE("Terminé");
    
    private final String libelle;
    
    StatutStage(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}