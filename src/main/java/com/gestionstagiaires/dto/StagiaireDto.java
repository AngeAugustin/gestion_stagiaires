package com.gestionstagiaires.dto;

import com.gestionstagiaires.model.StatutStage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StagiaireDto {
    
    private Long id;
    
    @NotEmpty(message = "Le prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;
    
    @NotEmpty(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;
    
    @NotEmpty(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
    
    @Pattern(regexp = "^(\\+[0-9]{1,3})?[0-9]{9,15}$", message = "Format de téléphone invalide")
    private String telephone;
    
    private String ecole;
    
    private String formation;
    
    private String niveau;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;
    
    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;
    
    private StatutStage statut;
    
    private Long encadrantId;
}