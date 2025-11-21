package com.medilabo.noteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée principal du microservice <strong>NoteService</strong>.
 *
 * <p>Ce microservice gère les notes médicales des patients en s'appuyant sur une
 * base de données MongoDB et expose une API REST pour créer, mettre à jour et
 * consulter ces notes.</p>
 */
@SpringBootApplication
public class NoteServiceApplication {

    /**
     * Démarre l'application Spring Boot pour le microservice NoteService.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
}
