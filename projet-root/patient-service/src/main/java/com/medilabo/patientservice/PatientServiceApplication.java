package com.medilabo.patientservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Classe principale du microservice Patient Service.
 * <p>
 * Cette classe lance l'application Spring Boot.
 * Elle configure également un {@link ObjectMapper} capable de gérer les types Java 8 comme {@code LocalDate}.
 * </p>
 */
@SpringBootApplication
public class PatientServiceApplication {

    /**
     * Méthode principale qui démarre l'application Spring Boot.
     *
     * @param args les arguments passés en ligne de commande
     */
    public static void main(String[] args) {
        SpringApplication.run(PatientServiceApplication.class, args);
    }

    /**
     * Fournit un bean {@link ObjectMapper} configuré avec le module {@link JavaTimeModule}
     * pour la sérialisation/désérialisation des dates Java 8 (comme {@code LocalDate}).
     *
     * @return un {@link ObjectMapper} personnalisé
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}
