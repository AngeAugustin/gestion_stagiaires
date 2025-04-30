package com.gestionstagiaires.controller;

import com.gestionstagiaires.dto.UserDto;
import com.gestionstagiaires.model.Role;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.repository.RoleRepository;
import com.gestionstagiaires.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoleRepository roleRepository;

    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "pages/admin/user/list";
    }

    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new UserDto());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "pages/admin/user/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserDto userDto,
                         BindingResult result, RedirectAttributes redirectAttributes, Model model) {
                         
        if (result.hasErrors()) {
            model.addAttribute("allRoles", roleRepository.findAll());
            return "pages/admin/user/form";
        }

        // Vérifier si les mots de passe correspondent lors de l'ajout
        if (userDto.getId() == null && !userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas");
            model.addAttribute("allRoles", roleRepository.findAll());
            return "pages/admin/user/form";
        }

        // Vérifier si l'email existe déjà pour un nouvel utilisateur
        if (userDto.getId() == null && userService.existsByEmail(userDto.getEmail())) {
            result.rejectValue("email", "error.user", "Cet email est déjà utilisé");
            model.addAttribute("allRoles", roleRepository.findAll());
            return "pages/admin/user/form";
        }

        User user;
        if (userDto.getId() != null) {
            // Mise à jour
            user = userService.getUserById(userDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
            user.setNom(userDto.getNom());
            user.setPrenom(userDto.getPrenom());
            user.setEmail(userDto.getEmail());
            
            // Ne mettre à jour le mot de passe que s'il est fourni
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                user.setPassword(userDto.getPassword());
            }
        } else {
            // Création
            user = new User();
            user.setNom(userDto.getNom());
            user.setPrenom(userDto.getPrenom());
            user.setEmail(userDto.getEmail());
            user.setPassword(userDto.getPassword());
        }

        // Gérer les rôles
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            String[] roleNames = userDto.getRoles().split(",");
            
            for (String roleName : roleNames) {
                roleRepository.findByName(roleName.trim())
                        .ifPresent(roles::add);
            }
            
            user.setRoles(roles);
        }

        userService.updateUser(user);
        
        redirectAttributes.addFlashAttribute("success", 
            userDto.getId() == null ? "Utilisateur créé avec succès" : "Utilisateur mis à jour avec succès");
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setNom(user.getNom());
        userDto.setPrenom(user.getPrenom());
        userDto.setEmail(user.getEmail());
        
        // Construire la chaîne des rôles
        StringBuilder roleString = new StringBuilder();
        for (Role role : user.getRoles()) {
            if (roleString.length() > 0) {
                roleString.append(",");
            }
            roleString.append(role.getName());
        }
        userDto.setRoles(roleString.toString());
        
        model.addAttribute("user", userDto);
        model.addAttribute("allRoles", roleRepository.findAll());
        
        return "pages/admin/user/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès");
        return "redirect:/admin/users";
    }
}