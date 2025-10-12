package com.medilabo.auth.security;

import com.medilabo.auth.model.AppUser;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Composant responsable de l'émission de JSON Web Tokens (JWT) signés (HS256).
 * <p>
 * Les paramètres (secret, TTL, issuer, audience, etc.) sont injectés via la
 * configuration Spring et utilisés pour construire et signer les tokens.
 * </p>
 */
@Component
public class JwtIssuer {

    /**
     * Clé secrète (octets) utilisée pour la signature HMAC.
     */
    private final byte[] secret;

    /**
     * Durée de vie du token, en secondes.
     */
    private final long ttlSeconds;

    /**
     * Émetteur (claim {@code iss}) du JWT.
     */
    private final String issuer;

    /**
     * Public visé (claim {@code aud}) du JWT.
     */
    private final List<String> audience;

    /**
     * Indique s'il faut inclure un identifiant unique (claim {@code jti}).
     */
    private final boolean includeJti;

    /**
     * Indique s'il faut inclure une date de non-validité avant (claim {@code nbf}).
     */
    private final boolean includeNbf;

    /**
     * Signer JOSE pour HS256.
     */
    private final JWSSigner signer;

    /**
     * Construit le composant d'émission de JWT.
     *
     * @param secret         la clé secrète (doit faire au moins 32 octets)
     * @param ttlSeconds     durée de vie du token en secondes
     * @param issuer         valeur du claim {@code iss}
     * @param audienceCsv    liste des audiences séparées par des virgules
     * @param includeJti     {@code true} pour inclure le claim {@code jti}
     * @param includeNbf     {@code true} pour inclure le claim {@code nbf}
     * @throws IllegalArgumentException si le secret est trop court
     * @throws IllegalStateException    si l'initialisation du signer échoue
     */
    public JwtIssuer(
            @Value("${JWT_SECRET}") String secret,
            @Value("${JWT_TTL_SECONDS:7200}") long ttlSeconds,
            @Value("${JWT_ISSUER:medilabo-auth}") String issuer,
            @Value("${JWT_AUDIENCE:gateway,patient,note,risk}") String audienceCsv,
            @Value("${JWT_INCLUDE_JTI:true}") boolean includeJti,
            @Value("${JWT_INCLUDE_NBF:true}") boolean includeNbf
    ) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        if (key.length < 32) throw new IllegalArgumentException("JWT_SECRET must be >= 32 bytes.");
        this.secret = key;
        this.ttlSeconds = ttlSeconds;
        this.issuer = issuer;
        this.audience = parseAudience(audienceCsv);
        this.includeJti = includeJti;
        this.includeNbf = includeNbf;
        try {
            this.signer = new MACSigner(this.secret);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot init signer", e);
        }
    }

    /**
     * Émet un JWT à partir d'un nom d'utilisateur et d'autorisations Spring.
     * <p>
     * Les autorités de type {@code ROLE_X} sont converties en {@code X} dans le claim {@code roles}.
     * Le sujet est normalisé en minuscules.
     * </p>
     *
     * @param username    le nom d'utilisateur
     * @param authorities la liste des autorités Spring Security
     * @return le token JWT signé et sérialisé
     * @throws RuntimeException si l'émission du token échoue
     */
    public String issue(String username, List<? extends GrantedAuthority> authorities) {
        String subject = normalize(username);
        List<String> roles = authorities == null ? List.of() :
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                        .collect(Collectors.toList());
        return internalIssue(subject, roles);
    }

    /**
     * Émet un JWT à partir d'un {@link AppUser}.
     * <p>
     * Le sujet correspond au nom d'utilisateur normalisé et le claim {@code roles}
     * contient le nom du rôle de l'utilisateur.
     * </p>
     *
     * @param user l'utilisateur source
     * @return le token JWT signé et sérialisé
     * @throws RuntimeException si l'émission du token échoue
     */
    public String issue(AppUser user) {
        return internalIssue(normalize(user.getUsername()), List.of(user.getRole().name()));
    }

    /**
     * Construit et signe le JWT avec les claims standard et personnalisés.
     *
     * @param subjectLower sujet (nom d'utilisateur) déjà normalisé en minuscules
     * @param roles        rôles applicatifs à inclure dans le claim {@code roles}
     * @return le token JWT signé et sérialisé
     * @throws RuntimeException si l'émission du token échoue
     */
    private String internalIssue(String subjectLower, List<String> roles) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plusSeconds(ttlSeconds);

            JWTClaimsSet.Builder b = new JWTClaimsSet.Builder()
                    .issuer(issuer)
                    .subject(subjectLower)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("roles", roles);

            if (!audience.isEmpty()) b.audience(audience);
            if (includeNbf) b.notBeforeTime(Date.from(now));
            if (includeJti) b.jwtID(UUID.randomUUID().toString());

            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), b.build());
            jwt.sign(signer);
            return jwt.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Cannot issue JWT", e);
        }
    }

    /**
     * Transforme une chaîne CSV d'audiences en liste immuable.
     *
     * @param csv la chaîne CSV (peut être nulle ou vide)
     * @return une liste immuable des audiences, ou une liste vide si aucune n'est fournie
     */
    private static List<String> parseAudience(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        String[] parts = csv.split(",");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String s = p.trim();
            if (!s.isEmpty()) out.add(s);
        }
        return Collections.unmodifiableList(out);
    }

    /**
     * Normalise un nom d'utilisateur (trim + minuscules).
     *
     * @param username le nom d'utilisateur d'origine (peut être {@code null})
     * @return le nom d'utilisateur normalisé ou {@code null} si l'entrée est nulle
     */
    private static String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }
}
