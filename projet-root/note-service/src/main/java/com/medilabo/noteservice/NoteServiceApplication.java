package com.medilabo.noteservice;

import com.medilabo.noteservice.model.Note;
import com.medilabo.noteservice.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Point d'entrée principal du microservice NoteService.
 * <p>
 * Ce microservice gère les notes médicales des patients à l'aide de MongoDB.
 * Il expose des endpoints REST pour la création, la lecture, la mise à jour
 * et la suppression des notes.
 * </p>
 *
 * <p>
 * Cette classe initialise le contexte Spring Boot et démarre l'application.
 * </p>
 *
 * @author [Ton Nom]
 * @version 1.0
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
