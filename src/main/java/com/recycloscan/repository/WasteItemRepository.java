// WasteItemRepository.java
package com.recycloscan.repository;

import com.recycloscan.entity.WasteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WasteItemRepository extends JpaRepository<WasteItem, Long> {

    // Recherche par nom (insensible à la casse)
    // LOWER() pour ignorer majuscules/minuscules
    @Query("SELECT w FROM WasteItem w WHERE LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WasteItem> searchByName(@Param("keyword") String keyword);

    // Recherche dans les mots-clés Gemini
    // Utilisé pour matcher la réponse de Gemini avec un déchet en base
    @Query("SELECT w FROM WasteItem w WHERE LOWER(w.geminiKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WasteItem> findByGeminiKeyword(@Param("keyword") String keyword);

    // Recherche combinée : nom OU mots-clés Gemini
    @Query("SELECT w FROM WasteItem w WHERE " +
            "LOWER(w.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(w.geminiKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WasteItem> searchByNameOrKeyword(@Param("keyword") String keyword);
}