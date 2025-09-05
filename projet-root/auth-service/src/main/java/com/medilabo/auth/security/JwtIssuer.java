package com.medilabo.auth.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Composant responsable de l'émission de jetons JWT signés (HS256).
 *
 * <p>
 * Cette classe est utilisée dans le {@code auth-service} afin de générer un
 * token d’authentification pour un utilisateur authentifié. Le JWT contient
 * les informations suivantes :
 * </p>
 * <ul>
 *   <li>Le sujet (nom d’utilisateur) ;</li>
 *   <li>Les rôles (authorities Spring Security) ;</li>
 *   <li>La date d’émission ({@code iat}) ;</li>
 *   <li>La date d’expiration ({@code exp}), fixée par défaut à +1h.</li>
 * </ul>
 *
 * <p>
 * Le secret utilisé pour signer le JWT est injecté via la propriété
 * d’environnement {@code JWT_SECRET}.
 * </p>
 */
@Component
public class JwtIssuer {

    /**
     * Clé secrète utilisée pour signer les jetons JWT (algorithme HS256).
     */
    private final byte[] secret;

    /**
     * Constructeur.
     *
     * @param secret valeur de la propriété {@code JWT_SECRET}, injectée
     *               depuis les variables d’environnement ou les fichiers
     *               de configuration Spring.
     */
    public JwtIssuer(@Value("${JWT_SECRET}") String secret) {
        this.secret = secret.getBytes();
    }

    /**
     * Génère et signe un jeton JWT pour un utilisateur.
     *
     * @param username     le nom de l’utilisateur (sera stocké dans {@code sub})
     * @param authorities  la liste des rôles/authorities de l’utilisateur
     * @return une chaîne représentant le JWT signé
     * @throws RuntimeException si une erreur survient lors de la signature
     */
    public String issue(String username, List<? extends GrantedAuthority> authorities) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(3600); // +1 heure

            List<String> roles = authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("roles", roles)
                    .build();

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            JWSSigner signer = new MACSigner(secret);
            jwt.sign(signer);

            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot issue JWT", e);
        }
    }
}
