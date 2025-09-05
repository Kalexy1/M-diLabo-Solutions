package com.medilabo.gatewayservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitaires pour {@link CookieToAuthHeaderFilter}.
 */
class CookieToAuthHeaderFilterTest {

    private final CookieToAuthHeaderFilter filter = new CookieToAuthHeaderFilter();

    @Test
    void whenAuthorizationHeaderAlreadyPresent_thenFilterDoesNotOverrideIt() {
        var request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer existing-token")
                .build();
        var exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = webExchange -> {
            assertThat(webExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo("Bearer existing-token");
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenNoCookie_thenFilterDoesNothing() {
        var request = MockServerHttpRequest.get("/test").build();
        var exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = webExchange -> {
            assertThat(webExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull();
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenCookieIsBlank_thenFilterDoesNothing() {
        var request = MockServerHttpRequest.get("/test")
                .cookie(new HttpCookie(CookieToAuthHeaderFilter.COOKIE_NAME, " "))
                .build();
        var exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = webExchange -> {
            assertThat(webExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .isNull();
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }

    @Test
    void whenCookieWithToken_thenFilterAddsAuthorizationHeader() {
        String token = "jwt-token-123";
        var request = MockServerHttpRequest.get("/test")
                .cookie(new HttpCookie(CookieToAuthHeaderFilter.COOKIE_NAME, token))
                .build();
        var exchange = MockServerWebExchange.from(request);

        WebFilterChain chain = webExchange -> {
            String authHeader = webExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            assertThat(authHeader).isEqualTo("Bearer " + token);
            return Mono.empty();
        };

        StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();
    }
}
