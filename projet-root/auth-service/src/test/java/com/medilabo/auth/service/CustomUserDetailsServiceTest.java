package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.model.UserRole;
import com.medilabo.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

class CustomUserDetailsServiceTest {

    @Test
    void loadUser_returns_user_with_correct_role() {
        UserRepository repo = Mockito.mock(UserRepository.class);
        CustomUserDetailsService service = new CustomUserDetailsService(repo);

        AppUser u = new AppUser();
        u.setUsername("med");
        u.setPassword("{bcrypt}pw");
        u.setRole(UserRole.PRATICIEN);

        Mockito.when(repo.findByUsername(anyString())).thenReturn(Optional.of(u));

        UserDetails ud = service.loadUserByUsername(" MED ");
        assertThat(ud.getUsername()).isEqualTo("med");
        assertThat(ud.getAuthorities()).extracting("authority").containsExactly("ROLE_PRATICIEN");
    }
}
