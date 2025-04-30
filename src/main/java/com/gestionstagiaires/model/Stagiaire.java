package com.gestionstagiaires.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "stagiaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stagiaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String prenom;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String telephone;
    
    @Column
    private String ecole;
    
    @Column
    private String formation;
    
    @Column
    private String niveau;
    
    @Column
    private LocalDate dateDebut;
    
    @Column
    private LocalDate dateFin;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private StatutStage statut = StatutStage.EN_ATTENTE;
    
    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private User encadrant;
}