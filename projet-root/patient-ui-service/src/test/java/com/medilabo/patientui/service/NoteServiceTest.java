package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Note;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class NoteServiceTest {

    /** Fabrique un WebClient qui renvoie (status, bodyJson), en capturant le header Authorization si demandé. */
    private static WebClient clientReturning(
            HttpStatus status, String bodyJson, AtomicReference<String> seenAuthHeader) {

        ExchangeFunction fn = request -> {
            if (seenAuthHeader != null) {
                seenAuthHeader.set(request.headers().getFirst(HttpHeaders.AUTHORIZATION));
            }
            var factory = new DefaultDataBufferFactory();
            Flux<DataBuffer> body = (bodyJson == null || bodyJson.isBlank())
                    ? Flux.empty()
                    : Flux.just(factory.wrap(bodyJson.getBytes(StandardCharsets.UTF_8)));

            ClientResponse resp = ClientResponse.create(status)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .build();
            return Mono.just(resp);
        };

        // Doit être cohérent avec ton AppConfig pour notes.api.base (peu importe la valeur exacte ici)
        return WebClient.builder()
                .baseUrl("http://example.test/api/notes")
                .exchangeFunction(fn)
                .build();
    }

    @Test
    void findByPatient_returnsList_and_sendsAuthorization() {
        String json = """
            [
              {"id":1,"patientId":99,"content":"Vertiges"},
              {"id":2,"patientId":99,"content":"Taille 172cm"}
            ]
        """;
        AtomicReference<String> seenAuth = new AtomicReference<>();
        WebClient client = clientReturning(HttpStatus.OK, json, seenAuth);

        NoteService service = new NoteService(client);

        List<Note> notes = service.findByPatient(99L, "jwt-123");
        assertThat(notes).hasSize(2);
        assertThat(notes.get(0).getId()).isEqualTo(1L);
        assertThat(notes.get(0).getPatientId()).isEqualTo(99L);
        assertThat(notes.get(0).getContent()).isEqualTo("Vertiges");
        assertThat(seenAuth.get()).isEqualTo("Bearer jwt-123");
    }

    @Test
    void findByPatient_emptyBody_returnsEmptyList() {
        WebClient client = clientReturning(HttpStatus.OK, "[]", null);
        NoteService service = new NoteService(client);

        List<Note> notes = service.findByPatient(123L, "t");
        assertThat(notes).isEmpty();
    }

    @Test
    void createForPatient_returnsCreatedNote() {
        String json = """
            {"id":1001,"patientId":77,"content":"Nouvelle note"}
        """;
        WebClient client = clientReturning(HttpStatus.CREATED, json, null);

        NoteService service = new NoteService(client);

        Note payload = new Note();
        payload.setContent("Nouvelle note");

        Note saved = service.createForPatient(77L, payload, "jwt-x");
        assertThat(saved.getId()).isEqualTo(1001L);
        assertThat(saved.getPatientId()).isEqualTo(77L);
        assertThat(saved.getContent()).isEqualTo("Nouvelle note");
    }
}
