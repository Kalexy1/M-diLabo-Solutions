package com.medilabo.gatewayservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.medilabo.gatewayservice.model.AppUser;

/**
 * Repository Spring Data JPA pour l'accès aux utilisateurs.
 *
 * <p>Fournit les opérations CRUD de base ainsi qu'une méthode personnalisée
 * pour rechercher un utilisateur à partir de son nom d'utilisateur.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Recherche un utilisateur à partir de son nom d'utilisateur.
     *
     * @param username le nom d'utilisateur recherché
     * @return un {@link Optional} contenant l'utilisateur s'il existe, vide sinon
     */
    Optional<AppUser> findByUsername(String username);
}
