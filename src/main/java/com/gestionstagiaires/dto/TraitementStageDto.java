package com.gestionstagiaires.dto;

import com.gestionstagiaires.model.StatutStage;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraitementStageDto {
    
    private Long stagiaireId;
    
    private StatutStage statut;
    
    @Size(max = 1000, message = "Le message ne doit pas dépasser 1000 caractères")
    private String message;
    
    private Long encadrantId;
}