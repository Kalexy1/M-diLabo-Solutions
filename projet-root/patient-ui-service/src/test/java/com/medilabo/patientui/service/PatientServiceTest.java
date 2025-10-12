package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.*;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class PatientServiceTest {

    private static WebClient clientReturning(
            HttpStatus status, String jsonBody, AtomicReference<String> seenAuthHeader
    ) {
        ExchangeFunction fn = req -> {
            if (seenAuthHeader != null) {
                seenAuthHeader.set(req.headers().getFirst(HttpHeaders.AUTHORIZATION));
            }
            DefaultDataBufferFactory f = new DefaultDataBufferFactory();
            Flux<DataBuffer> body = (jsonBody == null || jsonBody.isEmpty())
                    ? Flux.empty()
                    : Flux.just(f.wrap(jsonBody.getBytes(StandardCharsets.UTF_8)));

            ClientResponse resp = ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .build();
            return Mono.just(resp);
        };

        return WebClient.builder()
                .baseUrl("http://example.test/api/patients")
                .exchangeFunction(fn)
                .build();
    }

    @Test
    void findAll_returnsList_and_sendsAuthorizationHeader() {
        String json = """
            [
              {
                "id": 1,
                "firstName": "Marie",
                "lastName": "Curie",
                "birthDate": "1867-11-07",
                "gender": "F",
                "address": "Paris",
                "phone": "0102030405"
              }
            ]
            """;
        AtomicReference<String> seen = new AtomicReference<>();
        WebClient client = clientReturning(HttpStatus.OK, json, seen);

        PatientService service = new PatientService(client);

        List<Patient> res = service.findAll("jwt-abc");
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getLastName()).isEqualTo("Curie");

        assertThat(seen.get()).isEqualTo("Bearer jwt-abc");
    }

    @Test
    void getOne_returnsPatient() {
        String json = """
            {
              "id": 5,
              "firstName": "John",
              "lastName": "Doe",
              "birthDate": "1990-01-01",
              "gender": "M"
            }
            """;
        WebClient client = clientReturning(HttpStatus.OK, json, null);
        PatientService service = new PatientService(client);

        Patient p = service.getOne(5L, "t");
        assertThat(p.getId()).isEqualTo(5L);
        assertThat(p.getFirstName()).isEqualTo("John");
    }

    @Test
    void create_returnsCreatedPatient() {
        String json = """
            {
              "id": 9,
              "firstName": "Alice",
              "lastName": "Liddell",
              "birthDate": "1995-05-05",
              "gender": "F"
            }
            """;
        WebClient client = clientReturning(HttpStatus.CREATED, json, null);
        PatientService service = new PatientService(client);

        Patient req = new Patient();
        req.setFirstName("Alice");
        req.setLastName("Liddell");
        req.setBirthDate(LocalDate.of(1995,5,5));
        req.setGender("F");

        Patient saved = service.create(req, "t");
        assertThat(saved.getId()).isEqualTo(9L);
        assertThat(saved.getFirstName()).isEqualTo("Alice");
    }

    @Test
    void update_returnsUpdatedPatient() {
        String json = """
            {
              "id": 1,
              "firstName": "Updated",
              "lastName": "Curie",
              "birthDate": "1867-11-07",
              "gender": "F"
            }
            """;
        WebClient client = clientReturning(HttpStatus.OK, json, null);
        PatientService service = new PatientService(client);

        Patient payload = new Patient();
        payload.setFirstName("Updated");

        Patient updated = service.update(1L, payload, "t");
        assertThat(updated.getFirstName()).isEqualTo("Updated");
    }

    @Test
    void delete_noContent_ok() {
        WebClient client = clientReturning(HttpStatus.NO_CONTENT, null, null);
        PatientService service = new PatientService(client);

        service.delete(77L, "t");
    }
}
