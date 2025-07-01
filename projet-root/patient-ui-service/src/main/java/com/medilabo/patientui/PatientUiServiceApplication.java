package com.medilabo.patientui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Patient UI Service.
 * <p>
 * Cette classe lance l'application Spring Boot côté interface utilisateur,
 * permettant l'affichage et la gestion des données patients via une interface web.
 * </p>
 */
@SpringBootApplication
public class PatientUiServiceApplication {

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args les arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientUiServiceApplication.class, args);
    }

}
