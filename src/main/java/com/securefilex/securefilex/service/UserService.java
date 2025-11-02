package com.securefilex.securefilex.service;

import com.securefilex.securefilex.model.User;
import com.securefilex.securefilex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(User user) {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("User already exists");
        }

        // Normalize username to lowercase (optional preference)
        user.setUsername(user.getUsername().trim().toLowerCase());

        // Normalize role
        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        } else {
            role = role.toUpperCase();
            if (!role.startsWith("ROLE_")) role = "ROLE_" + role;
        }
        user.setRole(role);

        // BCrypt-hash the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepo.save(user);
    }
}
