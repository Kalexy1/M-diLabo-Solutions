package com.medilabo.patientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Application: com.medilabo.patientui
 * <p>
 * Classe <strong>PatientUiServiceApplication</strong>.
 * <br/>
 * Rôle : Point d'entrée principal du microservice <em>patient-ui-service</em>.
 * Ce service gère l'interface utilisateur permettant l'affichage et la
 * gestion des données patients via des vues Thymeleaf.
 * </p>
 * <p>
 * Configuration :
 * <ul>
 *   <li>Expose un bean {@link RestTemplate} utilisé pour communiquer avec
 *   les autres microservices (patient-service, note-service, risk-assessment-service)
 *   via le Gateway.</li>
 *   <li>Démarre un serveur Spring Boot embarqué (Tomcat) pour servir les vues Thymeleaf.</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
public class PatientUiServiceApplication {

    /**
     * Démarre l'application Spring Boot.
     *
     * @param args arguments passés en ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientUiServiceApplication.class, args);
    }

    /**
     * Déclare un bean {@link RestTemplate} pour les appels HTTP REST sortants.
     * <p>
     * Ce client REST est injecté automatiquement dans les services et contrôleurs
     * qui doivent interagir avec d'autres microservices via le Gateway.
     * </p>
     *
     * @return une instance de {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
