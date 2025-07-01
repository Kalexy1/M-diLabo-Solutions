package com.medilabo.patientui.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medilabo.patientui.model.AppUser;

/**
 * Interface de repository pour l'entité {@link AppUser}.
 * Fournit des méthodes CRUD et des requêtes personnalisées via Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur
     * @return un {@link Optional} contenant l'utilisateur s'il est trouvé, sinon vide
     */
    Optional<AppUser> findByUsername(String username);
}
