package com.recycloscan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${app.gemini.api-key}")
    private String apiKey;

    @Value("${app.gemini.url}")
    private String apiUrl;

    // RestTemplate pour faire des appels HTTP vers Gemini
    private final RestTemplate restTemplate;

    // ========================
    // Analyse une image et retourne le nom de l'objet détecté
    // ========================
    public String analyzeImage(String base64Image) {
        try {
            // Construire l'URL avec la clé API
            String url = apiUrl + "?key=" + apiKey;

            // Construire le body de la requête Gemini
            Map<String, Object> requestBody = buildRequestBody(base64Image);

            // Configurer les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Appeler l'API Gemini
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Extraire le texte de la réponse
            return extractTextFromResponse(response.getBody());

        } catch (Exception e) {
            // Si Gemini échoue, on retourne null
            // Le ScanService gérera ce cas avec une recherche manuelle
            return null;
        }
    }

    // ========================
    // Construit le body JSON pour Gemini
    // ========================
    private Map<String, Object> buildRequestBody(String base64Image) {

        // Partie image
        Map<String, Object> imagePart = new HashMap<>();
        Map<String, String> inlineData = new HashMap<>();
        inlineData.put("mime_type", "image/jpeg");
        inlineData.put("data", base64Image);
        imagePart.put("inline_data", inlineData);

        // Partie texte — instruction à Gemini
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text",
                "Identifie l'objet principal dans cette image en 2-3 mots maximum en français. " +
                        "Réponds UNIQUEMENT avec le nom de l'objet, rien d'autre. " +
                        "Exemples de réponses : 'bouteille plastique', 'journal papier', 'épluchures', 'pile électrique'"
        );

        // Assembler les parties
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(imagePart, textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        return body;
    }

    // ========================
    // Extrait le texte de la réponse Gemini
    // ========================
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            Map firstCandidate = candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return parts.get(0).get("text").toString().trim().toLowerCase();
        } catch (Exception e) {
            return null;
        }
    }
}