package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
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
 * Service chargé de communiquer avec l’API du microservice
 * <strong>patient-service</strong> via la Gateway.
 *
 * <p>
 * Ce service utilise un {@link RestTemplate} préconfiguré (bean
 * <code>patientApiClient</code>) dont l’URL de base correspond à l’API
 * Patient exposée via <code>/api/patients</code>.  
 * Les appels REST sont sécurisés grâce au JWT extrait du cookie utilisateur.
 * </p>
 */
@Service
public class PatientService {

    /** Client REST dédié aux appels vers le patient-service via la Gateway. */
    private final RestTemplate apiClient;

    public PatientService(@Qualifier("patientApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Construit les en-têtes HTTP contenant les informations
     * d’authentification (JWT en Bearer + Cookie).
     *
     * @param request requête contenant le cookie JWT
     * @return les en-têtes HTTP configurés
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
     * Méthode générique envoyant un appel REST vers l’API Patients.
     *
     * @param path  chemin relatif (ex. <code>"/5"</code>)
     * @param method méthode HTTP
     * @param body corps éventuel de la requête
     * @param request requête HTTP source contenant le JWT
     * @param type type attendu en réponse
     * @param <T> type générique retourné
     * @return corps de réponse désérialisé
     * @throws IllegalStateException en cas d’erreur HTTP renvoyée par le backend
     */
    private <T> T callApi(String path,
                          HttpMethod method,
                          Object body,
                          HttpServletRequest request,
                          Class<T> type) {

        HttpHeaders headers = buildAuthHeaders(request);

        HttpEntity<?> entity = (body != null)
                ? new HttpEntity<>(body, headers)
                : new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response =
                    apiClient.exchange(path, method, entity, type);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new IllegalStateException(
                    "Patients API error " + e.getStatusCode() + " : " + e.getResponseBodyAsString(), e);
        }
    }

    /**
     * Récupère l’ensemble des patients.
     *
     * @param request requête contenant le JWT
     * @return liste de tous les patients
     */
    public List<Patient> findAll(HttpServletRequest request) {
        Patient[] arr = callApi("", HttpMethod.GET, null, request, Patient[].class);
        return (arr == null) ? List.of() : Arrays.asList(arr);
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @param request requête contenant le JWT
     * @return patient correspondant
     */
    public Patient getOne(Long id, HttpServletRequest request) {
        return callApi("/" + id, HttpMethod.GET, null, request, Patient.class);
    }

    /**
     * Crée un nouveau patient.
     *
     * @param payload données du patient à créer
     * @param request requête contenant le JWT
     * @return patient créé
     */
    public Patient create(Patient payload, HttpServletRequest request) {
        return callApi("", HttpMethod.POST, payload, request, Patient.class);
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id identifiant du patient à modifier
     * @param payload nouvelles données du patient
     * @param request requête contenant le JWT
     * @return patient mis à jour
     */
    public Patient update(Long id, Patient payload, HttpServletRequest request) {
        return callApi("/" + id, HttpMethod.PUT, payload, request, Patient.class);
    }

    /**
     * Supprime un patient.
     *
     * @param id identifiant du patient à supprimer
     * @param request requête contenant le JWT
     */
    public void delete(Long id, HttpServletRequest request) {
        callApi("/" + id, HttpMethod.DELETE, null, request, Void.class);
    }
}
