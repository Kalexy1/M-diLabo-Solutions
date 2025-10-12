package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Note;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

/**
 * Service de gestion des notes médicales côté interface utilisateur.
 * <p>
 * Ce service communique avec le microservice <strong>note-service</strong> via la Gateway
 * pour récupérer et créer des notes associées à un patient.
 * Le jeton JWT est transmis dans l’en-tête {@code Authorization} pour l’authentification.
 * </p>
 */
@Service
public class NoteService {

    /**
     * Client Web configuré pour communiquer avec le microservice des notes.
     */
    private final WebClient noteApiClient;

    /**
     * Constructeur du service des notes.
     *
     * @param noteApiClient le client Web préconfiguré pour le microservice des notes
     */
    public NoteService(WebClient noteApiClient) {
        this.noteApiClient = noteApiClient;
    }

    /**
     * Récupère toutes les notes associées à un patient spécifique.
     *
     * @param patientId l’identifiant du patient
     * @param jwt       le jeton JWT pour l’authentification
     * @return la liste des notes du patient (liste vide si aucune note trouvée)
     */
    public List<Note> findByPatient(Long patientId, String jwt) {
        Note[] arr = noteApiClient.get()
                .uri("/patient/{pid}", patientId)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve()
                .bodyToMono(Note[].class)
                .block();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    /**
     * Crée une nouvelle note pour un patient.
     *
     * @param patientId l’identifiant du patient
     * @param payload   la note à créer
     * @param jwt       le jeton JWT pour l’authentification
     * @return la note créée
     */
    public Note createForPatient(Long patientId, Note payload, String jwt) {
        return noteApiClient.post()
                .uri("/patient/{pid}", patientId)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Note.class)
                .block();
    }

    /**
     * Génère l’en-tête {@code Authorization} au format Bearer.
     *
     * @param jwt le jeton JWT à inclure
     * @return la valeur complète de l’en-tête Authorization
     */
    private static String bearer(String jwt) {
        return "Bearer " + (jwt == null ? "" : jwt);
    }
}
