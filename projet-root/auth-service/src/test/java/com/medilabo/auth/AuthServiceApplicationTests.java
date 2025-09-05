package com.medilabo.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "JWT_SECRET=test-secret")
class AuthServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
