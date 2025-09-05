package com.medilabo.gatewayservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@WebFluxTest
@EnableAutoConfiguration(exclude = {
        org.springframework.cloud.gateway.config.GatewayAutoConfiguration.class,
        ReactiveOAuth2ClientAutoConfiguration.class,
        ReactiveOAuth2ResourceServerAutoConfiguration.class
})
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigTest {

    @TestConfiguration
    static class TestUsers {
        @Bean
        MapReactiveUserDetailsService uds() {
            UserDetails praticien = User.withUsername("praticien").password("{noop}pass").roles("PRATICIEN").build();
            UserDetails orga = User.withUsername("orga").password("{noop}pass").roles("ORGANISATEUR").build();
            UserDetails user = User.withUsername("user").password("{noop}pass").roles("USER").build();
            return new MapReactiveUserDetailsService(praticien, orga, user);
        }
    }

    @Autowired
    WebTestClient webTestClient;

    @Test
    void authPaths_arePermitAll_andReturn404IfNoRoute() {
        webTestClient.get().uri("/auth/login")
                .exchange()
                .expectStatus().isNotFound();
        webTestClient.get().uri("/access-denied")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void anonymous_onPatients_redirectsToLogin() {
        webTestClient.get().uri("/patients/1")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login$");
    }

    @Test
    void anonymous_onNotes_redirectsToLogin() {
        webTestClient.get().uri("/notes/1")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*/login$");
    }

    @Test
    void praticien_canAccessPatientsAndNotes_but404WithoutRoute() {
        webTestClient.mutateWith(mockUser("praticien").roles("PRATICIEN"))
                .get().uri("/patients/1").exchange().expectStatus().isNotFound();

        webTestClient.mutateWith(mockUser("praticien").roles("PRATICIEN"))
                .get().uri("/notes/1").exchange().expectStatus().isNotFound();

        webTestClient.mutateWith(mockUser("praticien").roles("PRATICIEN"))
                .get().uri("/risk/1").exchange().expectStatus().isNotFound();
    }

    @Test
    void organisateur_canAccessPatients_butForbiddenOnNotesAndRisk() {
        webTestClient.mutateWith(mockUser("orga").roles("ORGANISATEUR"))
                .get().uri("/patients/1").exchange().expectStatus().isNotFound();

        webTestClient.mutateWith(mockUser("orga").roles("ORGANISATEUR"))
                .get().uri("/notes/1").exchange()
                .expectStatus().isEqualTo(303)
                .expectHeader().valueEquals("Location", "/access-denied");

        webTestClient.mutateWith(mockUser("orga").roles("ORGANISATEUR"))
                .get().uri("/risk/1").exchange()
                .expectStatus().isEqualTo(303)
                .expectHeader().valueEquals("Location", "/access-denied");
    }

    @Test
    void plainUser_isAuthenticatedButForbidden_everywhere() {
        webTestClient.mutateWith(mockUser("user").roles("USER"))
                .get().uri("/patients/1").exchange()
                .expectStatus().isEqualTo(303)
                .expectHeader().valueEquals("Location", "/access-denied");
    }
}
