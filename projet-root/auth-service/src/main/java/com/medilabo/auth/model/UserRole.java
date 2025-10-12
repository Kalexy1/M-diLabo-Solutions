package com.medilabo.auth.model;

/**
 * Rôles possibles pour les utilisateurs de l'application.
 */
public enum UserRole {
    ORGANISATEUR,
    PRATICIEN;

    /** Retourne le rôle au format Spring Security (ex: ROLE_ORGANISATEUR). */
    public String asSpringRole() {
        return "ROLE_" + name();
    }
}
