package com.medilabo.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application : <strong>GatewayServiceApplication</strong>.
 * <p>
 * Cette classe constitue le point d’entrée du microservice
 * <em>gateway-service</em>, basé sur Spring Cloud Gateway.
 * </p>
 *
 * <h2>Rôle</h2>
 * <ul>
 *   <li>Démarre le contexte Spring Boot pour le gateway.</li>
 *   <li>Charge automatiquement la configuration des routes définies
 *       dans le fichier {@code application.yml}.</li>
 *   <li>Agit comme proxy inverse centralisé entre le client et les
 *       différents microservices de l’application (auth, patient,
 *       notes, risk, UI).</li>
 * </ul>
 *
 * <h2>Remarque</h2>
 * <p>
 * Les routes ne sont pas définies dans le code Java : elles sont
 * entièrement gérées via la configuration YAML. Cela simplifie la
 * maintenance et évite l’utilisation des URI virtuels de type
 * {@code lb://} lorsqu’aucun service discovery (Eureka/Consul)
 * n’est disponible.
 * </p>
 */
@SpringBootApplication
public class GatewayServiceApplication {

    /**
     * Méthode principale qui lance le microservice Gateway.
     *
     * @param args arguments de la ligne de commande (non utilisés
     *             dans ce projet, mais acceptés par convention)
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
