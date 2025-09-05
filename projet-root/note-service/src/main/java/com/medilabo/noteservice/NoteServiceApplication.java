package com.medilabo.noteservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application: com.medilabo.noteservice
 * <p>
 * Classe <strong>NoteServiceApplication</strong>.
 * <br/>
 * Rôle : Point d'entrée principal du microservice NoteService.
 * </p>
 * <p>
 * Ce microservice gère les notes médicales des patients à l'aide de MongoDB
 * et expose des endpoints REST pour la création et la consultation.
 * </p>
 */
@SpringBootApplication
public class NoteServiceApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(NoteServiceApplication.class, args);
    }
}
