package com.recycloscan.service;

import com.recycloscan.entity.ScanHistory;
import com.recycloscan.entity.User;
import com.recycloscan.entity.WasteItem;
import com.recycloscan.repository.ScanHistoryRepository;
import com.recycloscan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EcoAssistantService {

    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;
    private final RestTemplate restTemplate;

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.url}")
    private String apiUrl;

    // ========================
    // Récupère l'utilisateur connecté
    // ========================
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // ========================
    // Conseils personnalisés + défis + impact
    // Analyse les habitudes de l'utilisateur et génère des conseils
    // ========================
    public Map<String, Object> getPersonalizedAdvice() {
        User user = getCurrentUser();
        List<ScanHistory> history = scanHistoryRepository
                .findByUserIdOrderByScannedAtDesc(user.getId());

        Map<String, Object> result = new HashMap<>();

        // ========================
        // Analyser les habitudes
        // ========================

        // Catégorie la plus scannée
        Map<String, Long> categoryCount = history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getWasteItem().getCategory().name(),
                        Collectors.counting()
                ));

        String topCategory = categoryCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Catégorie la moins scannée
        String weakCategory = Arrays.asList(
                        "PLASTIQUE", "VERRE", "PAPIER", "ORGANIQUE", "DANGEREUX"
                ).stream()
                .filter(c -> !categoryCount.containsKey(c))
                .findFirst()
                .orElse(null);

        // Scans cette semaine
        long scansThisWeek = history.stream()
                .filter(h -> h.getScannedAt().isAfter(
                        LocalDateTime.now().with(DayOfWeek.MONDAY)
                                .withHour(0).withMinute(0)))
                .count();

        // ========================
        // Générer les conseils personnalisés
        // ========================
        List<Map<String, String>> conseils = new ArrayList<>();

        // Conseil basé sur la catégorie dominante
        if ("PLASTIQUE".equals(topCategory)) {
            addConseil(conseils, "🧴", "Réduire le plastique",
                    "Tu scannes beaucoup de plastiques. Essaie d'utiliser une gourde réutilisable pour réduire tes déchets plastiques à la source.",
                    "HIGH");
        } else if ("DANGEREUX".equals(topCategory)) {
            addConseil(conseils, "⚠️", "Bravo pour les déchets dangereux",
                    "Tu identifies bien les déchets dangereux. Pense à toujours les déposer dans les points de collecte spéciaux.",
                    "LOW");
        } else if (topCategory != null) {
            addConseil(conseils, "♻️", "Continue comme ça",
                    "Tu recycles bien les " + topCategory.toLowerCase() + ". Diversifie tes scans pour couvrir plus de catégories.",
                    "MEDIUM");
        }

        // Conseil si peu actif cette semaine
        if (scansThisWeek < 3) {
            addConseil(conseils, "📅", "Sois plus régulier",
                    "Tu n'as scanné que " + scansThisWeek + " déchets cette semaine. Vise au moins 1 scan par jour pour créer une habitude.",
                    "HIGH");
        }

        // Conseil catégorie manquante
        if ("ORGANIQUE".equals(weakCategory)) {
            addConseil(conseils, "🌿", "Pense au compostage",
                    "Tu n'as jamais scanné de déchets organiques. Les épluchures et restes de repas peuvent être compostés pour enrichir le sol.",
                    "MEDIUM");
        } else if ("VERRE".equals(weakCategory)) {
            addConseil(conseils, "🍶", "N'oublie pas le verre",
                    "Le verre est recyclable à l'infini ! Pense à déposer tes bouteilles dans les conteneurs à verre.",
                    "MEDIUM");
        }

        // Conseil général si débutant
        if (history.size() < 5) {
            addConseil(conseils, "🎓", "Apprends les bases",
                    "Commence par scanner les déchets du quotidien : bouteilles, cartons, épluchures. Chaque scan t'apprend quelque chose.",
                    "HIGH");
        }

        result.put("conseils", conseils);
        result.put("topCategory", topCategory);
        result.put("weakCategory", weakCategory);
        result.put("scansThisWeek", scansThisWeek);
        result.put("totalScans", history.size());

        // ========================
        // Impact environnemental résumé
        // ========================
        double kgRecycles = history.size() * 0.15;
        double co2Economise = kgRecycles * 2.5;
        result.put("kgRecycles", Math.round(kgRecycles * 10.0) / 10.0);
        result.put("co2Economise", Math.round(co2Economise * 10.0) / 10.0);
        result.put("bouteillesRecyclees", (int)(kgRecycles / 0.025));

        // ========================
        // Défis personnalisés selon les habitudes
        // ========================
        List<Map<String, Object>> defis = new ArrayList<>();

        // Défi basé sur la catégorie faible
        if (weakCategory != null) {
            addDefi(defis,
                    "Explorer " + weakCategory.toLowerCase(),
                    "Scanner 1 déchet de type " + weakCategory.toLowerCase(),
                    "🎯", 0, 1, 30);
        }

        // Défi activité
        addDefi(defis,
                "Semaine active",
                "Scanner 5 déchets cette semaine",
                "📅", (int) scansThisWeek, 5, 50);

        // Défi diversité
        long categoriesCount = history.stream()
                .map(h -> h.getWasteItem().getCategory())
                .distinct().count();
        addDefi(defis,
                "Recycleur polyvalent",
                "Scanner toutes les catégories",
                "🌈", (int) categoriesCount, 5, 100);

        result.put("defis", defis);

        return result;
    }

    // ========================
    // Chat avec Gemini sur le recyclage
    // Gemini répond uniquement aux questions liées au recyclage
    // ========================
    @SuppressWarnings("unchecked")
    public Map<String, String> chat(String question) {
        try {
            String url = apiUrl + "?key=" + apiKey;

            // Construire le prompt avec contexte africain
            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text",
                    "Tu es EcoBot, un assistant spécialisé dans le recyclage et " +
                            "la gestion des déchets en Afrique. " +
                            "Tu réponds UNIQUEMENT aux questions sur le recyclage, " +
                            "le tri des déchets, l'environnement et l'écologie. " +
                            "Si la question ne concerne pas ces sujets, réponds : " +
                            "'Je suis spécialisé uniquement dans le recyclage et l'environnement.' " +
                            "Réponds de manière courte (3-4 phrases max), " +
                            "pratique et adaptée au contexte africain. " +
                            "Question : " + question
            );

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(textPart));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url, entity, Map.class);

            // Extraire la réponse
            List<Map> candidates = (List<Map>) response.getBody().get("candidates");
            Map firstCandidate = candidates.get(0);
            Map responseContent = (Map) firstCandidate.get("content");
            List<Map> parts = (List<Map>) responseContent.get("parts");
            String answer = parts.get(0).get("text").toString().trim();

            return Map.of("answer", answer);

        } catch (Exception e) {
            System.out.println("=== ECO CHAT ERROR : " + e.getMessage());
            return Map.of("answer",
                    "Désolé, je ne peux pas répondre pour le moment. Réessaie.");
        }
    }

    // ========================
    // Helpers
    // ========================
    private void addConseil(List<Map<String, String>> conseils,
                            String emoji, String titre,
                            String description, String priorite) {
        Map<String, String> conseil = new HashMap<>();
        conseil.put("emoji", emoji);
        conseil.put("titre", titre);
        conseil.put("description", description);
        conseil.put("priorite", priorite); // HIGH, MEDIUM, LOW
        conseils.add(conseil);;
    }

    private void addDefi(List<Map<String, Object>> defis,
                         String titre, String description,
                         String emoji, int current,
                         int target, int bonusPoints) {
        Map<String, Object> defi = new HashMap<>();
        defi.put("titre", titre);
        defi.put("description", description);
        defi.put("emoji", emoji);
        defi.put("current", current);
        defi.put("target", target);
        defi.put("bonusPoints", bonusPoints);
        defi.put("completed", current >= target);
        defi.put("progress", Math.min((int)((current * 100.0) / target), 100));
        defis.add(defi);
    }
}