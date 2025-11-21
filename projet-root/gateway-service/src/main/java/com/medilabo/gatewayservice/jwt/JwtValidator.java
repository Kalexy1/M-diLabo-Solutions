package com.medilabo.gatewayservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

/**
 * Composant responsable de la validation et de l’analyse des jetons JWT.
 *
 * <p>Cette classe vérifie la signature du token à l’aide de la clé secrète
 * configurée dans les propriétés Spring, puis renvoie les {@link Claims}
 * contenus dans le jeton.</p>
 */
@Component
public class JwtValidator {

    @Value("${security.jwt.secret}")
    private String secret;

    /**
     * Valide un jeton JWT et en extrait les revendications.
     *
     * @param token le JWT signé à valider
     * @return les {@link Claims} du jeton JWT si la signature est correcte
     * @throws io.jsonwebtoken.JwtException si le token est invalide ou falsifié
     */
    public Claims validate(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
