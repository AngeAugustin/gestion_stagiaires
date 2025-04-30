package com.gestionstagiaires.controller;

import com.gestionstagiaires.dto.StagiaireDto;
import com.gestionstagiaires.dto.TraitementStageDto;
import com.gestionstagiaires.model.Stagiaire;
import com.gestionstagiaires.model.StatutStage;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.service.StagiaireService;
import com.gestionstagiaires.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/stagiaire")
@RequiredArgsConstructor
@Slf4j
public class StagiaireController {

    private final StagiaireService stagiaireService;
    private final UserService userService;

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String listStagiaires(Model model) {
        List<Stagiaire> stagiaires = stagiaireService.getAllStagiaires();
        model.addAttribute("stagiaires", stagiaires);
        return "pages/stagiaire/list";
    }

    @GetMapping("/new")
    public String showNewStagiaireForm(Model model) {
        // Création d'un nouveau DTO pour le formulaire
        StagiaireDto stagiaireDto = new StagiaireDto();
        model.addAttribute("stagiaire", stagiaireDto);
        
        // Pour les utilisateurs connectés, charger la liste des encadrants potentiels
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        
        // Débogage - vérifier si des utilisateurs sont trouvés
        log.info("Nombre d'utilisateurs trouvés pour le formulaire: {}", users.size());
        for (User user : users) {
            log.info("Utilisateur disponible comme encadrant: {} {} ({})", user.getPrenom(), user.getNom(), user.getEmail());
        }
        
        return "pages/stagiaire/form";
    }

    @PostMapping("/save")
    public String saveStagiaire(@Valid @ModelAttribute("stagiaire") StagiaireDto stagiaireDto,
                              BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            // En cas d'erreur, recharger la liste des utilisateurs
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "pages/stagiaire/form";
        }

        Stagiaire stagiaire = new Stagiaire();
        stagiaire.setId(stagiaireDto.getId());
        stagiaire.setNom(stagiaireDto.getNom());
        stagiaire.setPrenom(stagiaireDto.getPrenom());
        stagiaire.setEmail(stagiaireDto.getEmail());
        stagiaire.setTelephone(stagiaireDto.getTelephone());
        stagiaire.setEcole(stagiaireDto.getEcole());
        stagiaire.setFormation(stagiaireDto.getFormation());
        stagiaire.setNiveau(stagiaireDto.getNiveau());
        stagiaire.setDateDebut(stagiaireDto.getDateDebut());
        stagiaire.setDateFin(stagiaireDto.getDateFin());
        stagiaire.setDescription(stagiaireDto.getDescription());
        
        // Pour un nouveau stagiaire, statut = EN_ATTENTE
        if (stagiaire.getId() == null) {
            stagiaire.setStatut(StatutStage.EN_ATTENTE);
        } else {
            stagiaire.setStatut(stagiaireDto.getStatut());
        }
        
        // Encadrant
        if (stagiaireDto.getEncadrantId() != null) {
            log.info("Tentative d'attribution de l'encadrant avec ID: {}", stagiaireDto.getEncadrantId());
            userService.getUserById(stagiaireDto.getEncadrantId())
                    .ifPresent(user -> {
                        stagiaire.setEncadrant(user);
                        log.info("Encadrant attribué: {} {}", user.getPrenom(), user.getNom());
                    });
        }

        Stagiaire savedStagiaire = stagiaireService.createStagiaire(stagiaire);
        
