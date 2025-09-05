package com.medilabo.patientui.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application: com.medilabo.patientui.service
 * <p>
 * Classe <strong>NoteService</strong>.
 * <br/>
 * Rôle : Assure la communication entre le microservice <em>patient-ui-service</em>
 * et le microservice <em>note-service</em> via des appels REST.
 * </p>
 * <p>
 * Ce service est utilisé par les contrôleurs pour :
 * <ul>
 *   <li>Récupérer l’historique des notes d’un patient.</li>
 *   <li>Ajouter une nouvelle note à un patient.</li>
 * </ul>
 * Les appels transitent par le <strong>Gateway</strong> pour assurer la centralisation.
 * </p>
 */
@Service
public class NoteService {

    private final RestTemplate restTemplate;

    /**
     * URL de base du microservice note-service exposé via la Gateway.
     */
    private static final String NOTE_SERVICE_URL = "http://gateway-service:8080/notes";

    /**
     * Constructeur avec injection de dépendance.
     *
     * @param restTemplate client HTTP utilisé pour effectuer les appels REST
     */
    public NoteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Récupère toutes les notes associées à un patient depuis le microservice note-service.
     *
     * @param patientId identifiant du patient
     * @return une liste de notes représentées sous forme de {@link Map} (clé/valeur)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getNotesByPatientId(Long patientId) {
        return restTemplate.getForObject(NOTE_SERVICE_URL + "/patient/" + patientId, List.class);
    }

    /**
     * Ajoute une nouvelle note pour un patient en appelant le microservice note-service.
     *
     * @param patientId identifiant du patient
     * @param contenu   contenu textuel de la note à enregistrer
     */
    public void ajouterNote(Long patientId, String contenu) {
        Map<String, Object> nouvelleNote = new HashMap<>();
        nouvelleNote.put("patientId", patientId);
        nouvelleNote.put("contenu", contenu);
        restTemplate.postForObject(NOTE_SERVICE_URL, nouvelleNote, Void.class);
    }
}
