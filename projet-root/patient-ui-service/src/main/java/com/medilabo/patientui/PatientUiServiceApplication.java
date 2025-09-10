package com.medilabo.patientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

}
