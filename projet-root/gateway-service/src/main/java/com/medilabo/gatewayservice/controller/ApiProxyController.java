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

@Controller
public class ApiProxyController {

    private static final Logger log = LoggerFactory.getLogger(ApiProxyController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Base URL du patient-service vue par le Gateway :
     * - docker : http://patient-service:8080/api
     * - local  : http://localhost:8082/api (par exemple)
     *
     * IMPORTANT :
     * Ton PatientController est annoté avec @RequestMapping("/api/patients"),
     * donc le backend expose /api/patients.
     * On inclut donc déjà "/api" dans la base-url pour que :
     *
     *   /api/patients (gateway) → http://patient-service:8080/api/patients
     */
    @Value("${patients.backend.base-url:http://patient-service:8080/api}")
    private String patientBackendBaseUrl;

    @RequestMapping("/api/**")
    public ResponseEntity<byte[]> proxyApi(HttpServletRequest request) throws IOException {
        // Normalise la base (sans slash final)
        String base = patientBackendBaseUrl.endsWith("/") ?
                patientBackendBaseUrl.substring(0, patientBackendBaseUrl.length() - 1) :
                patientBackendBaseUrl;

        // Exemple :
        // - incomingPath = /api/patients
        // - pathAfterApi = /patients
        String incomingPath = request.getRequestURI();
        String query = request.getQueryString();
        String pathAfterApi = incomingPath.substring("/api".length());
        String normalized = pathAfterApi.isEmpty() ? "/" : pathAfterApi;

        // Avec base = http://patient-service:8080/api
        // et normalized = /patients
        // => target = http://patient-service:8080/api/patients
        String target = base + normalized + (query != null ? "?" + query : "");

        // Méthode HTTP
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException ignored) {
            method = HttpMethod.GET;
        }

        // Copie des en-têtes utiles vers le backend
        HttpHeaders headers = new HttpHeaders();
        copyHeaderIfPresent(request, headers, HttpHeaders.AUTHORIZATION);
        copyHeaderIfPresent(request, headers, HttpHeaders.COOKIE);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT_LANGUAGE);
        copyHeaderIfPresent(request, headers, HttpHeaders.USER_AGENT);
        copyHeaderIfPresent(request, headers, HttpHeaders.CONTENT_TYPE);

        // Corps seulement pour POST / PUT / PATCH
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

    private static void copyHeaderIfPresent(HttpServletRequest req, HttpHeaders dst, String name) {
        Enumeration<String> values = req.getHeaders(name);
        if (values != null) {
            while (values.hasMoreElements()) {
                dst.add(name, values.nextElement());
            }
        }
    }

    private static String safe(String s) {
        if (s == null) return "";
        return s.length() > 500 ? s.substring(0, 500) + "…" : s;
    }
}
