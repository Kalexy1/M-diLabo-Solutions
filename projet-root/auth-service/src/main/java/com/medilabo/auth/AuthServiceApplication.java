package com.medilabo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice d'authentification.
 * <p>
 * Cette classe démarre l'application Spring Boot pour le service d'authentification.
 * </p>
 */
@SpringBootApplication
public class AuthServiceApplication {

    /**
     * Démarre l'application Spring Boot.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
