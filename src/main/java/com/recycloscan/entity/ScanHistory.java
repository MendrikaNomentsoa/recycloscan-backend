package com.recycloscan.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation ManyToOne : plusieurs scans appartiennent à un utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Relation ManyToOne : plusieurs scans peuvent concerner le même déchet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waste_item_id", nullable = false)
    private WasteItem wasteItem;

    // Ce que Gemini a exactement retourné (utile pour le debug)
    @Column(length = 500)
    private String geminiLabel;

    @Column(nullable = false)
    private Integer pointsEarned;

    @Column(nullable = false, updatable = false)
    private LocalDateTime scannedAt;

    @PrePersist
    protected void onCreate() {
        scannedAt = LocalDateTime.now();
    }
}