        // Si c'est un nouveau stagiaire, on le notifie que sa demande a été reçue
        if (stagiaireDto.getId() == null) {
            redirectAttributes.addFlashAttribute("success", 
                "Votre demande de stage a été enregistrée avec succès. Nous vous contacterons bientôt.");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("success", "Stagiaire mis à jour avec succès.");
            return "redirect:/stagiaire/list";
        }
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String showEditForm(@PathVariable Long id, Model model) {
        Stagiaire stagiaire = stagiaireService.getStagiaireById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire non trouvé"));
        
        StagiaireDto stagiaireDto = new StagiaireDto();
        stagiaireDto.setId(stagiaire.getId());
        stagiaireDto.setNom(stagiaire.getNom());
        stagiaireDto.setPrenom(stagiaire.getPrenom());
        stagiaireDto.setEmail(stagiaire.getEmail());
        stagiaireDto.setTelephone(stagiaire.getTelephone());
        stagiaireDto.setEcole(stagiaire.getEcole());
        stagiaireDto.setFormation(stagiaire.getFormation());
        stagiaireDto.setNiveau(stagiaire.getNiveau());
        stagiaireDto.setDateDebut(stagiaire.getDateDebut());
        stagiaireDto.setDateFin(stagiaire.getDateFin());
        stagiaireDto.setDescription(stagiaire.getDescription());
        stagiaireDto.setStatut(stagiaire.getStatut());
        
        if (stagiaire.getEncadrant() != null) {
            stagiaireDto.setEncadrantId(stagiaire.getEncadrant().getId());
        }
        
        model.addAttribute("stagiaire", stagiaireDto);
        
        // Récupérer la liste des utilisateurs pour le choix de l'encadrant
        List<User> users = userService.getAllUsers();
        log.info("Édition: Nombre d'utilisateurs trouvés: {}", users.size());
        model.addAttribute("users", users);
        
        return "pages/stagiaire/form";
    }

    @GetMapping("/view/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String viewStagiaire(@PathVariable Long id, Model model) {
        Stagiaire stagiaire = stagiaireService.getStagiaireById(id)
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire non trouvé"));
        
        model.addAttribute("stagiaire", stagiaire);
        
        // Pour le formulaire de traitement
        TraitementStageDto traitementDto = new TraitementStageDto();
        traitementDto.setStagiaireId(stagiaire.getId());
        traitementDto.setStatut(stagiaire.getStatut());
        if (stagiaire.getEncadrant() != null) {
            traitementDto.setEncadrantId(stagiaire.getEncadrant().getId());
        }
        model.addAttribute("traitementDto", traitementDto);
        
        // Liste des utilisateurs pour le choix de l'encadrant
        List<User> users = userService.getAllUsers();
        log.info("Vue: Nombre d'utilisateurs trouvés: {}", users.size());
        model.addAttribute("users", users);
        
        return "pages/stagiaire/view";
    }

    @PostMapping("/traiter")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String traiterDemande(@ModelAttribute TraitementStageDto traitementDto,
                               RedirectAttributes redirectAttributes) {
        
        log.info("Traitement demande pour stagiaireId: {}", traitementDto.getStagiaireId());
        log.info("Nouveau statut: {}", traitementDto.getStatut());
        log.info("EncadrantId: {}", traitementDto.getEncadrantId());
        
        Stagiaire stagiaire = stagiaireService.traiterDemandeStage(
                traitementDto.getStagiaireId(), 
                traitementDto.getStatut(),
                traitementDto.getMessage()
        );
        
        if (stagiaire != null && traitementDto.getEncadrantId() != null) {
            User encadrant = userService.getUserById(traitementDto.getEncadrantId()).orElse(null);
            if (encadrant != null) {
                log.info("Attribution de l'encadrant: {} {}", encadrant.getPrenom(), encadrant.getNom());
                stagiaireService.assignerEncadrant(stagiaire.getId(), encadrant);
            } else {
                log.warn("Encadrant avec ID {} non trouvé", traitementDto.getEncadrantId());
            }
        }
        
        redirectAttributes.addFlashAttribute("success", "Le stage a été traité avec succès.");
        return "redirect:/stagiaire/list";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteStagiaire(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        stagiaireService.deleteStagiaire(id);
        redirectAttributes.addFlashAttribute("success", "Stagiaire supprimé avec succès.");
        return "redirect:/stagiaire/list";
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public String searchStagiaires(@RequestParam String keyword, Model model) {
        List<Stagiaire> stagiaires = stagiaireService.searchStagiaires(keyword);
        model.addAttribute("stagiaires", stagiaires);
        model.addAttribute("keyword", keyword);
        return "pages/stagiaire/list";
    }
}