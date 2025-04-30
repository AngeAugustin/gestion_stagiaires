package com.gestionstagiaires.service;

import com.gestionstagiaires.model.Stagiaire;
import com.gestionstagiaires.model.StatutStage;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.repository.StagiaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StagiaireService {
    
    private final StagiaireRepository stagiaireRepository;
    private final EmailService emailService;
    
    @Transactional(readOnly = true)
    public List<Stagiaire> getAllStagiaires() {
        return stagiaireRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Stagiaire> getStagiaireById(Long id) {
        return stagiaireRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Stagiaire> getStagiairesByStatut(StatutStage statut) {
        return stagiaireRepository.findByStatut(statut);
    }
    
    @Transactional(readOnly = true)
    public List<Stagiaire> getStagiairesByEncadrant(User encadrant) {
        return stagiaireRepository.findByEncadrant(encadrant);
    }
    
    @Transactional
    public Stagiaire createStagiaire(Stagiaire stagiaire) {
        return stagiaireRepository.save(stagiaire);
    }
    
    @Transactional
    public Stagiaire updateStagiaire(Stagiaire stagiaire) {
        return stagiaireRepository.save(stagiaire);
    }
    
    @Transactional
    public void deleteStagiaire(Long id) {
        stagiaireRepository.deleteById(id);
    }
    
    @Transactional
    public Stagiaire traiterDemandeStage(Long id, StatutStage statut, String message) {
        Optional<Stagiaire> optStagiaire = stagiaireRepository.findById(id);
        
        if (optStagiaire.isPresent()) {
            Stagiaire stagiaire = optStagiaire.get();
            StatutStage ancienStatut = stagiaire.getStatut();
            stagiaire.setStatut(statut);
            Stagiaire stagiaireUpdated = stagiaireRepository.save(stagiaire);
            
            // Envoyer un e-mail au stagiaire
            String sujet = "Votre demande de stage - " + statut.getLibelle();
            String contenu = "Bonjour " + stagiaire.getPrenom() + ",\n\n"
                          + "Votre demande de stage a été traitée. "
                          + "Statut: " + statut.getLibelle() + "\n\n";
            
            if (message != null && !message.isEmpty()) {
                contenu += "Message: " + message + "\n\n";
            }
            
            contenu += "Cordialement,\nL'équipe de gestion des stages";
            
            emailService.sendEmail(stagiaire.getEmail(), sujet, contenu);
            
            return stagiaireUpdated;
        }
        
        return null;
    }
    
    @Transactional
    public Stagiaire assignerEncadrant(Long stagiaireId, User encadrant) {
        Optional<Stagiaire> optStagiaire = stagiaireRepository.findById(stagiaireId);
        
        if (optStagiaire.isPresent()) {
            Stagiaire stagiaire = optStagiaire.get();
            stagiaire.setEncadrant(encadrant);
            return stagiaireRepository.save(stagiaire);
        }
        
        return null;
    }
    
    @Transactional(readOnly = true)
    public List<Stagiaire> searchStagiaires(String keyword) {
        return stagiaireRepository.searchStagiaires(keyword);
    }
}