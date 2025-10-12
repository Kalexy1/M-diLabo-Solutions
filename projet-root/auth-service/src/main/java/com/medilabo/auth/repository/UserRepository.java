package com.medilabo.auth.repository;

import com.medilabo.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username); // username stocké en lowercase
}
