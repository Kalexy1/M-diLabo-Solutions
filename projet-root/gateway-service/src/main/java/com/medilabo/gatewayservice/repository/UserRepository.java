package com.medilabo.gatewayservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.medilabo.gatewayservice.model.AppUser;

/**
 * Repository pour la gestion des utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return un utilisateur s'il existe
     */
    Optional<AppUser> findByUsername(String username);
}
