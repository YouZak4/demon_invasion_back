package com.demon_invasion.backend.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

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
    public String genererToken(UserDetails userDetails) {
        String roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userDetails.getUsername())                             // identifiant dans le token
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
