package com.medilabo.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Point d'entrée principal du microservice <strong>patient-service</strong>.
 *
 * <p>Initialise l'application Spring Boot et fournit un {@link ObjectMapper}
 * configuré pour gérer correctement la sérialisation et la désérialisation
 * des types Java Time (ex. {@code LocalDate}, {@code LocalDateTime}) grâce au
 * module {@link JavaTimeModule}.</p>
 */
@SpringBootApplication
public class PatientServiceApplication {

    /**
     * Démarre l'application Spring Boot du microservice patient-service.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    /**
     * Crée un {@link ObjectMapper} configuré avec {@link JavaTimeModule}.
     *
     * <p>Permet de prendre en charge les types temporels de Java 8 lors de la
     * sérialisation et de la désérialisation JSON.</p>
     *
     * @return un {@link ObjectMapper} prêt à l'emploi pour les types Java Time
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
