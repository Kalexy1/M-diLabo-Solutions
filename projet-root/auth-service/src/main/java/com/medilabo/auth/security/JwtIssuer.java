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

@Component
public class JwtIssuer {

    private final byte[] secret;
    private final long ttlSeconds;
    private final String issuer;
    private final List<String> audience;
    private final boolean includeJti;
    private final boolean includeNbf;
    private final JWSSigner signer;

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

    /** Émission depuis authorities Spring (ROLE_X -> X dans claim 'roles'). */
    public String issue(String username, List<? extends GrantedAuthority> authorities) {
        String subject = normalize(username);
        List<String> roles = authorities == null ? List.of() :
                authorities.stream()
                        .map(GrantedAuthority::getAuthority)      // "ROLE_X"
                        .map(a -> a.startsWith("ROLE_") ? a.substring(5) : a)
                        .collect(Collectors.toList());
        return internalIssue(subject, roles);
    }

    /** Émission pratique depuis un AppUser. */
    public String issue(AppUser user) {
        return internalIssue(normalize(user.getUsername()), List.of(user.getRole().name()));
    }

    // Impl
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

    private static String normalize(String username) {
        return username == null ? null : username.trim().toLowerCase();
    }
}
