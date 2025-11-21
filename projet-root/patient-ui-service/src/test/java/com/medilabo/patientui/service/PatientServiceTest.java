package com.medilabo.patientui.service;

import com.medilabo.patientui.model.Patient;
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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PatientServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        // RestTemplate configuré comme le bean patientApiClient :
        // rootUri = base URL de l’API patients, SANS slash final
        restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(
                new DefaultUriBuilderFactory("http://example.test/api/patients")
        );

        server = MockRestServiceServer.bindTo(restTemplate).build();
        // En prod Spring injecte @Qualifier("patientApiClient"), ici on lui passe ce RestTemplate de test
        patientService = new PatientService(restTemplate);
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

        // PatientService.findAll() appelle path "" → URL finale = http://example.test/api/patients
        server.expect(once(),
                      requestTo("http://example.test/api/patients"))
              .andExpect(method(GET))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-abc"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=jwt-abc"))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "jwt-abc"));

        List<Patient> res = patientService.findAll(req);

        server.verify();
        assertThat(res).hasSize(1);
        assertThat(res.get(0).getLastName()).isEqualTo("Curie");
        assertThat(res.get(0).getFirstName()).isEqualTo("Marie");
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

        // path "/5" → http://example.test/api/patients/5
        server.expect(once(),
                      requestTo("http://example.test/api/patients/5"))
              .andExpect(method(GET))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "t"));

        Patient p = patientService.getOne(5L, req);

        server.verify();
        assertThat(p.getId()).isEqualTo(5L);
        assertThat(p.getFirstName()).isEqualTo("John");
        assertThat(p.getLastName()).isEqualTo("Doe");
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

        // create() appelle path "" en POST → URL finale = http://example.test/api/patients
        server.expect(once(),
                      requestTo("http://example.test/api/patients"))
              .andExpect(method(POST))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer t"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=t"))
              .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withStatus(HttpStatus.CREATED)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "t"));

        Patient payload = new Patient();
        payload.setFirstName("Alice");
        payload.setLastName("Liddell");
        payload.setBirthDate(LocalDate.of(1995, 5, 5));
        payload.setGender("F");

        Patient saved = patientService.create(payload, req);

        server.verify();
        assertThat(saved.getId()).isEqualTo(9L);
        assertThat(saved.getFirstName()).isEqualTo("Alice");
        assertThat(saved.getLastName()).isEqualTo("Liddell");
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

        // path "/1" en PUT → http://example.test/api/patients/1
        server.expect(once(),
                      requestTo("http://example.test/api/patients/1"))
              .andExpect(method(PUT))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer t"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=t"))
              .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withStatus(HttpStatus.OK)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(json));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "t"));

        Patient payload = new Patient();
        payload.setFirstName("Updated");

        Patient updated = patientService.update(1L, payload, req);

        server.verify();
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getLastName()).isEqualTo("Curie");
    }

    @Test
    void delete_noContent_ok() {
        // path "/77" en DELETE → http://example.test/api/patients/77
        server.expect(once(),
                      requestTo("http://example.test/api/patients/77"))
              .andExpect(method(DELETE))
              .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer t"))
              .andExpect(header(HttpHeaders.COOKIE, JwtCookieUtil.DEFAULT_COOKIE_NAME + "=t"))
              .andRespond(withStatus(HttpStatus.NO_CONTENT));

        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setCookies(new Cookie("JWT_TOKEN", "t"));

        patientService.delete(77L, req);

        server.verify();
        // pas d’exception = OK
    }
}
