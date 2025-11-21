package com.medilabo.gatewayservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant un utilisateur de l'application d'authentification.
 *
 * <p>Chaque utilisateur possède :</p>
 * <ul>
 *   <li>un identifiant unique,</li>
 *   <li>un nom d'utilisateur unique,</li>
 *   <li>un mot de passe chiffré,</li>
 *   <li>un rôle parmi ceux définis dans {@link UserRole}.</li>
 * </ul>
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
     * Nom d'utilisateur unique et obligatoire.
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
     * Rôle attribué à l'utilisateur.
     *
     * <p>Valeurs possibles : {@link UserRole#ORGANISATEUR} ou {@link UserRole#PRATICIEN}.</p>
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /**
     * Constructeur vide requis par JPA.
     */
    public AppUser() {}

    /**
     * Constructeur complet permettant d'initialiser toutes les propriétés.
     *
     * @param id        identifiant unique
     * @param username  nom d'utilisateur
     * @param password  mot de passe chiffré
     * @param role      rôle attribué à l'utilisateur
     */
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

    /**
     * Retourne le rôle au format attendu par Spring Security,
     * par exemple {@code ROLE_ORGANISATEUR} ou {@code ROLE_PRATICIEN}.
     *
     * @return le rôle préfixé par "ROLE_", ou {@code null} si aucun rôle n'est défini
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
