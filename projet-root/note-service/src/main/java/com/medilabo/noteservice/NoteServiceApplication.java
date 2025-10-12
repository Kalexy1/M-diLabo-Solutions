package com.medilabo.noteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d’entrée principal du microservice <strong>NoteService</strong>.
 * <p>
 * Ce microservice gère les notes médicales des patients à l’aide d’une base de données MongoDB
 * et expose des endpoints REST pour la création, la mise à jour et la consultation des notes.
 * </p>
 */
@SpringBootApplication
public class NoteServiceApplication {

    /**
     * Démarre l’application Spring Boot du microservice NoteService.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
}
