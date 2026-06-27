package com.recycloscan.controller;

import com.recycloscan.entity.User;
import com.recycloscan.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ========================
    // GET /api/users/me
    // Profil de l'utilisateur connecté — authentifié
    // ========================
    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return ResponseEntity.ok(userService.getMe());
    }

    // ========================
    // GET /api/users/leaderboard
    // Top 10 par points — public
    // ========================
    @GetMapping("/leaderboard")
    public ResponseEntity<List<User>> getLeaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }

    // ========================
    // GET /api/users
    // Tous les utilisateurs — ADMIN seulement
    // ========================
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }
}