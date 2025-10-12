package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service gérant la logique métier liée aux utilisateurs.
 * <p>
 * Cette classe fournit les fonctionnalités d'inscription, de validation
 * des identifiants et de recherche d'utilisateurs en base de données.
 * </p>
 */
@Service
public class UserService {

    /**
     * Référentiel des utilisateurs.
     */
    private final UserRepository userRepository;

    /**
     * Encodeur de mots de passe utilisé pour le chiffrement et la vérification.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée une instance du service {@code UserService}.
     *
     * @param userRepository le repository d'accès aux utilisateurs
     * @param passwordEncoder l'encodeur de mots de passe
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Normalise un nom d'utilisateur (trim et conversion en minuscules).
     *
     * @param username le nom d'utilisateur à normaliser
     * @return le nom normalisé ou {@code null} si l'entrée est nulle
     */
    private String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }

    /**
     * Inscrit un nouvel utilisateur dans la base de données.
     * <p>
     * Vérifie l’unicité du nom d’utilisateur et encode le mot de passe
     * avec BCrypt avant de sauvegarder l'utilisateur.
     * </p>
     *
     * @param user l'utilisateur à enregistrer
     * @return l'utilisateur sauvegardé
     * @throws IllegalArgumentException si le nom d'utilisateur est déjà pris
     */
    public AppUser register(AppUser user) {
        String norm = normalize(user.getUsername());
        if (userRepository.findByUsername(norm).isPresent()) {
            throw new IllegalArgumentException("Nom d'utilisateur déjà pris.");
        }
        user.setUsername(norm);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Valide les identifiants d'un utilisateur.
     * <p>
     * Vérifie que le nom d'utilisateur existe et que le mot de passe fourni
     * correspond au mot de passe enregistré (via BCrypt).
     * </p>
     *
     * @param username le nom d'utilisateur
     * @param rawPassword le mot de passe en clair fourni par l'utilisateur
     * @return {@code true} si les identifiants sont valides, sinon {@code false}
     */
    public boolean validateCredentials(String username, String rawPassword) {
        return findByUsername(username)
                .map(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(false);
    }

    /**
     * Recherche un utilisateur par son nom d'utilisateur (stocké en minuscules).
     *
     * @param username le nom d'utilisateur à rechercher
     * @return un {@link Optional} contenant l'utilisateur correspondant, ou vide si absent
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(normalize(username));
    }
}
