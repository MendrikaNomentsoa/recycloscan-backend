package com.recycloscan.service;

import com.recycloscan.dto.ScanResponseDto;
import com.recycloscan.entity.ScanHistory;
import com.recycloscan.entity.User;
import com.recycloscan.entity.WasteItem;
import com.recycloscan.repository.ScanHistoryRepository;
import com.recycloscan.repository.UserRepository;
import com.recycloscan.repository.WasteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final GeminiService geminiService;
    private final WasteService wasteService;
    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final WasteItemRepository wasteItemRepository; // ← ajouté

    // ========================
    // Scan via photo (base64)
    // ========================
    public ScanResponseDto scanByPhoto(String base64Image) {

        // 1. Envoyer l'image à Gemini pour identification
        String geminiLabel = geminiService.analyzeImage(base64Image);

        // 2. Chercher le déchet correspondant dans la base
        WasteItem wasteItem = null;
        if (geminiLabel != null) {
            wasteItem = wasteService.findByGeminiLabel(geminiLabel);
        }

        // 3. Si Gemini n'a pas trouvé → déchet inconnu sauvegardé en base
        if (wasteItem == null) {
            wasteItem = getDefaultWasteItem();
        }

        // 4. Créditer les points et enregistrer le scan
        return processScan(wasteItem, geminiLabel);
    }

    // ========================
    // Scan manuel via texte
    // ========================
    public ScanResponseDto scanByText(String keyword) {

        // Chercher directement dans la base par texte
        List<WasteItem> results = wasteService.search(keyword);

        WasteItem wasteItem = results.isEmpty()
                ? getDefaultWasteItem()
                : results.get(0);

        return processScan(wasteItem, keyword);
    }

    // ========================
    // Logique commune : créditer points + enregistrer
    // ========================
    private ScanResponseDto processScan(WasteItem wasteItem, String geminiLabel) {

        // Récupérer l'utilisateur connecté depuis le token JWT
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Créditer les points
        int pointsEarned = wasteItem.getPointsValue();
        user.setTotalPoints(user.getTotalPoints() + pointsEarned);
        userRepository.save(user);

        // Enregistrer dans l'historique
        ScanHistory history = new ScanHistory();
        history.setUser(user);
        history.setWasteItem(wasteItem);
        history.setGeminiLabel(geminiLabel);
        history.setPointsEarned(pointsEarned);
        scanHistoryRepository.save(history);

        // Construire la réponse
        return new ScanResponseDto(
                wasteItem.getName(),
                wasteItem.getCategory().name(),
                wasteItem.getBinColor().name(),
                wasteItem.getInstruction(),
                geminiLabel,
                pointsEarned,
                user.getTotalPoints(),
                "Bien joué ! +" + pointsEarned + " points"
        );
    }

    // ========================
    // Déchet par défaut sauvegardé en base
    // ========================
    private WasteItem getDefaultWasteItem() {

        // Vérifier s'il existe déjà en base pour éviter les doublons
        List<WasteItem> existing = wasteItemRepository
                .searchByNameOrKeyword("non identifié");

        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        // Créer et sauvegarder en base
        WasteItem unknown = new WasteItem();
        unknown.setName("Déchet non identifié");
        unknown.setCategory(WasteItem.Category.AUTRE);
        unknown.setBinColor(WasteItem.BinColor.GRISE);
        unknown.setGeminiKeywords("inconnu,non identifié,autre");
        unknown.setInstruction(
                "Déposez ce déchet dans la poubelle grise (ordures ménagères)"
        );
        unknown.setPointsValue(5);

        return wasteItemRepository.save(unknown);
    }

    // ========================
    // Historique de l'utilisateur connecté
    // ========================
    public List<ScanHistory> getHistory() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return scanHistoryRepository.findByUserIdOrderByScannedAtDesc(user.getId());
    }
}