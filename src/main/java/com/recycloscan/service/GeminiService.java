package com.recycloscan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    // ObjectMapper pour parser le JSON retourné par Gemini
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========================
    // Analyse une image et retourne les infos complètes du déchet
    // Retourne un Map avec : nom, materiau, recyclable, explication, consigne, astuce
    // ========================
    public Map<String, Object> analyzeImage(String base64Image) {
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

            // Extraire le texte brut de la réponse
            String rawText = extractTextFromResponse(response.getBody());

            // Log pour voir le texte extrait
            System.out.println("=== GEMINI RAW TEXT : " + rawText);

            // Parser le JSON retourné par Gemini
            return parseGeminiJson(rawText);

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
    // Demande une analyse complète avec explication éducative
    // ========================
    private Map<String, Object> buildRequestBody(String base64Image) {

        // Partie image
        Map<String, Object> imagePart = new HashMap<>();
        Map<String, String> inlineData = new HashMap<>();
        inlineData.put("mime_type", "image/jpeg");
        inlineData.put("data", base64Image);
        imagePart.put("inline_data", inlineData);

        // Partie texte — demander une analyse complète en JSON
        // Gemini doit retourner un JSON structuré avec toutes les infos éducatives
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text",
                "Analyse ce déchet et réponds UNIQUEMENT en JSON valide sans markdown, " +
                        "sans balises ```json, exactement dans ce format : " +
                        "{" +
                        "\"nom\": \"nom du déchet en français\"," +
                        "\"materiau\": \"matériau principal ex: PET, verre, papier, aluminium\"," +
                        "\"recyclable\": true ou false," +
                        "\"explication\": \"une phrase courte expliquant pourquoi ce matériau est recyclable ou non\"," +
                        "\"consigne\": \"consigne de tri précise et pratique ex: vider écraser retirer le bouchon\"," +
                        "\"astuce\": \"une astuce locale ou environnementale courte et utile\"" +
                        "}"
        );

        // Assembler les parties image + texte
        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(imagePart, textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        return body;
    }

    // ========================
    // Extrait le texte brut de la réponse Gemini
    // La réponse contient un JSON imbriqué — on extrait juste le texte
    // ========================
    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map responseBody) {
        try {
            List<Map> candidates = (List<Map>) responseBody.get("candidates");
            Map firstCandidate = candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List<Map> parts = (List<Map>) content.get("parts");

            String text = parts.get(0).get("text").toString().trim();
            System.out.println("=== EXTRACTED TEXT : " + text);

            return text;
        } catch (Exception e) {
            System.out.println("=== EXTRACT ERROR : " + e.getMessage());
            return null;
        }
    }

    // ========================
    // Parse le JSON retourné par Gemini en Map Java
    // Gemini peut ajouter des backticks ou du markdown — on nettoie avant
    // ========================
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseGeminiJson(String rawText) {
        try {
            if (rawText == null) return null;

            // Nettoyer le texte — Gemini ajoute parfois des balises markdown
            String cleaned = rawText
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            // Parser le JSON nettoyé
            return objectMapper.readValue(cleaned, Map.class);

        } catch (Exception e) {
            System.out.println("=== PARSE ERROR : " + e.getMessage());

            // Si le parsing échoue → retourner un objet minimal
            // pour ne pas bloquer le scan
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("nom", rawText); // utiliser le texte brut comme nom
            fallback.put("materiau", "inconnu");
            fallback.put("recyclable", false);
            fallback.put("explication", "Analyse non disponible");
            fallback.put("consigne", "Déposer dans la poubelle appropriée");
            fallback.put("astuce", "Trier correctement ses déchets aide l'environnement");
            return fallback;
        }
    }
}