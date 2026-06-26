// dto/ScanResponseDto.java
package com.recycloscan.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ScanResponseDto {
    private String wasteName;       // ex: "Bouteille en plastique"
    private String category;        // ex: "PLASTIQUE"
    private String binColor;        // ex: "JAUNE"
    private String instruction;     // ex: "Vider et écraser avant de jeter"
    private String geminiLabel;     // ce que Gemini a retourné brut
    private Integer pointsEarned;   // points gagnés pour ce scan
    private Integer totalPoints;    // nouveau total de l'utilisateur
    private String message;         // ex: "Bien joué ! +10 points"
}