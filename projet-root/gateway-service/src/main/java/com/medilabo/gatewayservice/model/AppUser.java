package com.medilabo.gatewayservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Représente un utilisateur de l'application (organisateur ou praticien).
 * Utilisé pour l'authentification et l'autorisation via Spring Security.
 */
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nom d'utilisateur unique de l'application */
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Column(unique = true, nullable = false)
    private String username;

    /** Mot de passe chiffré de l'utilisateur */
    @NotBlank(message = "Le mot de passe est requis")
    @Column(nullable = false)
    private String password;

    /** Rôle de l'utilisateur (ROLE_ORGANISATEUR ou ROLE_PRATICIEN) */
    @NotBlank(message = "Le rôle est requis")
    @Column(nullable = false)
    private String role;

    /**
     * Renvoie l'identifiant de l'utilisateur.
     * @return l'ID de l'utilisateur
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant de l'utilisateur.
     * @param id l'identifiant
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Renvoie le nom d'utilisateur.
     * @return le nom d'utilisateur
     */
    public String getUsername() {
        return username;
    }

    /**
     * Définit le nom d'utilisateur.
     * @param username le nom d'utilisateur
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Renvoie le mot de passe chiffré.
     * @return le mot de passe
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe (non chiffré à l'entrée, mais doit être encodé avant persistance).
     * @param password le mot de passe brut
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Renvoie le rôle de l'utilisateur.
     * @return le rôle sous forme ROLE_*
     */
    public String getRole() {
        return role;
    }

    /**
     * Définit le rôle de l'utilisateur.
     * Le rôle doit être "ORGANISATEUR" ou "PRATICIEN" (avec ou sans le préfixe "ROLE_").
     * @param role le rôle à définir
     * @throws IllegalArgumentException si le rôle est invalide
     */
    public void setRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Le rôle est requis.");
        }

        String formattedRole = role.toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }

        if (!formattedRole.equals("ROLE_ORGANISATEUR") && !formattedRole.equals("ROLE_PRATICIEN")) {
            throw new IllegalArgumentException("Rôle invalide : " + role + ". Le rôle doit être ORGANISATEUR ou PRATICIEN.");
        }

        this.role = formattedRole;
    }
}