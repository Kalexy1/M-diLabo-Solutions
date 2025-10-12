package com.medilabo.gatewayservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Slice test WebFlux :
 * - fournit WebHandler/WebFlux infra
 * - on importe explicitement la SecurityConfig (gateway)
 * - on déclare des routes fonctionnelles factices
 */
@WebFluxTest // ✅ Contexte WebFlux minimal (pas de servlet MVC, pas d'autoconfig du gateway)
@TestPropertySource(properties = {
    "JWT_SECRET=0123456789abcdefghijklmnopqrstuvwxyz012345",
    "logging.level.org.springframework.security=DEBUG"
})
@Import({
    com.medilabo.gatewayservice.config.SecurityConfig.class, // ✅ ta sécurité WebFlux
    SecurityConfigWebFluxTest.TestRoutes.class               // ✅ routes factices
})
class SecurityConfigWebFluxTest {

    @Autowired
    WebTestClient webTestClient;

    private static final String TEST_SECRET = "0123456789abcdefghijklmnopqrstuvwxyz012345";

    private static String jwt(Map<String, Object> claims) {
        return TestJwtUtil.createHs256(TEST_SECRET, claims);
    }

    /** UI sans JWT -> 303 vers /auth/login?redirect=<original> */
    @Test
    void whenNoJwt_onUi_then303_toLogin_withOriginalPath() {
        webTestClient.get()
            .uri("/ui/patients/list")
            .exchange()
            .expectStatus().isEqualTo(303)
            .expectHeader().value(HttpHeaders.LOCATION, loc -> {
                assertThat(loc).startsWith("/auth/login?redirect=");
                assertThat(loc).contains("/ui/patients/list");
            });
    }

    /** API avec rôle insuffisant -> 403, pas de redirection */
    @Test
    void whenRoleInsufficient_onApiNotes_then403_andNoLoginRedirect() {
        String token = jwt(Map.of(
            "sub", "med",
            "roles", List.of("ORGANISATEUR"), // pas le rôle requis PRATICIEN
            "iat", Instant.now().getEpochSecond(),
            "exp", Instant.now().plusSeconds(1800).getEpochSecond()
        ));

        webTestClient.get()
            .uri("/api/notes/all")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .exchange()
            .expectStatus().isForbidden()
            .expectHeader().doesNotExist(HttpHeaders.LOCATION);
    }

    /** Handlers WebFlux de test : always-200 si la sécu ne bloque pas */
    @TestConfiguration
    static class TestRoutes {
        @Bean
        RouterFunction<ServerResponse> testRouter() {
            return route(GET("/ui/patients/list"),
                         req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("UI OK"))
                .andRoute(GET("/api/notes/all"),
                          req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("API OK"));
        }
    }
}
