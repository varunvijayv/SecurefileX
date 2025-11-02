package com.securefilex.securefilex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import com.securefilex.securefilex.repository.FileRepository;

@Controller
public class DashboardController {

    @Autowired
    private FileRepository fileRepo;

    @GetMapping("/udashboard")
    public String showUserDashboard(Model model, Principal principal) {
        model.addAttribute("files", fileRepo.findByOwner(principal.getName()));
        return "udashboard"; // loads udashboard.html
    }
}
