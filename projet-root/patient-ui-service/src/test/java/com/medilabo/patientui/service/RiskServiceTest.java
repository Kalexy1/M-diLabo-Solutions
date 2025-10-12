package com.medilabo.patientui.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patientui.model.RiskAssessmentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RiskServiceTest {

    private ExchangeFunction exchange;
    private WebClient webClient;
    private RiskService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        exchange = mock(ExchangeFunction.class);
        webClient = WebClient.builder().exchangeFunction(exchange).build();
        service = new RiskService(webClient);
    }

    @Test
    void getRisk_withJwt_addsBearerHeader_andReturnsBody() throws Exception {
        RiskAssessmentResponse resp = new RiskAssessmentResponse();
        resp.setRiskLevel("IN_DANGER");
        String json = mapper.writeValueAsString(resp);

        when(exchange.exchange(any(ClientRequest.class))).thenAnswer(inv -> {
            ClientRequest req = inv.getArgument(0);

            assertThat(req.url().getPath()).endsWith("/7");

            assertThat(req.headers().getFirst("Authorization")).isEqualTo("Bearer jwt-123");

            var buf = new DefaultDataBufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            ClientResponse clientResp = ClientResponse
                    .create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(Flux.just(new DefaultDataBufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))))
                    .build();
            return Mono.just(clientResp);
        });

        RiskAssessmentResponse out = service.getRisk(7L, "jwt-123");
        assertThat(out).isNotNull();
        assertThat(out.getRiskLevel()).isEqualTo("IN_DANGER");

        verify(exchange, times(1)).exchange(any(ClientRequest.class));
        verifyNoMoreInteractions(exchange);
    }

    @Test
    void getRisk_withoutJwt_sendsEmptyBearer_andReturnsBody() throws Exception {
        RiskAssessmentResponse resp = new RiskAssessmentResponse();
        resp.setRiskLevel("LOW");
        String json = mapper.writeValueAsString(resp);

        when(exchange.exchange(any(ClientRequest.class))).thenAnswer(inv -> {
            ClientRequest req = inv.getArgument(0);
            assertThat(req.url().getPath()).endsWith("/42");
            assertThat(req.headers().getFirst("Authorization")).isEqualTo("Bearer ");

            var buf = new DefaultDataBufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            ClientResponse clientResp = ClientResponse
                    .create(HttpStatus.OK)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .body(Flux.just(new DefaultDataBufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8))))
                    .build();
            return Mono.just(clientResp);
        });

        RiskAssessmentResponse out = service.getRisk(42L, null);
        assertThat(out).isNotNull();
        assertThat(out.getRiskLevel()).isEqualTo("LOW");

        verify(exchange, times(1)).exchange(any(ClientRequest.class));
        verifyNoMoreInteractions(exchange);
    }
}
