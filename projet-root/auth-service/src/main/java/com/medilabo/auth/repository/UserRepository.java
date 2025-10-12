package com.medilabo.auth.repository;

import com.medilabo.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Référentiel (repository) Spring Data JPA pour l'entité {@link AppUser}.
 * <p>
 * Cette interface permet d'effectuer des opérations CRUD sur les utilisateurs,
 * ainsi que des recherches spécifiques par nom d'utilisateur.
 * </p>
 */
public interface UserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Recherche un utilisateur à partir de son nom d'utilisateur.
     * <p>
     * Le nom d'utilisateur est stocké en minuscules dans la base de données.
     * </p>
     *
     * @param username le nom d'utilisateur à rechercher
     * @return un {@link Optional} contenant l'utilisateur correspondant, ou vide si aucun résultat
     */
    Optional<AppUser> findByUsername(String username);
}
