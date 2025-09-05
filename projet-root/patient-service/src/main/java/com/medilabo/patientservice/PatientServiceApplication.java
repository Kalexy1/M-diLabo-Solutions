package com.medilabo.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Application: com.medilabo.patientservice
 * <p>
 * Classe <strong>PatientServiceApplication</strong>.
 * <br/>
 * Rôle : Point d'entrée du microservice <em>patient-service</em>.
 * Initialise Spring Boot et expose un {@link ObjectMapper} configuré pour les
 * types de date/heure Java 8 (par ex. {@code LocalDate}) via {@link JavaTimeModule}.
 * </p>
 */
@SpringBootApplication
public class PatientServiceApplication {

    /**
     * Démarre l'application Spring Boot.
     *
     * @param args arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
        }

    /**
     * Fournit un bean {@link ObjectMapper} enrichi du {@link JavaTimeModule}
     * afin de gérer correctement la (dé)sérialisation des types Java 8
     * (comme {@code LocalDate}, {@code LocalDateTime}, etc.).
     *
     * @return un {@link ObjectMapper} prêt pour les types JavaTime.
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
