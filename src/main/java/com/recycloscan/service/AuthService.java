package com.recycloscan.service;

import com.recycloscan.config.JwtUtil;
import com.recycloscan.dto.AuthRequestDto;
import com.recycloscan.dto.AuthResponseDto;
import com.recycloscan.dto.RegisterRequestDto;
import com.recycloscan.entity.User;
import com.recycloscan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // ========================
    // Inscription
    // ========================
    public AuthResponseDto register(RegisterRequestDto dto) {

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Vérifier si le username existe déjà
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Ce username est déjà pris");
        }

        // Créer le nouvel utilisateur
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Hasher le mot de passe avant de le stocker — jamais en clair
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        user.setTotalPoints(0);

        // Sauvegarder en base
        userRepository.save(user);

        // Générer le token JWT
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getTotalPoints()
        );
    }

    // ========================
    // Connexion
    // ========================
    public AuthResponseDto login(AuthRequestDto dto) {

        // Spring Security vérifie email + mot de passe automatiquement
        // Lance une exception si les credentials sont incorrects
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        // Si on arrive ici, les credentials sont corrects
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Générer un nouveau token
        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getUsername(),
                user.getEmail(),
                user.getTotalPoints()
        );
    }
}