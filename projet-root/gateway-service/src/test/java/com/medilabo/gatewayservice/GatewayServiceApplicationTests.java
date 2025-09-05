package com.medilabo.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.gateway.enabled=true",
        "spring.main.web-application-type=reactive",
        "server.port=0",
        "JWT_SECRET=test-secret-for-tests-please-32-bytes-min-123456"
    }
)
@ActiveProfiles("test")
class GatewayServiceApplicationTest {

    @Autowired RouteLocator routeLocator;
    @Autowired WebTestClient webTestClient;
    @Autowired ApplicationContext applicationContext;

    @Test
    void main_startsApplication_withGatewayDisabled_reactive() {
        SpringApplication app = new SpringApplication(GatewayServiceApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.setDefaultProperties(Map.of(
            "spring.cloud.gateway.enabled", "false",
            "server.port", "0",
            "JWT_SECRET", "test-secret-for-tests-please-32-bytes-min-123456"
        ));
        try (var ctx = app.run()) {
            assertThat(ctx).isNotNull();
            assertThat(ctx.getBeanProvider(RouteLocator.class).getIfAvailable()).isNull();
        }
    }

    @Test
    void shouldBuildAllExpectedRoutes() {
        Set<String> ids = routeLocator.getRoutes()
            .map(Route::getId)
            .collectList()
            .block()
            .stream()
            .collect(Collectors.toSet());

        assertThat(ids).contains(
            "auth-service",
            "patient-service",
            "note-service",
            "risk-assessment-service",
            "patient-ui-service"
        );
    }

    @Test
    void anonymous_protectedRoutes_redirectToLogin() {
        webTestClient.get().uri("/patients/any")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/auth/login");

        webTestClient.get().uri("/notes/any")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/auth/login");

        webTestClient.get().uri("/risk/any")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/auth/login");

        webTestClient.get().uri("/ui/index.html")
            .exchange()
            .expectStatus().is3xxRedirection()
            .expectHeader().valueEquals("Location", "/auth/login");
    }
}
