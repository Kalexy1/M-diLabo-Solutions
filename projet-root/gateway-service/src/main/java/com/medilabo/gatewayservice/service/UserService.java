package com.medilabo.gatewayservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medilabo.gatewayservice.model.AppUser;
import com.medilabo.gatewayservice.repository.UserRepository;

/**
 * Service gérant les opérations liées aux utilisateurs.
 *
 * <p>Ce service encapsule la logique métier pour :</p>
 * <ul>
 *   <li>rechercher un utilisateur par son nom d'utilisateur,</li>
 *   <li>enregistrer un nouvel utilisateur avec mot de passe chiffré,</li>
 *   <li>valider les identifiants fournis lors de la connexion.</li>
 * </ul>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Construit le service utilisateur.
     *
     * @param userRepository repository de gestion des utilisateurs
     * @param passwordEncoder encodeur utilisé pour hasher les mots de passe
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Recherche un utilisateur via son nom d'utilisateur.
     *
     * @param username nom d'utilisateur recherché
     * @return un {@link Optional} contenant l'utilisateur s'il existe
     */
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Enregistre un nouvel utilisateur après avoir chiffré son mot de passe.
     *
     * @param user utilisateur à enregistrer
     * @return l'entité persistée
     */
    public AppUser register(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Valide les identifiants fournis par un utilisateur lors de la connexion.
     *
     * @param username    nom d'utilisateur
     * @param rawPassword mot de passe en clair fourni par l'utilisateur
     * @return {@code true} si les identifiants sont valides, {@code false} sinon
     */
    public boolean validateCredentials(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .map(u -> passwordEncoder.matches(rawPassword, u.getPassword()))
                .orElse(false);
    }
}
