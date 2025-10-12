package com.medilabo.gatewayservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "JWT_SECRET=0123456789abcdefghijklmnopqrstuvwxyz012345"
})
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
