package com.medilabo.gatewayservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Field;
import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.medilabo.gatewayservice.TestSecurityConfig;

@WebMvcTest(UiProxyController.class)
@ContextConfiguration(classes = { UiProxyController.class, TestSecurityConfig.class })
@Import(TestSecurityConfig.class)  // Désactive la sécurité pour les tests MockMvc
class UiProxyControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UiProxyController controller;

    @MockBean
    private RestTemplate restTemplate;  // Mock qui remplace le new RestTemplate() interne

    @BeforeEach
    void setup() throws Exception {
        // Injection par réflexion pour remplacer le RestTemplate interne
        Field f = UiProxyController.class.getDeclaredField("restTemplate");
        f.setAccessible(true);
        f.set(controller, restTemplate);
    }

    // ---------------------------------------------------------
    // 1. SUCCESS - Le backend UI renvoie 200 OK
    // ---------------------------------------------------------
    @Test
    void testProxyUiSuccess() throws Exception {

        byte[] responseBody = "UI_OK".getBytes();

        ResponseEntity<byte[]> backendResponse =
                new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(restTemplate.exchange(
                any(URI.class),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(byte[].class)
        )).thenReturn(backendResponse);

        mvc.perform(get("/ui/home"))
                .andExpect(status().isOk())
                .andExpect(content().string("UI_OK"));
    }

    // ---------------------------------------------------------
    // 2. BACKEND → renvoie 404 Not Found
    // ---------------------------------------------------------
    @Test
    void testProxyUiBackend404() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "active");

        HttpStatusCodeException ex = new HttpStatusCodeException(
                HttpStatus.NOT_FOUND,
                "Not Found",
                headers,
                "NOT_FOUND_UI".getBytes(),
                null
        ) {};

        when(restTemplate.exchange(any(URI.class), any(), any(), eq(byte[].class)))
                .thenThrow(ex);

        mvc.perform(get("/ui/page"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("X-Test", "active"))
                .andExpect(content().string("NOT_FOUND_UI"));
    }

    // ---------------------------------------------------------
    // 3. BACKEND inaccessible → ResourceAccessException
    // ---------------------------------------------------------
    @Test
    void testProxyUiBackendUnavailable() throws Exception {

        when(restTemplate.exchange(
                any(URI.class),
                any(),
                any(),
                eq(byte[].class)
        )).thenThrow(new ResourceAccessException("Connection failed"));

        mvc.perform(get("/ui/dashboard"))
                .andExpect(status().isBadGateway())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Bad Gateway")));
    }
}
