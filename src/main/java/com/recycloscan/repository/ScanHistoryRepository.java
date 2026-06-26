// ScanHistoryRepository.java
package com.recycloscan.repository;

import com.recycloscan.entity.ScanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScanHistoryRepository extends JpaRepository<ScanHistory, Long> {

    // Historique d'un utilisateur trié du plus récent au plus ancien
    List<ScanHistory> findByUserIdOrderByScannedAtDesc(Long userId);

    // Nombre total de scans d'un utilisateur
    long countByUserId(Long userId);
}