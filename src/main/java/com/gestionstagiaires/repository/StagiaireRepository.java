package com.gestionstagiaires.repository;

import com.gestionstagiaires.model.Stagiaire;
import com.gestionstagiaires.model.StatutStage;
import com.gestionstagiaires.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StagiaireRepository extends JpaRepository<Stagiaire, Long> {
    List<Stagiaire> findByStatut(StatutStage statut);
    
    List<Stagiaire> findByEncadrant(User encadrant);
    
    Optional<Stagiaire> findByEmail(String email);
    
    @Query("SELECT s FROM Stagiaire s WHERE " +
           "LOWER(s.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.prenom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Stagiaire> searchStagiaires(String keyword);
}