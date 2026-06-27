package com.recycloscan.controller;

import com.recycloscan.dto.AuthRequestDto;
import com.recycloscan.dto.AuthResponseDto;
import com.recycloscan.dto.RegisterRequestDto;
import com.recycloscan.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ========================
    // POST /api/auth/register
    // Body : { username, email, password }
    // ========================
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(
            @Valid @RequestBody RegisterRequestDto dto) {
        // @Valid déclenche la validation des annotations (@NotBlank, @Email...)
        // Si invalide → Spring retourne 400 automatiquement
        AuthResponseDto response = authService.register(dto);
        return ResponseEntity.ok(response);
    }

    // ========================
    // POST /api/auth/login
    // Body : { email, password }
    // ========================
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody AuthRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}