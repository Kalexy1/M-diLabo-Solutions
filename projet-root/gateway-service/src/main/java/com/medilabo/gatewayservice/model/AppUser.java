package com.medilabo.gatewayservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant un utilisateur dans l'application d'authentification.
 * Chaque utilisateur possède un nom d'utilisateur unique, un mot de passe chiffré,
 * et un rôle défini (ORGANISATEUR ou PRATICIEN).
 */
@Entity
@Table(name = "users")
public class AppUser {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom d'utilisateur unique, obligatoire.
     */
    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Mot de passe chiffré de l'utilisateur.
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /**
     * Rôle attribué à l'utilisateur (ORGANISATEUR ou PRATICIEN).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /** Constructeur vide requis par JPA */
    public AppUser() {}

    /** Constructeur complet */
    public AppUser(Long id, String username, String password, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ----- GETTERS -----

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    // ----- SETTERS -----

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // ----- Méthodes utilitaires -----

    /**
     * Retourne le rôle au format attendu par Spring Security,
     * par exemple {@code ROLE_ORGANISATEUR} ou {@code ROLE_PRATICIEN}.
     *
     * @return le rôle au format Spring Security ou {@code null} si aucun rôle n'est défini
     */
    public String getSpringRole() {
        return role != null ? role.asSpringRole() : null;
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
