package com.medilabo.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant un utilisateur dans l'application d'authentification.
 * <p>
 * Chaque utilisateur possède un nom d'utilisateur unique, un mot de passe chiffré
 * et un rôle défini (ORGANISATEUR ou PRATICIEN).
 * </p>
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

    /**
     * Retourne l'identifiant unique de l'utilisateur.
     *
     * @return l'identifiant de l'utilisateur
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de l'utilisateur.
     *
     * @param id l'identifiant à définir
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retourne le nom d'utilisateur.
     *
     * @return le nom d'utilisateur
     */
    public String getUsername() {
        return username;
    }

    /**
     * Définit le nom d'utilisateur.
     *
     * @param username le nom d'utilisateur à définir
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retourne le mot de passe chiffré de l'utilisateur.
     *
     * @return le mot de passe chiffré
     */
    public String getPassword() {
        return password;
    }

    /**
     * Définit le mot de passe chiffré de l'utilisateur.
     *
     * @param password le mot de passe à définir
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retourne le rôle attribué à l'utilisateur.
     *
     * @return le rôle de l'utilisateur
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Définit le rôle attribué à l'utilisateur.
     *
     * @param role le rôle à définir
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Retourne le rôle au format attendu par Spring Security,
     * par exemple {@code ROLE_ORGANISATEUR} ou {@code ROLE_PRATICIEN}.
     *
     * @return le rôle au format Spring Security ou {@code null} si aucun rôle n'est défini
     */
    public String getSpringRole() {
        return role != null ? role.asSpringRole() : null;
    }
}
