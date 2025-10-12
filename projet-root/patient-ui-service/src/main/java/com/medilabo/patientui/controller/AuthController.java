package com.medilabo.patientui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/")
    public String home() {
        // Laisser le Gateway gérer la redirection si non authentifié
        return "redirect:/ui/patients";
    }

    @GetMapping("/ui/access-denied")
    public String accessDenied() {
        return "access-denied"; // ton template Thymeleaf
    }
}
