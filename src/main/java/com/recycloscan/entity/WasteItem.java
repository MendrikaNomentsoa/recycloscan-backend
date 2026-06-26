package com.recycloscan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "waste_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WasteItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // ex: "Bouteille en plastique"

    // Mots-clés séparés par virgule pour matcher avec la réponse de Gemini
    // ex: "bouteille,plastique,pet,flacon"
    @Column(nullable = false, length = 500)
    private String geminiKeywords;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BinColor binColor;

    // Consigne de tri affichée à l'utilisateur
    @Column(nullable = false, length = 500)
    private String instruction;

    // Points gagnés quand l'utilisateur scanne ce déchet
    @Column(nullable = false)
    private Integer pointsValue = 10;

    public enum Category {
        PLASTIQUE, VERRE, PAPIER, ORGANIQUE, DANGEREUX, AUTRE
    }

    public enum BinColor {
        JAUNE,  // plastiques, métaux, cartons
        VERTE,  // verre
        BLEUE,  // papier dans certaines communes
        MARRON, // déchets organiques
        GRISE   // ordures ménagères non recyclables
    }
}