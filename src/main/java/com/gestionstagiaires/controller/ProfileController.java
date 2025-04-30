package com.gestionstagiaires.controller;

import com.gestionstagiaires.dto.UserDto;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        User currentUser = userService.getUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));
        
        // Créer un DTO pour le formulaire
        UserDto userDto = new UserDto();
        userDto.setId(currentUser.getId());
        userDto.setNom(currentUser.getNom());
        userDto.setPrenom(currentUser.getPrenom());
        userDto.setEmail(currentUser.getEmail());
        
        model.addAttribute("user", userDto);
        
        return "pages/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute("user") UserDto userDto,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "pages/profile";
        }
        
        // Récupérer l'utilisateur actuel
        User currentUser = userService.getUserById(userDto.getId())
                .orElseThrow(() -> new IllegalStateException("Utilisateur non trouvé"));
        
        // Vérifier si le mot de passe est fourni
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            // Vérifier si les mots de passe correspondent
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas");
                return "pages/profile";
            }
            
            // Mettre à jour le mot de passe
            currentUser.setPassword(userDto.getPassword());
        }
        
        // Mettre à jour les informations de l'utilisateur
        currentUser.setNom(userDto.getNom());
        currentUser.setPrenom(userDto.getPrenom());
        
        // Sauvegarder les modifications
        userService.updateUser(currentUser);
        
        redirectAttributes.addFlashAttribute("success", "Votre profil a été mis à jour avec succès");
        return "redirect:/profile";
    }
}