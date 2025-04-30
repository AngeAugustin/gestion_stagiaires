package com.gestionstagiaires.service;

import com.gestionstagiaires.model.Role;
import com.gestionstagiaires.model.User;
import com.gestionstagiaires.repository.RoleRepository;
import com.gestionstagiaires.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Transactional
    public User createUser(User user) {
        // Encoder le mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Ajouter un rôle par défaut si aucun n'est défini
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            user.addRole(userRole);
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public User updateUser(User user) {
        // Si le mot de passe est vide, on garde l'ancien
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            Optional<User> existingUser = userRepository.findById(user.getId());
            existingUser.ifPresent(u -> user.setPassword(u.getPassword()));
        } else {
            // Sinon on encode le nouveau mot de passe
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        
        return userRepository.save(user);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Transactional
    public void assignRoleToUser(String email, String roleName) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        
        if (userOpt.isPresent() && roleOpt.isPresent()) {
            User user = userOpt.get();
            Role role = roleOpt.get();
            user.addRole(role);
            userRepository.save(user);
        }
    }
}