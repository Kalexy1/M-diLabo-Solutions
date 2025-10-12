package com.medilabo.patientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d’entrée principal du microservice <strong>patient-ui-service</strong>.
 * <p>
 * Ce service gère l’interface utilisateur (UI) de l’application Medilabo.
 * Il permet l’affichage et la gestion des données des patients à travers
 * des vues Thymeleaf rendues par un serveur Spring Boot embarqué.
 * </p>
 * <p>
 * <strong>Configuration :</strong>
 * <ul>
 *   <li>Démarre un serveur Tomcat intégré via Spring Boot.</li>
 *   <li>Charge et rend les vues Thymeleaf situées dans le répertoire {@code templates/}.</li>
 * </ul>
 * </p>
 */
@SpringBootApplication
public class PatientUiServiceApplication {

    /**
     * Méthode principale de démarrage de l’application Spring Boot.
     *
     * @param args les arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientUiServiceApplication.class, args);
    }
}
