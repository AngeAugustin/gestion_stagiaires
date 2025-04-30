package com.gestionstagiaires.controller;

import com.gestionstagiaires.dto.UserDto;
import com.gestionstagiaires.model.Role;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.repository.RoleRepository;
import com.gestionstagiaires.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping("/login")
    public String login() {
        return "pages/auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "pages/auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result, Model model) {
        // Vérifier les erreurs de validation
        if (result.hasErrors()) {
            return "pages/auth/register";
        }

        // Vérifier si les mots de passe correspondent
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas");
            return "pages/auth/register";
        }

        // Vérifier si l'email existe déjà
        if (userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
            return "pages/auth/register";
        }

        // Créer l'utilisateur
        User user = new User();
        user.setPrenom(userDto.getPrenom());
        user.setNom(userDto.getNom());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        
        // Ajouter le rôle USER par défaut
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.addRole(userRole);

        userService.createUser(user);

        return "redirect:/login?registered";
    }
}