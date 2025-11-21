package com.medilabo.gatewayservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Contrôleur responsable du proxy API du Gateway.
 *
 * <p>Cette classe intercepte les requêtes entrantes sous {@code /api/**}
 * et les redirige vers le microservice backend approprié (patients, notes,
 * risk). Les en-têtes utiles, le corps de la requête et la méthode HTTP
 * sont conservés afin d'assurer un comportement transparent.</p>
 *
 * <p>En cas d'erreur côté backend (4xx/5xx), le Gateway renvoie le code
 * d'erreur et le corps retourné par le microservice cible.</p>
 */
@Controller
public class ApiProxyController {

    private static final Logger log = LoggerFactory.getLogger(ApiProxyController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${patients.backend.base-url:http://patient-service:8081/api}")
    private String patientBackendBaseUrl;

    @Value("${notes.backend.base-url:http://note-service:8082/api}")
    private String notesBackendBaseUrl;

    @Value("${risk.backend.base-url:http://risk-assessment-service:8083/api}")
    private String riskBackendBaseUrl;

    /**
     * Proxy général pour les endpoints exposés sous {@code /api/**}.
     *
     * <p>Cette méthode :</p>
     * <ul>
     *   <li>détermine le microservice cible en fonction du chemin de la requête,</li>
     *   <li>reconstruit l'URL finale vers le backend,</li>
     *   <li>copie les en-têtes pertinents,</li>
     *   <li>transmet le corps et la méthode HTTP,</li>
     *   <li>retourne intégralement la réponse du backend (corps, headers, status).</li>
     * </ul>
     *
     * @param request la requête HTTP entrante
     * @return la réponse brute transmise au backend correspondant
     * @throws IOException si la lecture du corps de requête échoue
     */
    @RequestMapping("/api/**")
    public ResponseEntity<byte[]> proxyApi(HttpServletRequest request) throws IOException {

        String incomingPath = request.getRequestURI();
        String query = request.getQueryString();

        String pathAfterApi = incomingPath.substring("/api".length());
        if (pathAfterApi.isEmpty()) {
            pathAfterApi = "/";
        }

        String base;
        if (pathAfterApi.startsWith("/patients")) {
            base = normalizeBase(patientBackendBaseUrl);
        } else if (pathAfterApi.startsWith("/notes")) {
            base = normalizeBase(notesBackendBaseUrl);
        } else if (pathAfterApi.startsWith("/risk")) {
            base = normalizeBase(riskBackendBaseUrl);
        } else {
            log.warn("[ApiProxy] Aucun backend pour path={}", incomingPath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(("No backend found for path " + incomingPath).getBytes());
        }

        String target = base + pathAfterApi + (query != null ? "?" + query : "");

        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException ignored) {
            method = HttpMethod.GET;
        }

        HttpHeaders headers = new HttpHeaders();
        copyHeaderIfPresent(request, headers, HttpHeaders.AUTHORIZATION);
        copyHeaderIfPresent(request, headers, HttpHeaders.COOKIE);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT_LANGUAGE);
        copyHeaderIfPresent(request, headers, HttpHeaders.USER_AGENT);
        copyHeaderIfPresent(request, headers, HttpHeaders.CONTENT_TYPE);

        byte[] body = null;
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            body = request.getInputStream().readAllBytes();
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            log.debug("[ApiProxy] {} -> {}", method, target);
            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    URI.create(target),
                    method,
                    entity,
                    byte[].class
            );

            HttpHeaders out = new HttpHeaders();
            out.putAll(resp.getHeaders());
            out.setAccessControlExposeHeaders(Collections.singletonList("Location"));

            return new ResponseEntity<>(resp.getBody(), out, resp.getStatusCode());

        } catch (HttpStatusCodeException e) {
            log.warn("[ApiProxy] target={} -> {} {}", target, e.getStatusCode(), safe(e.getResponseBodyAsString()));
            HttpHeaders out = e.getResponseHeaders() != null ? e.getResponseHeaders() : new HttpHeaders();
            return ResponseEntity.status(e.getStatusCode())
                    .headers(out)
                    .body(e.getResponseBodyAsByteArray());

        } catch (ResourceAccessException e) {
            log.error("[ApiProxy] target={} connection error: {}", target, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Bad Gateway: cannot reach " + target).getBytes());

        } catch (Exception e) {
            log.error("[ApiProxy] target={} unexpected error: {}", target, e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Bad Gateway to " + target).getBytes());
        }
    }

    /**
     * Supprime la barre oblique finale d'une URL si elle est présente.
     *
     * @param base l'URL à normaliser
     * @return l'URL sans barre oblique finale
     */
    private static String normalizeBase(String base) {
        if (base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    /**
     * Copie un en-tête HTTP s'il est présent dans la requête entrante.
     *
     * @param req la requête d'origine
     * @param dst l'objet {@link HttpHeaders} cible
     * @param name le nom de l'en-tête à copier
     */
    private static void copyHeaderIfPresent(HttpServletRequest req, HttpHeaders dst, String name) {
        Enumeration<String> values = req.getHeaders(name);
        if (values != null) {
            while (values.hasMoreElements()) {
                dst.add(name, values.nextElement());
            }
        }
    }

    /**
     * Tronque une chaîne longue pour les logs.
     *
     * @param s la chaîne originale
     * @return la chaîne tronquée si nécessaire
     */
    private static String safe(String s) {
        if (s == null) return "";
        return s.length() > 500 ? s.substring(0, 500) + "…" : s;
    }
}
