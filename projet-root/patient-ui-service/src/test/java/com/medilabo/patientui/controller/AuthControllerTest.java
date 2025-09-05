package com.medilabo.patientui.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;
import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    @Test
    void accessDenied_shouldReturnAccessDeniedView() {
        AuthController controller = new AuthController();
        String viewName = controller.accessDenied();
        assertThat(viewName).isEqualTo("access-denied");
    }
}
