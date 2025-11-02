package com.securefilex.securefilex.controller;

import com.securefilex.securefilex.model.User;
import com.securefilex.securefilex.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user,
                                      Authentication authentication,
                                      Model model) {
        try {
            // If the requester is not an authenticated admin, force ROLE_USER
            boolean isAdmin = authentication != null &&
                    authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                user.setRole("ROLE_USER");
            } else {
                // Ensure admin-submitted role is normalized (prefix + uppercase)
                String r = user.getRole();
                if (r == null || r.isBlank()) user.setRole("ROLE_USER");
                else {
                    r = r.toUpperCase();
                    if (!r.startsWith("ROLE_")) r = "ROLE_" + r;
                    user.setRole(r);
                }
            }

            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
