package com.demon_invasion.backend.security;

import com.demon_invasion.backend.model.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;

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
    public String genererToken(String email, Set<Role> roles) {
        return Jwts.builder()
                .subject(email)                                                 // qui est l'utilisateur
                .claim("roles", roles)                                       // les rôles dans le token
                .issuedAt(new Date())                                           // quand il a été créé
                .expiration(new Date(System.currentTimeMillis() + expiration))  // quand il expire
                .signWith(getSigningKey())                                      // signature avec la clé secrète
                .compact();
    }

    // Extrait l'email depuis un token
    public String extraireEmail(String token) {
        return parserToken(token).getSubject();
    }

    // Extrait le rôle depuis un token
    public String extraireRole(String token) {
        return parserToken(token).get("roles", Set.class).toString();
    }

    // Vérifie si un token est valide et non expiré
    public boolean estValide(String token) {
        try {
            parserToken(token);
            return true;
        } catch (JwtException e) {
            return false;  // token falsifié, expiré ou malformé
        }
    }

    private Claims parserToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
