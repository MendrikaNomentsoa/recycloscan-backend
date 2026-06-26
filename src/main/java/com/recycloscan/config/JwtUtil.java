package com.recycloscan.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Spring gère cette classe
public class JwtUtil {

    // Récupère la valeur depuis application.properties
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private Long expiration;

    // ========================
    // Génère la clé de signature à partir du secret
    // ========================
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ========================
    // Génère un token JWT pour un utilisateur
    // ========================
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)                          // qui est l'utilisateur
                .issuedAt(new Date())                    // quand le token a été créé
                .expiration(new Date(System.currentTimeMillis() + expiration)) // quand il expire
                .signWith(getSigningKey())               // signature avec la clé secrète
                .compact();                              // construit le token en String
    }

    // ========================
    // Extrait l'email depuis un token
    // ========================
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject(); // retourne l'email qu'on a mis dans subject
    }

    // ========================
    // Vérifie si le token est valide et non expiré
    // ========================
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true; // token valide
        } catch (JwtException | IllegalArgumentException e) {
            return false; // token invalide ou expiré
        }
    }
}