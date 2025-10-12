package com.medilabo.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant un utilisateur dans l'application.
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // --- Getters / Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    /** Retourne le rôle au format Spring Security (ROLE_ORGANISATEUR ou ROLE_PRATICIEN). */
    public String getSpringRole() {
        return role != null ? role.asSpringRole() : null;
    }
}
