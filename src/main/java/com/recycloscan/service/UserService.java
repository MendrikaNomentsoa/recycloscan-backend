package com.recycloscan.service;

import com.recycloscan.entity.User;
import com.recycloscan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ========================
    // Profil de l'utilisateur connecté
    // ========================
    public User getMe() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // ========================
    // Top 10 pour le leaderboard
    // ========================
    public List<User> getLeaderboard() {
        return userRepository.findTop10ByOrderByTotalPointsDesc();
    }

    // ========================
    // Tous les utilisateurs (Admin)
    // ========================
    public List<User> getAll() {
        return userRepository.findAll();
    }
}