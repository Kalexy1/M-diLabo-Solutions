package com.medilabo.auth.model;

/**
 * Enumération représentant les rôles possibles pour les utilisateurs de l'application.
 * <p>
 * Les rôles définissent les autorisations et les accès disponibles
 * dans le système d'authentification.
 * </p>
 */
public enum UserRole {

    /**
     * Rôle attribué aux organisateurs, responsables de la gestion des informations.
     */
    ORGANISATEUR,

    /**
     * Rôle attribué aux praticiens, responsables du suivi médical des patients.
     */
    PRATICIEN;

    /**
     * Retourne le rôle au format attendu par Spring Security.
     * <p>
     * Par exemple, {@code ORGANISATEUR} devient {@code ROLE_ORGANISATEUR}.
     * </p>
     *
     * @return le rôle au format Spring Security (préfixé par {@code ROLE_})
     */
    public String asSpringRole() {
        return "ROLE_" + name();
    }
}
