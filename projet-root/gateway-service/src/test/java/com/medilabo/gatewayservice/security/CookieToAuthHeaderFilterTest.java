package com.medilabo.gatewayservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class CookieToAuthHeaderFilterTest {

    private final CookieToAuthHeaderFilter filter = new CookieToAuthHeaderFilter();

    private static GatewayFilterChain chainAsserting(java.util.function.Consumer<ServerWebExchange> assertions) {
        return new GatewayFilterChain() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange) {
                assertions.accept(exchange);
                return Mono.empty();
            }
        };
    }

    @Test
    void whenAuthorizationHeaderAlreadyPresent_thenFilterDoesNotOverrideIt() {
        var request = MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer existing-token")
            .build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer existing-token")
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenNoCookie_thenFilterDoesNothing() {
        var request = MockServerHttpRequest.get("/test").build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull()
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenCookieIsBlank_thenFilterDoesNothing() {
        var request = MockServerHttpRequest.get("/test")
            .cookie(new HttpCookie(CookieToAuthHeaderFilter.COOKIE_NAME, " "))
            .build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull()
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenCookieWithToken_thenFilterAddsAuthorizationHeader() {
        String token = "jwt-token-123";
        var request = MockServerHttpRequest.get("/test")
            .cookie(new HttpCookie(CookieToAuthHeaderFilter.COOKIE_NAME, token))
            .build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer " + token)
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenPreflightRequest_thenFilterDoesNothing() {
        var request = MockServerHttpRequest.method(HttpMethod.OPTIONS, "/test").build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull()
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenPathExcluded_authOrActuator_thenFilterDoesNothing() {
        String token = "jwt-token-456";
        var request = MockServerHttpRequest.get("/auth/login")
            .cookie(new HttpCookie(CookieToAuthHeaderFilter.COOKIE_NAME, token))
            .build();
        var exchange = MockServerWebExchange.from(request);

        var chain = chainAsserting(ex ->
            assertThat(ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull()
        );

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }
}
