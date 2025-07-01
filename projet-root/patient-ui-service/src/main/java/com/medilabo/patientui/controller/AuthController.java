package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.AppUser;
import com.medilabo.patientui.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur chargé de gérer l'authentification des utilisateurs.
 * Il permet l'affichage de la page de connexion, du formulaire d'inscription,
 * et le traitement de la création de compte utilisateur.
 */
@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Affiche la page de connexion de l'utilisateur.
     *
     * @return le nom de la vue Thymeleaf correspondant à la page de connexion ("login.html")
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Affiche la page d'inscription pour permettre à un utilisateur de créer un compte.
     *
     * @param model le modèle Thymeleaf contenant une instance vide d'AppUser
     * @return le nom de la vue Thymeleaf correspondant à la page d'inscription ("register.html")
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    /**
     * Traite les informations soumises via le formulaire d'inscription.
     * Vérifie l'unicité du nom d'utilisateur, valide les données,
     * encode le mot de passe et enregistre l'utilisateur si toutes les conditions sont remplies.
     *
     * @param user l'objet AppUser renseigné dans le formulaire
     * @param result le résultat de la validation des champs du formulaire
     * @param model le modèle Thymeleaf utilisé pour renvoyer les erreurs éventuelles à la vue
     * @return une redirection vers la page de connexion si l'inscription réussit, sinon la page d'inscription avec erreurs
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute @Valid AppUser user,
                                  BindingResult result,
                                  Model model) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "Ce nom d'utilisateur existe déjà.");
            return "register";
        }

        try {
            user.setRole(user.getRole());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }

        if (result.hasErrors()) {
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }
}
