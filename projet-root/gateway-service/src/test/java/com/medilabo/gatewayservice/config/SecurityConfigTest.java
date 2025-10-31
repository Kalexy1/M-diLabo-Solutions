package com.medilabo.gatewayservice.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@WebFluxTest
@TestPropertySource(properties = {
    "logging.level.org.springframework.security=DEBUG"
})
@Import({
    com.medilabo.gatewayservice.config.SecurityConfig.class,
    SecurityConfigWebFluxTest.TestRoutes.class
})
class SecurityConfigWebFluxTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenNoJwt_onUi_then401() {
        webTestClient.get()
            .uri("/ui/patients/list")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void whenNoJwt_onApiNotes_then401() {
        webTestClient.get()
            .uri("/api/notes/all")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void publicEndpointsPassThrough() {
        webTestClient.get()
            .uri("/auth/login")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_PLAIN);
    }

    @Test
    void wellKnown_isProtected_byDefault() {
        webTestClient.get()
            .uri("/.well-known/appspecific/com.chrome.devtools.json")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    void preservesAuthorizationHeader_butStillRequiresValidAuth() {
        String token = "Bearer SOME_TOKEN";
        webTestClient.get()
            .uri("/api/notes/all")
            .header(HttpHeaders.AUTHORIZATION, token)
            .exchange()
            .expectStatus().isUnauthorized();
    }

    static class TestRoutes {
        @Bean
        RouterFunction<ServerResponse> testRouter() {
            return route(GET("/ui/patients/list"),
                         req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("UI OK"))
                .andRoute(GET("/api/notes/all"),
                          req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("API OK"))
                .andRoute(GET("/auth/login"),
                          req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("LOGIN PAGE"))
                .andRoute(GET("/.well-known/appspecific/com.chrome.devtools.json"),
                          req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("WELL KNOWN"));
        }
    }
}
