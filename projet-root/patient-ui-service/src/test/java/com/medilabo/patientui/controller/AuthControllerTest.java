package com.medilabo.patientui.controller;

import com.medilabo.patientui.controller.AuthController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // on ne teste pas la sécu ici
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // évite l'échec si les templates ne sont pas présents en test
        "spring.thymeleaf.check-template-location=false"
})
class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired RequestMappingHandlerMapping mapping;

    private String findPathEndingWith(String suffix) {
        for (Map.Entry<RequestMappingInfo, HandlerMethod> e : mapping.getHandlerMethods().entrySet()) {
            Set<String> patterns = e.getKey().getPatternValues();
            for (String p : patterns) {
                if (p.endsWith(suffix)) return p;
                if (p.endsWith("/ui" + suffix)) return p;
                if (p.endsWith("/ui" + suffix.replaceFirst("^/", ""))) return p;
            }
        }
        return null;
    }

    @Test
    void accessDenied_view() throws Exception {
        String path = findPathEndingWith("/access-denied");
        assertThat(path).as("mapping pour /access-denied").isNotNull();

        mvc.perform(get(path))
           .andDo(print())
           .andExpect(status().isOk());
    }
}
