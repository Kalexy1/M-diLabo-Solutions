package com.medilabo.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class TestSecurityConfig {

    @RestController
    static class DummyCtrl {
        @GetMapping("/test-open")
        public String open() {
            return "ok";
        }
    }
}
