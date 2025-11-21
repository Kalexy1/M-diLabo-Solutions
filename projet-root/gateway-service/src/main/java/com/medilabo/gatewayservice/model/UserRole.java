package com.medilabo.gatewayservice.model;

/**
 * Enumération représentant les rôles disponibles dans l'application.
 *
 * <p>Chaque rôle peut être converti au format attendu par Spring Security
 * grâce à la méthode {@link #asSpringRole()}.</p>
 */
public enum UserRole {
    ORGANISATEUR,
    PRATICIEN;

    /**
     * Retourne le rôle préfixé par {@code ROLE_}, conformément au format attendu
     * par Spring Security.
     *
     * @return une chaîne de type {@code ROLE_ORGANISATEUR} ou {@code ROLE_PRATICIEN}
     */
    public String asSpringRole() {
        return "ROLE_" + name();
    }
}
