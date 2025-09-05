package com.medilabo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application: com.medilabo.auth
 * <p>
 * Classe <strong>AuthServiceApplication</strong>.
 * <br/>
 * Rôle: Point d'entrée principal du microservice d'authentification.
 * </p>
 */
@SpringBootApplication
public class AuthServiceApplication {

    /**
     * main: Démarre l'application Spring Boot.
     *
     * @param args paramètres de ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
