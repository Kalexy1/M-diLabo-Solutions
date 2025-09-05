package com.medilabo.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Application: com.medilabo.auth.model
 * <p>
 * Classe <strong>AppUser</strong>.
 * <br/>
 * Rôle: Représente un utilisateur de l'application pour l'authentification et l'autorisation.
 * </p>
 */
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String role;

    /**
     * @return identifiant unique de l'utilisateur.
     */
    public Long getId() { return id; }

    /**
     * @param id identifiant unique de l'utilisateur.
     */
    public void setId(Long id) { this.id = id; }

    /**
     * @return nom d'utilisateur.
     */
    public String getUsername() { return username; }

    /**
     * @param username nom d'utilisateur.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * @return mot de passe.
     */
    public String getPassword() { return password; }

    /**
     * @param password mot de passe.
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * @return rôle de l'utilisateur.
     */
    public String getRole() { return role; }

    /**
     * Définit le rôle utilisateur. Le rôle doit être soit "ROLE_ORGANISATEUR" soit "ROLE_PRATICIEN".
     * Si le rôle ne commence pas par "ROLE_", il est automatiquement préfixé.
     *
     * @param role rôle utilisateur.
     */
    public void setRole(String role) {
        String formattedRole = formatRole(role);
        if (!formattedRole.equals("ROLE_ORGANISATEUR") && !formattedRole.equals("ROLE_PRATICIEN")) {
            throw new IllegalArgumentException("Rôle invalide");
        }
        this.role = formattedRole;
    }

    /**
     * Ajoute le préfixe ROLE_ si manquant et transforme en majuscules.
     *
     * @param role rôle utilisateur.
     * @return rôle formaté.
     */
    private String formatRole(String role) {
        String upper = role.toUpperCase();
        return upper.startsWith("ROLE_") ? upper : "ROLE_" + upper;
    }
}
