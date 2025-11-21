package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Note;
import com.medilabo.patientui.web.JwtCookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Service dédié à la communication avec l’API du microservice
 * <strong>note-service</strong> via la Gateway.
 *
 * <p>
 * Il utilise un {@link RestTemplate} configuré avec l’URL de base définie dans
 * <code>application.yml</code> (bean <code>noteApiClient</code>).  
 * Les appels sont réalisés via des chemins relatifs (ex. <code>/patient/{id}</code>),
 * et incluent automatiquement le JWT extrait du cookie utilisateur.
 * </p>
 */
@Service
public class NoteService {

    /** Client REST configuré pour appeler l’API des notes via la Gateway. */
    private final RestTemplate apiClient;

    public NoteService(@Qualifier("noteApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Construit les en-têtes HTTP nécessaires à l’appel, incluant
     * l’Authorization Bearer et le cookie JWT quand ils sont présents.
     *
     * @param request requête contenant éventuellement le cookie JWT
     * @return les en-têtes configurés
     */
    private HttpHeaders buildAuthHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        String jwt = JwtCookieUtil.extractJwt(request);
        if (jwt != null && !jwt.isBlank()) {
            headers.setBearerAuth(jwt);
            headers.add(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=" + jwt);
        }

        return headers;
    }

    /**
     * Envoie un appel REST générique vers l’API des notes.
     *
     * @param path         chemin relatif (ex. <code>/patient/5</code>)
     * @param method       méthode HTTP à utiliser
     * @param body         corps de requête éventuel
     * @param request      requête HTTP contenant le JWT
     * @param responseType type de la réponse attendue
     * @param <T>          type retourné par l’appel
     * @return la réponse désérialisée
     * @throws IllegalStateException en cas d’erreur HTTP du backend
     */
    private <T> T callApi(String path,
                          HttpMethod method,
                          Object body,
                          HttpServletRequest request,
                          Class<T> responseType) {

        HttpHeaders headers = buildAuthHeaders(request);

        HttpEntity<?> entity = (body != null)
                ? new HttpEntity<>(body, headers)
                : new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response =
                    apiClient.exchange(path, method, entity, responseType);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException(
                    "Notes API error " + e.getStatusCode() + " : " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Récupère toutes les notes d’un patient.
     *
     * @param patientId identifiant du patient
     * @param request   requête contenant le JWT
     * @return liste des notes du patient (éventuellement vide)
     */
    public List<Note> findByPatient(Long patientId, HttpServletRequest request) {
        Note[] arr = callApi(
                "/patient/" + patientId,
                HttpMethod.GET,
                null,
                request,
                Note[].class
        );
        return (arr == null) ? List.of() : Arrays.asList(arr);
    }

    /**
     * Crée une nouvelle note pour un patient donné.
     *
     * @param patientId identifiant du patient
     * @param payload   contenu de la note
     * @param request   requête contenant le JWT
     * @return note nouvellement créée
     */
    public Note createForPatient(Long patientId, Note payload, HttpServletRequest request) {
        return callApi(
                "/patient/" + patientId,
                HttpMethod.POST,
                payload,
                request,
                Note.class
        );
    }
}
