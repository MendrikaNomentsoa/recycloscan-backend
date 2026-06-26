// UserRepository.java
package com.recycloscan.repository;

import com.recycloscan.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring génère : SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Spring génère : SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Top 10 triés par points décroissants → pour le leaderboard
    List<User> findTop10ByOrderByTotalPointsDesc();
}