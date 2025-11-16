package com.medilabo.gatewayservice.model;

/**
 * Enumération représentant les rôles disponibles dans le système.
 */
public enum UserRole {
    ORGANISATEUR,
    PRATICIEN;

    /**
     * Retourne le rôle au format attendu par Spring Security.
     *
     * @return par exemple "ROLE_ORGANISATEUR" ou "ROLE_PRATICIEN"
     */
    public String asSpringRole() {
        return "ROLE_" + name();
    }
}
