package com.recycloscan.controller;

import com.recycloscan.entity.WasteItem;
import com.recycloscan.service.WasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waste")
@RequiredArgsConstructor
public class WasteController {

    private final WasteService wasteService;

    // ========================
    // GET /api/waste
    // Retourne tous les déchets — public
    // ========================
    @GetMapping
    public ResponseEntity<List<WasteItem>> getAll() {
        return ResponseEntity.ok(wasteService.getAll());
    }

    // ========================
    // GET /api/waste/search?q=bouteille
    // Recherche manuelle — public
    // ========================
    @GetMapping("/search")
    public ResponseEntity<List<WasteItem>> search(
            @RequestParam String q) {
        return ResponseEntity.ok(wasteService.search(q));
    }

    // ========================
    // GET /api/waste/{id}
    // Détail d'un déchet — authentifié
    // ========================
    @GetMapping("/{id}")
    public ResponseEntity<WasteItem> getById(@PathVariable Long id) {
        return wasteService.getAll()
                .stream()
                .filter(w -> w.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ========================
    // POST /api/waste
    // Ajouter un déchet — ADMIN seulement
    // ========================
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // bloque si l'utilisateur n'est pas ADMIN
    public ResponseEntity<WasteItem> create(@RequestBody WasteItem wasteItem) {
        return ResponseEntity.ok(wasteService.create(wasteItem));
    }

    // ========================
    // PUT /api/waste/{id}
    // Modifier un déchet — ADMIN seulement
    // ========================
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WasteItem> update(
            @PathVariable Long id,
            @RequestBody WasteItem wasteItem) {
        return ResponseEntity.ok(wasteService.update(id, wasteItem));
    }

    // ========================
    // DELETE /api/waste/{id}
    // Supprimer un déchet — ADMIN seulement
    // ========================
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wasteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}