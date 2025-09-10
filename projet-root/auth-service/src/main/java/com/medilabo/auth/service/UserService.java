package com.medilabo.auth.service;

import com.medilabo.auth.model.AppUser;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Application: com.medilabo.auth.service
 * <p>
 * Classe <strong>UserService</strong>.
 * <br/>
 * Rôle: Gère la logique métier liée aux utilisateurs.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur.
     *
     * @param userRepository référentiel utilisateur.
     * @param passwordEncoder encodeur de mot de passe.
     */
    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Vérifie si un utilisateur existe.
     *
     * @param username nom d'utilisateur.
     * @return true si l'utilisateur existe, false sinon.
     */
    public boolean userExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    /**
     * Sauvegarde un nouvel utilisateur avec un mot de passe encodé.
     *
     * @param user utilisateur à sauvegarder.
     * @return utilisateur sauvegardé.
     */
    public AppUser saveUser(AppUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Valide les identifiants d'un utilisateur.
     *
     * @param username     nom d'utilisateur.
     * @param rawPassword  mot de passe fourni.
     * @return true si les identifiants sont corrects, false sinon.
     */
    public boolean validateCredentials(String username, String rawPassword) {
        AppUser user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
