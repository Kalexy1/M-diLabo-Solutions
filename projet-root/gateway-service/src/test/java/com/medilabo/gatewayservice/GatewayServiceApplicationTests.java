package com.medilabo.gatewayservice;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.gateway.enabled=true",
        "spring.main.web-application-type=reactive"
    }
)
@ActiveProfiles("test")
class GatewayServiceApplicationTest {

    @Autowired
    RouteLocator routeLocator;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void customRouteLocator_callsRouteWithExpectedIds() {
        RouteLocatorBuilder builder = mock(RouteLocatorBuilder.class);
        RouteLocatorBuilder.Builder routesBuilder = mock(RouteLocatorBuilder.Builder.class);
        RouteLocator locator = mock(RouteLocator.class);

        when(builder.routes()).thenReturn(routesBuilder);
        when(routesBuilder.route(anyString(), any())).thenReturn(routesBuilder);
        when(routesBuilder.build()).thenReturn(locator);

        RouteLocator result = new GatewayServiceApplication().customRouteLocator(builder);

        assertThat(result).isSameAs(locator);

        verify(builder, times(1)).routes();

        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(routesBuilder, atLeast(1)).route(idCaptor.capture(), any(Function.class));

        List<String> ids = idCaptor.getAllValues();
        assertThat(ids).contains(
            "auth-service",
            "patient-service",
            "note-service",
            "risk-service",
            "patient-ui-service"
        );

        verify(routesBuilder).build();
        verifyNoMoreInteractions(routesBuilder);
    }

    @Test
    void main_startsApplication_withGatewayDisabled_reactive() {
        SpringApplication app = new SpringApplication(GatewayServiceApplication.class);
        app.setWebApplicationType(WebApplicationType.REACTIVE);
        app.setDefaultProperties(Map.of(
            "spring.cloud.gateway.enabled", "false",
            "server.port", "0"
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
            "risk-service",
            "patient-ui-service"
        );
    }

    @Test
    void anonymous_protectedRoutes_redirectToLogin() {
        webTestClient.get().uri("/patients/any").exchange().expectStatus().is3xxRedirection();
        webTestClient.get().uri("/notes/any").exchange().expectStatus().is3xxRedirection();
        webTestClient.get().uri("/assess/any").exchange().expectStatus().is3xxRedirection();
        webTestClient.get().uri("/ui/index.html").exchange().expectStatus().is3xxRedirection();
    }

    @Test
    void authenticated_routesReturn5xxOr404WithoutBackends() {
        WebTestClient securityClient = WebTestClient.bindToApplicationContext(applicationContext)
            .apply(springSecurity())
            .configureClient()
            .build();

        expect5xxOr404(securityClient.mutateWith(mockUser("orga").roles("ORGANISATEUR")), "/patients/any");
        expect5xxOr404(securityClient.mutateWith(mockUser("praticien").roles("PRATICIEN")), "/notes/any");
        expect5xxOr404(securityClient.mutateWith(mockUser("praticien").roles("PRATICIEN")), "/risk/any");
        expect5xxOr404(securityClient.mutateWith(mockUser("orga").roles("ORGANISATEUR")), "/ui/index.html");
        expect5xxOr404(securityClient, "/auth/login");
    }

    private void expect5xxOr404(WebTestClient client, String uri) {
        EntityExchangeResult<byte[]> res = client.get().uri(uri).exchange().expectBody().returnResult();
        int status = res.getStatus().value();
        boolean ok = (status >= 500 && status <= 599) || status == 404;
        assertThat(ok).isTrue();
    }
}
