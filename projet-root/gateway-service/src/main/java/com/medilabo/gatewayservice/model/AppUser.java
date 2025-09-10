package com.medilabo.gatewayservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Représente un utilisateur de l'application.
 * Utilisé pour l'authentification via Spring Security.
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

}