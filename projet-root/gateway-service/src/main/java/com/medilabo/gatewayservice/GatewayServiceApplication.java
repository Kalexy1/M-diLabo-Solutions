package com.medilabo.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal de l'application {@code gateway-service}.
 * <p>
 * Cette application utilise Spring Cloud Gateway pour rediriger les requêtes HTTP
 * vers les microservices appropriés (comme {@code patient-service}, {@code note-service}, etc.).
 * </p>
 *
 * <p>
 * L'annotation {@code @SpringBootApplication} active :
 * <ul>
 *     <li>La configuration automatique de Spring Boot</li>
 *     <li>Le scan des composants dans le package {@code com.medilabo.gatewayservice}</li>
 *     <li>La déclaration de cette classe comme source principale de configuration</li>
 * </ul>
 * </p>
 *
 * @author [Ton Nom]
 * @version 1.0
 */
@SpringBootApplication
public class GatewayServiceApplication {

    /**
     * Méthode principale permettant de lancer l'application Spring Boot.
     *
     * @param args arguments de ligne de commande passés à l'application
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}
