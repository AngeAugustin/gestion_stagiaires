package com.gestionstagiaires.controller;

import com.gestionstagiaires.model.Stagiaire;
import com.gestionstagiaires.model.StatutStage;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.service.StagiaireService;
import com.gestionstagiaires.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final StagiaireService stagiaireService;
    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "pages/home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userService.getUserByEmail(email).orElse(null);
        model.addAttribute("user", currentUser);
        
        // Récupérer les statistiques
        List<Stagiaire> enAttente = stagiaireService.getStagiairesByStatut(StatutStage.EN_ATTENTE);
        List<Stagiaire> acceptes = stagiaireService.getStagiairesByStatut(StatutStage.ACCEPTE);
        List<Stagiaire> enCours = stagiaireService.getStagiairesByStatut(StatutStage.EN_COURS);
        List<Stagiaire> termines = stagiaireService.getStagiairesByStatut(StatutStage.TERMINE);
        
        model.addAttribute("nbEnAttente", enAttente.size());
        model.addAttribute("nbAcceptes", acceptes.size());
        model.addAttribute("nbEnCours", enCours.size());
        model.addAttribute("nbTermines", termines.size());
        
        // Si l'utilisateur est un encadrant, montrer ses stagiaires
        if (currentUser != null) {
            List<Stagiaire> mesStagiaires = stagiaireService.getStagiairesByEncadrant(currentUser);
            model.addAttribute("mesStagiaires", mesStagiaires);
        }
        
        return "pages/dashboard";
    }
}