package com.medilabo.patientui.controller;

import com.medilabo.patientui.model.Patient;
import com.medilabo.patientui.model.RiskAssessmentResponse;
import com.medilabo.patientui.service.NoteService;
import com.medilabo.patientui.service.PatientService;
import com.medilabo.patientui.service.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = PatientController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class,
                OAuth2ClientAutoConfiguration.class,
                OAuth2ResourceServerAutoConfiguration.class,
                ThymeleafAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ImportAutoConfiguration({
        HttpMessageConvertersAutoConfiguration.class,
        GsonAutoConfiguration.class,
        JsonbAutoConfiguration.class,
        FreeMarkerAutoConfiguration.class,
        MustacheAutoConfiguration.class
})
class PatientControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PatientService patientService;

    @MockBean
    NoteService noteService;

    @MockBean
    RiskService riskService;

    @TestConfiguration
    static class NoOpViewResolverConfig {

        private static View noOpView() {
            return new View() {
                @Override
                public String getContentType() {
                    return "text/html";
                }

                @Override
                public void render(Map<String, ?> model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
                    // no-op : on évite Thymeleaf pendant les tests
                }
            };
        }

        /** Ne résout PAS redirect:/ ni forward:/ pour laisser Spring renvoyer 3xx. */
        static class HighPriorityNoOpResolver implements ViewResolver, Ordered {
            @Override
            public View resolveViewName(String viewName, Locale locale) {
                if (viewName == null) return null;
                if (viewName.startsWith("redirect:")) return null;
                if (viewName.startsWith("forward:")) return null;
                return noOpView();
            }

            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }
        }

        @Bean(name = "thymeleafViewResolver")
        @Primary
        ViewResolver thymeleafViewResolver() {
            return new HighPriorityNoOpResolver();
        }

        @Bean
        @Primary
        ViewResolver viewResolver() {
            return new HighPriorityNoOpResolver();
        }
    }

    @BeforeEach
    void setUp() {
        // findAll(HttpServletRequest)
        given(patientService.findAll(any(HttpServletRequest.class)))
                .willReturn(List.of(new Patient()));

        // getOne(Long, HttpServletRequest)
        Patient p = new Patient();
        p.setId(1L);
        given(patientService.getOne(eq(1L), any(HttpServletRequest.class))).willReturn(p);

        // notes.findByPatient(Long, HttpServletRequest)
        given(noteService.findByPatient(eq(1L), any(HttpServletRequest.class)))
                .willReturn(List.of());

        // risk.getRisk(Long, HttpServletRequest)
        RiskAssessmentResponse risk = new RiskAssessmentResponse();
        risk.setPatientId(1);
        risk.setRiskLevel("NONE");
        risk.setTriggerCount(0);
        given(riskService.getRisk(eq(1L), any(HttpServletRequest.class))).willReturn(risk);
    }

    // ====== les chemins internes ne contiennent PAS /ui ======

    @Test
    void list_returns_200() throws Exception {
        mvc.perform(get("/patients"))
                .andExpect(status().isOk());
    }

    @Test
    void home_redirects_to_list_internally() throws Exception {
        // "/" appelle home(), qui renvoie listPatients() => 200
        mvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void showAddForm_returns_200() throws Exception {
        mvc.perform(get("/patients/new"))
                .andExpect(status().isOk());
    }

    @Test
    void showEditForm_returns_200() throws Exception {
        mvc.perform(get("/patients/edit/1"))
                .andExpect(status().isOk());
    }

    @Test
    void riskReport_returns_200() throws Exception {
        mvc.perform(get("/patients/1/risk"))
                .andExpect(status().isOk());
    }

    @Test
    void patientNotes_returns_200() throws Exception {
        mvc.perform(get("/patients/1/notes"))
                .andExpect(status().isOk());
    }

    @Test
    void create_returns_200_and_calls_service() throws Exception {
        mvc.perform(post("/patients").with(csrf())
                        .param("firstName", "Bob")
                        .param("lastName", "Martin"))
                .andExpect(status().isOk());

        // on vérifie que le service de création est bien appelé
        verify(patientService).create(any(Patient.class), any(HttpServletRequest.class));
    }

    @Test
    void update_returns_200_and_calls_service() throws Exception {
        mvc.perform(post("/patients/update").with(csrf())
                        .param("id", "1")
                        .param("firstName", "Alice")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk());

        verify(patientService).update(eq(1L), any(Patient.class), any(HttpServletRequest.class));
    }

    @Test
    void delete_returns_200_and_calls_service() throws Exception {
        mvc.perform(post("/patients/delete/1").with(csrf()))
                .andExpect(status().isOk());

        verify(patientService).delete(eq(1L), any(HttpServletRequest.class));
    }
}
