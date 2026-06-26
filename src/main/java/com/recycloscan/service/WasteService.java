package com.recycloscan.service;

import com.recycloscan.entity.WasteItem;
import com.recycloscan.repository.WasteItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WasteService {

    private final WasteItemRepository wasteItemRepository;

    // ========================
    // Retourne tous les déchets
    // ========================
    public List<WasteItem> getAll() {
        return wasteItemRepository.findAll();
    }

    // ========================
    // Recherche par nom (barre de recherche manuelle)
    // ========================
    public List<WasteItem> search(String keyword) {
        return wasteItemRepository.searchByNameOrKeyword(keyword);
    }

    // ========================
    // Trouve le meilleur match pour un label Gemini
    // Essaie chaque mot du label séparément
    // ========================
    public WasteItem findByGeminiLabel(String geminiLabel) {
        if (geminiLabel == null || geminiLabel.isBlank()) return null;

        // Essai 1 : recherche avec le label complet
        List<WasteItem> results = wasteItemRepository.searchByNameOrKeyword(geminiLabel);
        if (!results.isEmpty()) return results.get(0);

        // Essai 2 : recherche mot par mot
        // ex: "bouteille plastique" → cherche "bouteille" puis "plastique"
        String[] words = geminiLabel.split(" ");
        for (String word : words) {
            if (word.length() > 3) { // ignorer les mots trop courts (le, la, un...)
                results = wasteItemRepository.searchByNameOrKeyword(word);
                if (!results.isEmpty()) return results.get(0);
            }
        }

        return null; // aucun match trouvé
    }

    // ========================
    // Ajouter un déchet (Admin)
    // ========================
    public WasteItem create(WasteItem wasteItem) {
        return wasteItemRepository.save(wasteItem);
    }

    // ========================
    // Modifier un déchet (Admin)
    // ========================
    public WasteItem update(Long id, WasteItem updated) {
        WasteItem existing = wasteItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Déchet non trouvé"));

        existing.setName(updated.getName());
        existing.setGeminiKeywords(updated.getGeminiKeywords());
        existing.setCategory(updated.getCategory());
        existing.setBinColor(updated.getBinColor());
        existing.setInstruction(updated.getInstruction());
        existing.setPointsValue(updated.getPointsValue());

        return wasteItemRepository.save(existing);
    }

    // ========================
    // Supprimer un déchet (Admin)
    // ========================
    public void delete(Long id) {
        wasteItemRepository.deleteById(id);
    }
}