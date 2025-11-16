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

@Service
public class PatientService {

    private final RestTemplate apiClient;

    public PatientService(@Qualifier("patientApiClient") RestTemplate apiClient) {
        this.apiClient = apiClient;
    }

    /** Construit les headers d’authentification */
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

    /** Enveloppe générique pour appeler l’API Patients (path relatif au rootUri du RestTemplate) */
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

    // ============================================================
    //                    MÉTHODES PUBLIQUES
    // ============================================================

    /** GET http://gateway-service:8080/api/patients */
    public List<Patient> findAll(HttpServletRequest request) {
        Patient[] arr = callApi("", HttpMethod.GET, null, request, Patient[].class);
        return (arr == null) ? List.of() : Arrays.asList(arr);
    }

    /** GET http://gateway-service:8080/api/patients/{id} */
    public Patient getOne(Long id, HttpServletRequest request) {
        return callApi("/" + id, HttpMethod.GET, null, request, Patient.class);
    }

    /** POST http://gateway-service:8080/api/patients */
    public Patient create(Patient payload, HttpServletRequest request) {
        return callApi("", HttpMethod.POST, payload, request, Patient.class);
    }

    /** PUT http://gateway-service:8080/api/patients/{id} */
    public Patient update(Long id, Patient payload, HttpServletRequest request) {
        return callApi("/" + id, HttpMethod.PUT, payload, request, Patient.class);
    }

    /** DELETE http://gateway-service:8080/api/patients/{id} */
    public void delete(Long id, HttpServletRequest request) {
        callApi("/" + id, HttpMethod.DELETE, null, request, Void.class);
    }
}
