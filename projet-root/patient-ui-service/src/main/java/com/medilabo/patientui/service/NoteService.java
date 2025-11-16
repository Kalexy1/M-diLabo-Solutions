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
 * Service responsable des appels à l’API des notes via la Gateway.
 * <p>
 * Il utilise un {@link RestTemplate} dédié (bean {@code noteApiClient})
 * dont l’URL de base est configurée dans {@code application.yml} :
 * <pre>
 * notes.api.url: http://gateway-service:8080/api/notes
 * </pre>
 * Les chemins utilisés ici sont donc relatifs :
 * <ul>
 *   <li>"/patient/{id}"</li>
 *   <li>etc.</li>
 * </ul>
 */
@Service
public class NoteService {

    private final RestTemplate apiClient;

    public NoteService(@Qualifier("noteApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Construit les en-têtes HTTP avec le JWT (Authorization + Cookie).
     *
     * @param request requête HTTP source
     * @return en-têtes configurés
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
     * Méthode générique pour appeler l'API des notes.
     *
     * @param path         chemin relatif (ex: "/patient/{id}")
     * @param method       méthode HTTP
     * @param body         éventuel corps de requête
     * @param request      requête HTTP source (pour extraire le JWT)
     * @param responseType type de la réponse attendue
     * @param <T>          type générique de retour
     * @return corps de la réponse désérialisé
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

    // ============================================================
    //                    MÉTHODES PUBLIQUES
    // ============================================================

    /**
     * Récupère la liste des notes pour un patient.
     *
     * @param patientId identifiant du patient
     * @param request   requête HTTP source
     * @return liste de notes (potentiellement vide)
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
     * @param request   requête HTTP source
     * @return la note créée
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
