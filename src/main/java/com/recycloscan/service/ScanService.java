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
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScanService {

    private final GeminiService geminiService;
    private final WasteService wasteService;
    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final WasteItemRepository wasteItemRepository;

    // ========================
    // Scan via photo (base64)
    // Envoie l'image à Gemini qui retourne une analyse complète
    // ========================
    public ScanResponseDto scanByPhoto(String base64Image) {

        // 1. Envoyer l'image à Gemini — retourne maintenant un Map complet
        Map<String, Object> geminiData = geminiService.analyzeImage(base64Image);

        // 2. Extraire les infos depuis la réponse Gemini
        String geminiLabel = null;
        String explication = null;
        String materiau = null;
        String astuce = null;
        Boolean recyclable = null;

        if (geminiData != null) {
            geminiLabel = (String) geminiData.get("nom");
            explication = (String) geminiData.get("explication");
            materiau = (String) geminiData.get("materiau");
            astuce = (String) geminiData.get("astuce");
            recyclable = (Boolean) geminiData.get("recyclable");
        }

        // 3. Chercher le déchet correspondant dans la base
        WasteItem wasteItem = null;
        if (geminiLabel != null) {
            wasteItem = wasteService.findByGeminiLabel(geminiLabel);
        }

        // 4. Si non trouvé → déchet par défaut sauvegardé en base
        if (wasteItem == null) {
            wasteItem = getDefaultWasteItem();
        }

        // 5. Créditer les points et enregistrer le scan
        return processScan(wasteItem, geminiLabel, explication, materiau, astuce, recyclable);
    }

    // ========================
    // Scan manuel via texte
    // Pas d'appel Gemini — recherche directe en base
    // ========================
    public ScanResponseDto scanByText(String keyword) {

        // Chercher directement dans la base par texte
        List<WasteItem> results = wasteService.search(keyword);

        WasteItem wasteItem = results.isEmpty()
                ? getDefaultWasteItem()
                : results.get(0);

        // Pas de données Gemini pour le scan manuel — on passe null
        return processScan(wasteItem, keyword, null, null, null, null);
    }

    // ========================
    // Logique commune : créditer points + enregistrer dans l'historique
    // ========================
    private ScanResponseDto processScan(WasteItem wasteItem,
                                        String geminiLabel,
                                        String explication,
                                        String materiau,
                                        String astuce,
                                        Boolean recyclable) {

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

        // Construire la réponse avec tous les champs éducatifs
        ScanResponseDto dto = new ScanResponseDto();
        dto.setWasteName(wasteItem.getName());
        dto.setCategory(wasteItem.getCategory().name());
        dto.setBinColor(wasteItem.getBinColor().name());
        dto.setInstruction(wasteItem.getInstruction());
        dto.setGeminiLabel(geminiLabel);
        dto.setPointsEarned(pointsEarned);
        dto.setTotalPoints(user.getTotalPoints());
        dto.setMessage("Bien joué ! +" + pointsEarned + " points");

        // Champs éducatifs — peuvent être null si scan manuel
        dto.setMateriau(materiau);
        dto.setExplication(explication);
        dto.setAstuce(astuce);
        dto.setRecyclable(recyclable);

        return dto;
    }

    // ========================
    // Déchet par défaut sauvegardé en base
    // Utilisé quand Gemini ne reconnaît pas le déchet
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