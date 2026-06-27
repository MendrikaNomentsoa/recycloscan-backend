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

            // Log pour vérifier l'URL et la taille de l'image
            System.out.println("=== GEMINI URL : " + url);
            System.out.println("=== IMAGE SIZE : " + base64Image.length());

            // Construire le body de la requête Gemini
            Map<String, Object> requestBody = buildRequestBody(base64Image);

            // Configurer les headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // Appeler l'API Gemini
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            // Log pour voir la réponse brute de Gemini
            System.out.println("=== GEMINI RESPONSE : " + response.getBody());

            // Extraire le texte de la réponse
            String label = extractTextFromResponse(response.getBody());

            // Log pour voir le label extrait
            System.out.println("=== GEMINI LABEL : " + label);

            return label;

        } catch (Exception e) {
            // Log pour voir l'erreur exacte si Gemini échoue
            System.out.println("=== GEMINI ERROR : " + e.getMessage());
            e.printStackTrace();
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

            // Log pour voir ce qu'on extrait
            String text = parts.get(0).get("text").toString().trim().toLowerCase();
            System.out.println("=== EXTRACTED TEXT : " + text);

            return text;
        } catch (Exception e) {
            System.out.println("=== EXTRACT ERROR : " + e.getMessage());
            return null;
        }
    }
}