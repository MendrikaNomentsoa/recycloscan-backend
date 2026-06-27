package com.recycloscan.config;

import com.recycloscan.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF car on utilise JWT (pas de cookies)
                .csrf(AbstractHttpConfigurer::disable)

                // Configurer CORS pour autoriser React (localhost:5173)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Définir les routes publiques et protégées
                .authorizeHttpRequests(auth -> auth
                        // Routes publiques — pas besoin de token
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/waste").permitAll()
                        .requestMatchers("/api/waste/search").permitAll()
                        .requestMatchers("/api/users/leaderboard").permitAll()
                        // Toutes les autres routes nécessitent un token valide
                        .anyRequest().authenticated()
                )

                // Pas de session côté serveur — chaque requête porte son token
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authenticationProvider(authenticationProvider())

                // Ajouter notre filtre JWT AVANT le filtre d'auth par défaut de Spring
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ========================
    // Autoriser React à appeler l'API
    // ========================
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:5173", // Vite dev server
                "http://localhost:3000"  // Create React App
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ========================
    // Encode les mots de passe avec BCrypt
    // ========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ========================
    // Configure comment Spring verifie les credentials
    // ========================
    @Bean
    public AuthenticationProvider authenticationProvider() {
        //  UserDetailsService dans le constructeur
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        // PasswordEncoder séparément via setter
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // ========================
    // Nécessaire pour l'AuthService (login)
    // ========================
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}