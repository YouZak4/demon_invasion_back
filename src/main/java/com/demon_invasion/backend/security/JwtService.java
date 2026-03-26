package com.demon_invasion.backend.security;

import com.demon_invasion.backend.model.CustomUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

//Génère et valide les tokens
@Service
public class JwtService {

    // Lit la clé depuis application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Génère la clé cryptographique à partir de la chaîne de caractères
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Génère un token pour un utilisateur
    public String generateToken(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userPrincipal)) {
            throw new IllegalArgumentException("Principal is not CustomUserDetails");
        }

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())                             // identifiant dans le token
                .claim("roles", roles)                                    // rôles dans le payload
                .issuedAt(new Date())                                           // date de création
                .expiration(new Date(System.currentTimeMillis() + expiration))  // date d'expiration
                .signWith(getSigningKey())
                .compact();
    }

    // Extrait l'identifiant depuis un token
    public String extractIdentifiant(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // Vérifie que le token est valide et correspond à l'utilisateur
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String identifiant = extractIdentifiant(token);
            return identifiant.equals(userDetails.getUsername()) && !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        Date expDate = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
        return expDate.before(new Date());
    }
}
