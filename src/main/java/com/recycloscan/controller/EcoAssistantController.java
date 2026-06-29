package com.recycloscan.controller;

import com.recycloscan.service.EcoAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/eco")
@RequiredArgsConstructor
public class EcoAssistantController {

    private final EcoAssistantService ecoAssistantService;

    // ========================
    // GET /api/eco/advice
    // Conseils personnalisés selon les habitudes
    // ========================
    @GetMapping("/advice")
    public ResponseEntity<Map<String, Object>> getAdvice() {
        return ResponseEntity.ok(ecoAssistantService.getPersonalizedAdvice());
    }

    // ========================
    // POST /api/eco/chat
    // Question/réponse avec Gemini sur le recyclage
    // Body : { "question": "..." }
    // ========================
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> body) {
        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(ecoAssistantService.chat(question));
    }
}