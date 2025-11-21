package com.medilabo.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée du microservice <strong>gateway-service</strong>.
 *
 * <p>Cette application démarre le contexte Spring Boot et charge la configuration
 * Spring Cloud Gateway définie dans le fichier {@code application.yml}. Le
 * gateway agit comme un proxy centralisé entre le client et les différents
 * microservices de l'écosystème (auth, patient, notes, risk, UI).</p>
 */
@SpringBootApplication
public class GatewayServiceApplication {

    /**
     * Lance l'application Gateway.
     *
     * @param args arguments de ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
