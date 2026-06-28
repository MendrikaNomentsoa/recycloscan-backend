package com.recycloscan.controller;

import com.recycloscan.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    // ========================
    // GET /api/stats/me
    // Toutes les stats de l'utilisateur connecté
    // ========================
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMyStats() {
        return ResponseEntity.ok(statsService.getMyStats());
    }
}