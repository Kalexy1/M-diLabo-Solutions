package com.medilabo.auth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
	        .csrf().disable()
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/auth/**", "/").permitAll()
	            .anyRequest().authenticated()
	        )
	        .exceptionHandling(e -> e.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/login")))
	        .formLogin().disable()
	        .logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login?logout").permitAll());
	
	    return http.build();
	}
}