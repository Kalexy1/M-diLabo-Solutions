package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Note;
import com.medilabo.patientui.web.JwtCookieUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class NoteServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        // RestTemplate configuré comme dans AppConfig (rootUri = base URL de l’API notes)
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://example.test/api/notes")
        );

        server = MockRestServiceServer.bindTo(restTemplate).build();

        // Le NoteService injecte normalement le bean "noteApiClient",
        // mais en test on lui passe directement ce RestTemplate.
        noteService = new NoteService(restTemplate);
    }

    @Test
    void findByPatient_returnsList_and_sendsAuthorization() {
        String json = """
            [
              {"id":1,"patientId":99,"content":"Vertiges"},
              {"id":2,"patientId":99,"content":"Taille 172cm"}
            ]
        """;

        // Avec rootUri = http://example.test/api/notes
        // et path = "/patient/99", l’URL finale est :
        // http://example.test/api/notes/patient/99
        server.expect(once(),
                      requestTo("http://example.test/api/notes/patient/99"))
              .andExpect(method(org.springframework.http.HttpMethod.GET))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-123"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=jwt-123"))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "jwt-123"));

        List<Note> notes = noteService.findByPatient(99L, req);

        server.verify();
        assertThat(notes).hasSize(2);
        assertThat(notes.get(0).getId()).isEqualTo(1L);
        assertThat(notes.get(0).getPatientId()).isEqualTo(99L);
        assertThat(notes.get(0).getContent()).isEqualTo("Vertiges");
    }

    @Test
    void findByPatient_emptyBody_returnsEmptyList() {
        server.expect(once(),
                      requestTo("http://example.test/api/notes/patient/123"))
              .andExpect(method(org.springframework.http.HttpMethod.GET))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body("[]"));

        MockHttpServletRequest req = new MockHttpServletRequest();
        // pas de cookie, on teste juste le body vide

        List<Note> notes = noteService.findByPatient(123L, req);

        server.verify();
        assertThat(notes).isEmpty();
    }

    @Test
    void createForPatient_returnsCreatedNote() {
        String json = """
            {"id":1001,"patientId":77,"content":"Nouvelle note"}
        """;

        server.expect(once(),
                      requestTo("http://example.test/api/notes/patient/77"))
              .andExpect(method(org.springframework.http.HttpMethod.POST))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-x"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=jwt-x"))
              .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withStatus(HttpStatus.CREATED)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "jwt-x"));

        Note payload = new Note();
        payload.setContent("Nouvelle note");

        Note saved = noteService.createForPatient(77L, payload, req);

        server.verify();
        assertThat(saved.getId()).isEqualTo(1001L);
        assertThat(saved.getPatientId()).isEqualTo(77L);
        assertThat(saved.getContent()).isEqualTo("Nouvelle note");
    }
}
