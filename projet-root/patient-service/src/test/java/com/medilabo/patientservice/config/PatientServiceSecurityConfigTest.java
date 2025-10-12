package com.medilabo.patientservice.config;

import com.medilabo.patientservice.PatientServiceApplication;
import com.medilabo.patientservice.model.Patient;
import com.medilabo.patientservice.service.PatientService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        classes = PatientServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ImportAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "JWT_SECRET=0123456789abcdefghijklmnopqrstuvwxyz012345",
        "spring.main.allow-bean-definition-overriding=true"
})
class PatientApiSecurityIT {

    @Autowired MockMvc mockMvc;

    @MockBean PatientService patientService;

    private static final String SECRET = "0123456789abcdefghijklmnopqrstuvwxyz012345";

    private static String jwt(String sub, List<String> roles, long ttlSeconds) throws Exception {
        Instant now = Instant.now();
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(sub)
                .issueTime(java.util.Date.from(now))
                .expirationTime(java.util.Date.from(now.plusSeconds(ttlSeconds)))
                .claim("roles", roles)
                .build();
        JWSSigner signer = new MACSigner(SECRET.getBytes(StandardCharsets.UTF_8));
        SignedJWT jwt = new SignedJWT(
                new com.nimbusds.jose.JWSHeader(JWSAlgorithm.HS256),
                claims
        );
        jwt.sign(signer);
        return jwt.serialize();
    }

    // ---------- GET /api/patients ----------

    @Test
    void get_all_unauthenticated_401() throws Exception {
        mockMvc.perform(get("/api/patients"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void get_all_asPraticien_200() throws Exception {
        String token = jwt("doc", List.of("PRATICIEN"), 600);

        when(patientService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/patients")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(content().json("[]"));

        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Marie");
        p.setLastName("Curie");
        p.setBirthDate(LocalDate.of(1867, 11, 7));
        p.setGender("F");
        when(patientService.findAll()).thenReturn(List.of(p));

        mockMvc.perform(get("/api/patients")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].lastName").value("Curie"));
    }

    // ---------- GET /api/patients/{id} ----------

    @Test
    void get_byId_asOrganisateur_200() throws Exception {
        String token = jwt("orga", List.of("ORGANISATEUR"), 600);

        Patient p = new Patient();
        p.setId(5L);
        p.setFirstName("Marie");
        p.setLastName("Curie");
        p.setBirthDate(LocalDate.of(1867, 11, 7));
        p.setGender("F");
        when(patientService.getById(5L)).thenReturn(p);

        mockMvc.perform(get("/api/patients/5")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.lastName").value("Curie"));
    }

    // ---------- POST /api/patients (create) ----------

    @Test
    void post_asOrganisateur_201_and_body() throws Exception {
        String token = jwt("orga", List.of("ORGANISATEUR"), 600);

        Patient saved = new Patient();
        saved.setId(10L);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setBirthDate(LocalDate.of(1990, 1, 1));
        saved.setGender("M");

        when(patientService.create(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                 {
                                   "firstName":"John",
                                   "lastName":"Doe",
                                   "birthDate":"1990-01-01",
                                   "gender":"M"
                                 }
                                 """))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(10L))
               .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(patientService).create(any(Patient.class));
    }

    @Test
    void post_asPraticien_403() throws Exception {
        String token = jwt("doc", List.of("PRATICIEN"), 600);

        mockMvc.perform(post("/api/patients")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                 {
                                   "firstName":"John",
                                   "lastName":"Doe",
                                   "birthDate":"1990-01-01",
                                   "gender":"M"
                                 }
                                 """))
               .andExpect(status().isForbidden());

        verify(patientService, never()).create(any());
    }

    // ---------- PUT /api/patients/{id} (update) ----------

    @Test
    void put_asOrganisateur_200() throws Exception {
        String token = jwt("orga", List.of("ORGANISATEUR"), 600);

        Patient updated = new Patient();
        updated.setId(7L);
        updated.setFirstName("Neo");
        updated.setLastName("Anderson");
        updated.setBirthDate(LocalDate.of(1980, 1, 1));
        updated.setGender("M");

        when(patientService.update(eq(7L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/7")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                 {
                                   "firstName":"Neo",
                                   "lastName":"Anderson",
                                   "birthDate":"1980-01-01",
                                   "gender":"M"
                                 }
                                 """))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.firstName").value("Neo"));

        verify(patientService).update(eq(7L), any(Patient.class));
    }

    @Test
    void put_asPraticien_403() throws Exception {
        String token = jwt("doc", List.of("PRATICIEN"), 600);

        mockMvc.perform(put("/api/patients/5")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType("application/json")
                        .content("""
                                 {
                                   "firstName":"Neo",
                                   "lastName":"Anderson",
                                   "birthDate":"1980-01-01",
                                   "gender":"M"
                                 }
                                 """))
               .andExpect(status().isForbidden());

        verify(patientService, never()).update(anyLong(), any());
    }

    // ---------- DELETE /api/patients/{id} ----------

    @Test
    void delete_asOrganisateur_204() throws Exception {
        String token = jwt("orga", List.of("ORGANISATEUR"), 600);

        doNothing().when(patientService).delete(7L);

        mockMvc.perform(delete("/api/patients/7")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isNoContent());

        verify(patientService).delete(7L);
    }

    @Test
    void delete_asPraticien_403() throws Exception {
        String token = jwt("doc", List.of("PRATICIEN"), 600);

        mockMvc.perform(delete("/api/patients/7")
                        .with(csrf())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
               .andExpect(status().isForbidden());

        verify(patientService, never()).delete(anyLong());
    }
}
