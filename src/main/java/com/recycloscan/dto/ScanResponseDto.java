package com.recycloscan.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScanResponseDto {

    // ========================
    // Informations de base du déchet
    // ========================
    private String wasteName;       // ex: "Bouteille en plastique"
    private String category;        // ex: "PLASTIQUE"
    private String binColor;        // ex: "JAUNE"
    private String instruction;     // ex: "Vider et écraser avant de jeter"
    private String geminiLabel;     // ce que Gemini a retourné brut
    private Integer pointsEarned;   // points gagnés pour ce scan
    private Integer totalPoints;    // nouveau total de l'utilisateur
    private String message;         // ex: "Bien joué ! +10 points"

    // ========================
    // Nouveaux champs éducatifs — remplis par Gemini
    // ========================
    private String materiau;        // ex: "PET" ou "Verre" ou "Aluminium"
    private String explication;     // ex: "Le PET est recyclable à 100%"
    private String astuce;          // ex: "Garde le bouchon sur la bouteille"
    private Boolean recyclable;     // true si recyclable, false sinon
}