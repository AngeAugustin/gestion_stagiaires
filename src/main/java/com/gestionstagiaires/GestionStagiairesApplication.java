package com.gestionstagiaires;

import com.gestionstagiaires.model.Role;
import com.gestionstagiaires.model.Stagiaire;
import com.gestionstagiaires.model.StatutStage;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.repository.RoleRepository;
import com.gestionstagiaires.service.StagiaireService;
import com.gestionstagiaires.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDate;

@SpringBootApplication
@EnableAsync
public class GestionStagiairesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionStagiairesApplication.class, args);
    }
    
    // Initialisation des données par défaut
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, 
                                      UserService userService,
                                      StagiaireService stagiaireService) {
        return args -> {
            // Créer les rôles si non existants
            if (roleRepository.count() == 0) {
                roleRepository.save(new Role("ROLE_ADMIN"));
                roleRepository.save(new Role("ROLE_USER"));
            }
            
            // Créer un admin par défaut si aucun utilisateur n'existe
            if (userService.getAllUsers().isEmpty()) {
                User admin = new User();
                admin.setNom("Admin");
                admin.setPrenom("Super");
                admin.setEmail("admin@gestionstagiaires.com");
                admin.setPassword("admin123");
                userService.createUser(admin);
                
                // Ajouter le rôle ADMIN
                userService.assignRoleToUser("admin@gestionstagiaires.com", "ROLE_ADMIN");
                
                // Créer un utilisateur standard
                User user = new User();
                user.setNom("Utilisateur");
                user.setPrenom("Simple");
                user.setEmail("user@gestionstagiaires.com");
                user.setPassword("user123");
                userService.createUser(user);
                
                System.out.println("Utilisateurs créés avec succès: admin@gestionstagiaires.com et user@gestionstagiaires.com");
            }
            
            // Créer deux stagiaires par défaut pour les tests
            if (stagiaireService.getAllStagiaires().isEmpty()) {
                // Premier stagiaire - En attente
                Stagiaire stagiaire1 = new Stagiaire();
                stagiaire1.setNom("Martin");
                stagiaire1.setPrenom("Sophie");
                stagiaire1.setEmail("sophie.martin@example.com");
                stagiaire1.setTelephone("0612345678");
                stagiaire1.setEcole("École Polytechnique");
                stagiaire1.setFormation("Ingénierie informatique");
                stagiaire1.setNiveau("BAC+5");
                stagiaire1.setDateDebut(LocalDate.now().plusMonths(1));
                stagiaire1.setDateFin(LocalDate.now().plusMonths(4));
                stagiaire1.setDescription("Stage de fin d'études en développement Java/Spring");
                stagiaire1.setStatut(StatutStage.EN_ATTENTE);
                stagiaireService.createStagiaire(stagiaire1);
                
                // Deuxième stagiaire - Accepté
                Stagiaire stagiaire2 = new Stagiaire();
                stagiaire2.setNom("Dubois");
                stagiaire2.setPrenom("Thomas");
                stagiaire2.setEmail("thomas.dubois@example.com");
                stagiaire2.setTelephone("0698765432");
                stagiaire2.setEcole("Université Paris-Saclay");
                stagiaire2.setFormation("Master en Intelligence Artificielle");
                stagiaire2.setNiveau("BAC+4");
                stagiaire2.setDateDebut(LocalDate.now().minusMonths(1));
                stagiaire2.setDateFin(LocalDate.now().plusMonths(2));
                stagiaire2.setDescription("Stage de recherche en apprentissage automatique appliqué au traitement d'images");
                stagiaire2.setStatut(StatutStage.ACCEPTE);
                
                // Récupérer l'utilisateur simple et l'assigner comme encadrant
                userService.getUserByEmail("user@gestionstagiaires.com").ifPresent(stagiaire2::setEncadrant);
                
                stagiaireService.createStagiaire(stagiaire2);
                
                System.out.println("Stagiaires créés avec succès: Sophie Martin et Thomas Dubois");
            }
        };
    }
}