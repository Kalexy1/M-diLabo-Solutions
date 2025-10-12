package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

/**
 * Service de gestion des patients côté interface utilisateur.
 * <p>
 * Ce service communique avec le microservice <strong>patient-service</strong>
 * via la Gateway pour exécuter les opérations CRUD sur les patients.
 * Le jeton JWT est transmis dans l’en-tête {@code Authorization} pour
 * assurer l’authentification et la sécurité des requêtes.
 * </p>
 */
@Service
public class PatientService {

    /**
     * Client Web configuré pour communiquer avec le microservice des patients.
     */
    private final WebClient patientApiClient;

    /**
     * Constructeur du service Patient.
     *
     * @param patientApiClient le client Web préconfiguré pour le microservice des patients
     */
    public PatientService(WebClient patientApiClient) {
        this.patientApiClient = patientApiClient;
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @param jwt le jeton JWT pour l’authentification
     * @return une liste de patients (liste vide si aucun patient trouvé)
     */
    public List<Patient> findAll(String jwt) {
        var spec = patientApiClient.get()
                .uri("")
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt));
        Patient[] arr = spec.retrieve().bodyToMono(Patient[].class).block();
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id  l’identifiant du patient
     * @param jwt le jeton JWT pour l’authentification
     * @return le patient correspondant à l’identifiant fourni
     */
    public Patient getOne(Long id, String jwt) {
        return patientApiClient.get()
                .uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    /**
     * Crée un nouveau patient.
     *
     * @param payload les données du patient à créer
     * @param jwt     le jeton JWT pour l’authentification
     * @return le patient créé
     */
    public Patient create(Patient payload, String jwt) {
        return patientApiClient.post()
                .uri("")
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id      l’identifiant du patient à mettre à jour
     * @param payload les nouvelles données du patient
     * @param jwt     le jeton JWT pour l’authentification
     * @return le patient mis à jour
     */
    public Patient update(Long id, Patient payload, String jwt) {
        return patientApiClient.put()
                .uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Patient.class)
                .block();
    }

    /**
     * Supprime un patient à partir de son identifiant.
     *
     * @param id  l’identifiant du patient à supprimer
     * @param jwt le jeton JWT pour l’authentification
     */
    public void delete(Long id, String jwt) {
        patientApiClient.delete()
                .uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearer(jwt))
                .retrieve()
                .toBodilessEntity()
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
