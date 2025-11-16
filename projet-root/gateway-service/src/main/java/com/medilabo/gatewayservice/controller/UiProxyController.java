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
public class UiProxyController {

    private static final Logger log = LoggerFactory.getLogger(UiProxyController.class);

    private final RestTemplate restTemplate = new RestTemplate();

    /** Base URL de l’UI vue par le Gateway (local: http://localhost:8084, docker: http://patient-ui-service:8080) */
    @Value("${ui.base-url:http://patient-ui-service:8080}")
    private String uiBaseUrl;

    @RequestMapping("/ui/**")
    public ResponseEntity<byte[]> proxyUi(HttpServletRequest request) throws IOException {
        // Normalise la base (sans slash final)
        String base = uiBaseUrl.endsWith("/") ? uiBaseUrl.substring(0, uiBaseUrl.length() - 1) : uiBaseUrl;

        // Construit le chemin cible
        String incomingPath = request.getRequestURI();                // ex: /ui/patients
        String query        = request.getQueryString();               // ex: page=1
        String pathAfterUi  = incomingPath.substring("/ui".length()); // ex: /patients ou "" si /ui
        String normalized   = pathAfterUi.isEmpty() ? "/" : pathAfterUi;

        String target = base + normalized + (query != null ? "?" + query : "");

        // Méthode HTTP (fallback GET)
        HttpMethod method;
        try {
            method = HttpMethod.valueOf(request.getMethod());
        } catch (IllegalArgumentException ignored) {
            method = HttpMethod.GET;
        }

        // Copie des en-têtes utiles vers l’UI
        HttpHeaders headers = new HttpHeaders();
        copyHeaderIfPresent(request, headers, HttpHeaders.AUTHORIZATION);
        copyHeaderIfPresent(request, headers, HttpHeaders.COOKIE);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT);
        copyHeaderIfPresent(request, headers, HttpHeaders.ACCEPT_LANGUAGE);
        copyHeaderIfPresent(request, headers, HttpHeaders.USER_AGENT);
        copyHeaderIfPresent(request, headers, HttpHeaders.CONTENT_TYPE);

        // Corps uniquement pour méthodes avec body
        byte[] body = null;
        if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH) {
            body = request.getInputStream().readAllBytes();
        }

        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        try {
            log.debug("[UiProxy] {} -> {}", method, target);
            ResponseEntity<byte[]> resp = restTemplate.exchange(URI.create(target), method, entity, byte[].class);

            // Renvoie tel quel (status + headers + body) + expose Location au navigateur
            HttpHeaders out = new HttpHeaders();
            out.putAll(resp.getHeaders());
            out.setAccessControlExposeHeaders(Collections.singletonList("Location"));

            return new ResponseEntity<>(resp.getBody(), out, resp.getStatusCode());
        } catch (HttpStatusCodeException e) {
            // L’UI a répondu avec un statut d’erreur : on propage tel quel (très utile pour diagnostiquer)
            log.warn("[UiProxy] target={} -> {} {}", target, e.getStatusCode(), safe(e.getResponseBodyAsString()));
            HttpHeaders out = e.getResponseHeaders() != null ? e.getResponseHeaders() : new HttpHeaders();
            return ResponseEntity.status(e.getStatusCode())
                    .headers(out)
                    .body(e.getResponseBodyAsByteArray());
        } catch (ResourceAccessException e) {
            // Connexion impossible (DNS, port, timeout…) -> 502
            log.error("[UiProxy] target={} connection error: {}", target, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Bad Gateway: cannot reach " + target).getBytes());
        } catch (Exception e) {
            // Autre erreur inattendue -> 502 avec trace réduite
            log.error("[UiProxy] target={} unexpected error: {}", target, e.toString());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(("Bad Gateway to " + target).getBytes());
        }
    }

    /** Copie un en-tête HTTP du HttpServletRequest vers les HttpHeaders envoyés au backend. */
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
