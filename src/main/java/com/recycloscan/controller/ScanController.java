package com.recycloscan.controller;

import com.recycloscan.dto.ScanResponseDto;
import com.recycloscan.entity.ScanHistory;
import com.recycloscan.service.ScanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/scan")
@RequiredArgsConstructor
public class ScanController {

    private final ScanService scanService;

    // ========================
    // POST /api/scan/photo
    // Body : { "image": "base64string..." }
    // Scan via caméra — authentifié
    // ========================
    @PostMapping("/photo")
    public ResponseEntity<ScanResponseDto> scanByPhoto(
            @RequestBody Map<String, String> body) {

        // Extraire l'image base64 du body
        String base64Image = body.get("image");

        if (base64Image == null || base64Image.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ScanResponseDto response = scanService.scanByPhoto(base64Image);
        return ResponseEntity.ok(response);
    }

    // ========================
    // POST /api/scan/manual
    // Body : { "keyword": "bouteille" }
    // Scan manuel par texte — authentifié
    // ========================
    @PostMapping("/manual")
    public ResponseEntity<ScanResponseDto> scanByText(
            @RequestBody Map<String, String> body) {

        String keyword = body.get("keyword");

        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ScanResponseDto response = scanService.scanByText(keyword);
        return ResponseEntity.ok(response);
    }

    // ========================
    // GET /api/scan/history
    // Historique de l'utilisateur connecté — authentifié
    // ========================
    @GetMapping("/history")
    public ResponseEntity<List<ScanHistory>> getHistory() {
        return ResponseEntity.ok(scanService.getHistory());
    }
}