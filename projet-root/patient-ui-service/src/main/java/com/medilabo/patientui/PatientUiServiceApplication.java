package com.medilabo.patientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice <strong>patient-ui-service</strong>.
 *
 * <p>Ce service gère l’interface utilisateur (UI) de l’application Medilabo.
 * Il s'appuie sur Spring Boot et Thymeleaf pour afficher et manipuler les données
 * des patients via des vues rendues côté serveur.</p>
 *
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>démarre un serveur Tomcat embarqué grâce à Spring Boot ;</li>
 *   <li>charge les vues Thymeleaf situées dans le répertoire {@code templates/} ;</li>
 *   <li>sert de passerelle UI entre l'utilisateur et les microservices backend.</li>
 * </ul>
 */
@SpringBootApplication
public class PatientUiServiceApplication {

    /**
     * Méthode principale démarrant l'application Spring Boot.
     *
     * @param args arguments fournis au lancement de l'application
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientUiServiceApplication.class, args);
    }
}
