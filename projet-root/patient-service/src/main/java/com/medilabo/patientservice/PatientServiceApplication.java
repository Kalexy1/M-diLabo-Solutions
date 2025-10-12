package com.medilabo.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Point d’entrée principal du microservice <strong>patient-service</strong>.
 * <p>
 * Cette classe initialise le contexte Spring Boot et fournit un
 * {@link ObjectMapper} configuré pour gérer correctement la (dé)sérialisation
 * des types de date et d’heure Java 8 (comme {@code LocalDate} ou {@code LocalDateTime})
 * via le module {@link JavaTimeModule}.
 * </p>
 */
@SpringBootApplication
public class PatientServiceApplication {

    /**
     * Démarre l’application Spring Boot du microservice patient-service.
     *
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    /**
     * Fournit un {@link ObjectMapper} configuré avec {@link JavaTimeModule}.
     * <p>
     * Permet de gérer la sérialisation et la désérialisation des types temporels
     * de Java 8 (tels que {@code LocalDate}, {@code LocalDateTime}, etc.).
     * </p>
     *
     * @return un {@link ObjectMapper} prêt à l’emploi pour les types JavaTime
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
