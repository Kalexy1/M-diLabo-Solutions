package com.medilabo.patientui.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service chargé de la communication avec le microservice note-service.
 * <p>
 * Permet de récupérer les notes d'un patient et d'en ajouter de nouvelles
 * via des appels HTTP REST.
 * </p>
 */
@Service
public class NoteService {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * URL exposée par le gateway pour le microservice note-service.
     */
    private final String NOTE_SERVICE_URL = "http://gateway-service:8080/notes";

    /**
     * Récupère les notes d’un patient à partir du microservice note-service.
     *
     * @param patientId identifiant du patient
     * @return une liste de notes représentées sous forme de {@link Map} (clé/valeur)
     */
    public List<Map<String, Object>> getNotesByPatientId(Long patientId) {
        return restTemplate.getForObject(NOTE_SERVICE_URL + "/patient/" + patientId, List.class);
    }

    /**
     * Envoie une nouvelle note pour un patient au microservice note-service.
     *
     * @param patientId identifiant du patient
     * @param contenu   texte de la note à enregistrer
     */
    public void ajouterNote(Long patientId, String contenu) {
        Map<String, Object> nouvelleNote = new HashMap<>();
        nouvelleNote.put("patientId", patientId);
        nouvelleNote.put("contenu", contenu);
        restTemplate.postForObject(NOTE_SERVICE_URL, nouvelleNote, Void.class);
    }
}