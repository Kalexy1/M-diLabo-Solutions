package com.medilabo.auth.repository;

import com.medilabo.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Application: com.medilabo.auth.repository
 * <p>
 * Interface <strong>UserRepository</strong>.
 * <br/>
 * Rôle: Fournit l'accès aux données des utilisateurs via JPA.
 * </p>
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Recherche un utilisateur par son nom.
     *
     * @param username nom d'utilisateur.
     * @return AppUser trouvé ou null si inexistant.
     */
    AppUser findByUsername(String username);
}
