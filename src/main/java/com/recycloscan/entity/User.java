package com.recycloscan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "users") // "user" est un mot réservé en SQL, on utilise "users"
@Data               // Lombok : génère getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok : génère le constructeur sans arguments (requis par JPA)
@AllArgsConstructor // Lombok : génère le constructeur avec tous les arguments
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto-incrément PostgreSQL
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // sera hashé avec bcrypt

    @Column(nullable = false)
    private Integer totalPoints = 0; // points accumulés par l'utilisateur

    @Enumerated(EnumType.STRING) // stocke "USER" ou "ADMIN" en texte dans la DB
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Exécuté automatiquement avant chaque insertion en base
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Enum des rôles possibles
    public enum Role {
        USER, ADMIN
    }
}