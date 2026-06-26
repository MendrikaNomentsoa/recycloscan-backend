package com.recycloscan.config;

import com.recycloscan.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // Lombok génère le constructeur avec les champs final
public class JwtFilter extends OncePerRequestFilter {
    // OncePerRequestFilter garantit que ce filtre s'exécute UNE seule fois par requête

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. Vérifier que le header existe et commence par "Bearer "
        // Format attendu : "Bearer eyJhbGciOiJIUzI1NiJ9..."
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pas de token → on laisse passer (Spring Security bloquera si la route est protégée)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (enlever "Bearer " qui fait 7 caractères)
        String token = authHeader.substring(7);

        // 4. Valider le token et authentifier l'utilisateur
        if (jwtUtil.isTokenValid(token)) {

            // 5. Extraire l'email du token
            String email = jwtUtil.extractEmail(token);

            // 6. Charger l'utilisateur depuis la base
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Créer l'objet d'authentification Spring Security
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                        // pas de credentials (déjà validé par JWT)
                            userDetails.getAuthorities() // rôles de l'utilisateur
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 8. Dire à Spring Security que cet utilisateur est authentifié
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 9. Continuer vers le controller
        filterChain.doFilter(request, response);
    }
}