package com.recycloscan.service;

import com.recycloscan.entity.ScanHistory;
import com.recycloscan.entity.User;
import com.recycloscan.repository.ScanHistoryRepository;
import com.recycloscan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final ScanHistoryRepository scanHistoryRepository;

    // ========================
    // Retourne toutes les stats de l'utilisateur connecté
    // ========================
    public Map<String, Object> getMyStats() {

        // Récupérer l'utilisateur connecté
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer tout l'historique
        List<ScanHistory> history = scanHistoryRepository
                .findByUserIdOrderByScannedAtDesc(user.getId());

        Map<String, Object> stats = new HashMap<>();

        // ========================
        // Stats générales
        // ========================
        stats.put("totalPoints", user.getTotalPoints());
        stats.put("totalScans", history.size());

        // ========================
        // Niveau et progression
        // ========================
        stats.put("level", getLevel(user.getTotalPoints()));
        stats.put("levelName", getLevelName(user.getTotalPoints()));
        stats.put("nextLevelPoints", getNextLevelPoints(user.getTotalPoints()));
        stats.put("levelProgress", getLevelProgress(user.getTotalPoints()));

        // ========================
        // Scans cette semaine
        // ========================
        LocalDateTime startOfWeek = LocalDateTime.now()
                .with(DayOfWeek.MONDAY)
                .withHour(0).withMinute(0).withSecond(0);

        long scansThisWeek = history.stream()
                .filter(h -> h.getScannedAt().isAfter(startOfWeek))
                .count();
        stats.put("scansThisWeek", scansThisWeek);

        // ========================
        // Streak — jours consécutifs
        // ========================
        stats.put("streak", calculateStreak(history));

        // ========================
        // Impact environnemental
        // Formules basées sur des moyennes réelles
        // ========================
        double kgRecycles = history.size() * 0.15; // 150g par déchet en moyenne
        double co2Economise = kgRecycles * 2.5;    // 2.5kg CO2 par kg recyclé
        int bouteillesRecyclees = (int)(kgRecycles / 0.025); // 25g par bouteille
        double arbresEquivalent = co2Economise / 21.0; // 21kg CO2 absorbé par arbre/an

        stats.put("kgRecycles", Math.round(kgRecycles * 10.0) / 10.0);
        stats.put("co2Economise", Math.round(co2Economise * 10.0) / 10.0);
        stats.put("bouteillesRecyclees", bouteillesRecyclees);
        stats.put("arbresEquivalent", Math.round(arbresEquivalent * 100.0) / 100.0);

        // ========================
        // Graphique d'activité — 7 derniers jours
        // ========================
        stats.put("activityLast7Days", getActivityLast7Days(history));

        // ========================
        // Répartition par catégorie
        // ========================
        stats.put("categoryBreakdown", getCategoryBreakdown(history));

        // ========================
        // Badges gagnés
        // ========================
        stats.put("badges", getBadges(user, history));

        // ========================
        // Défis hebdomadaires
        // ========================
        stats.put("challenges", getChallenges(history, scansThisWeek));

        return stats;
    }

    // ========================
    // Calcule le niveau selon les points
    // ========================
    private int getLevel(int points) {
        if (points >= 1000) return 5;
        if (points >= 500)  return 4;
        if (points >= 200)  return 3;
        if (points >= 50)   return 2;
        return 1;
    }

    private String getLevelName(int points) {
        if (points >= 1000) return "Champion ♻️";
        if (points >= 500)  return "Expert 🌟";
        if (points >= 200)  return "Recycleur 💚";
        if (points >= 50)   return "Apprenti 🌱";
        return "Débutant 🐣";
    }

    private int getNextLevelPoints(int points) {
        if (points >= 1000) return 1000;
        if (points >= 500)  return 1000;
        if (points >= 200)  return 500;
        if (points >= 50)   return 200;
        return 50;
    }

    private int getLevelProgress(int points) {
        if (points >= 1000) return 100;
        if (points >= 500)  return (int)((points - 500) / 5.0);
        if (points >= 200)  return (int)((points - 200) / 3.0);
        if (points >= 50)   return (int)((points - 50) / 1.5);
        return (int)(points / 0.5);
    }

    // ========================
    // Calcule le streak de jours consécutifs
    // ========================
    private int calculateStreak(List<ScanHistory> history) {
        if (history.isEmpty()) return 0;

        // Extraire les dates uniques de scan
        Set<String> scanDates = history.stream()
                .map(h -> h.getScannedAt().toLocalDate().toString())
                .collect(Collectors.toSet());

        int streak = 0;
        LocalDateTime current = LocalDateTime.now();

        // Compter les jours consécutifs en remontant depuis aujourd'hui
        while (scanDates.contains(current.toLocalDate().toString())) {
            streak++;
            current = current.minus(1, ChronoUnit.DAYS);
        }

        return streak;
    }

    // ========================
    // Activité des 7 derniers jours pour le graphique
    // ========================
    private List<Map<String, Object>> getActivityLast7Days(List<ScanHistory> history) {
        List<Map<String, Object>> activity = new ArrayList<>();

        String[] jours = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};

        for (int i = 6; i >= 0; i--) {
            LocalDateTime day = LocalDateTime.now().minus(i, ChronoUnit.DAYS);
            String dayStr = day.toLocalDate().toString();;

            // Compter les scans ce jour
            long count = history.stream()
                    .filter(h -> h.getScannedAt().toLocalDate().toString().equals(dayStr))
                    .count();

            Map<String, Object> dayData = new HashMap<>();
            dayData.put("jour", jours[day.getDayOfWeek().getValue() - 1]);
            dayData.put("scans", count);
            activity.add(dayData);
        }

        return activity;
    }

    // ========================
    // Répartition des déchets par catégorie
    // ========================
    private Map<String, Long> getCategoryBreakdown(List<ScanHistory> history) {
        return history.stream()
                .collect(Collectors.groupingBy(
                        h -> h.getWasteItem().getCategory().name(),
                        Collectors.counting()
                ));
    }

    // ========================
    // Badges gagnés selon les actions
    // ========================
    private List<Map<String, Object>> getBadges(User user, List<ScanHistory> history) {
        List<Map<String, Object>> badges = new ArrayList<>();

        // Badge premier scan
        addBadge(badges, "Premier Pas", "🌱",
                "Premier scan effectué",
                history.size() >= 1);

        // Badge 10 scans
        addBadge(badges, "Engagé", "🌿",
                "10 scans effectués",
                history.size() >= 10);

        // Badge 50 scans
        addBadge(badges, "Recycleur", "♻️",
                "50 scans effectués",
                history.size() >= 50);

        // Badge 100 scans
        addBadge(badges, "Expert", "🏆",
                "100 scans effectués",
                history.size() >= 100);

        // Badge 5 catégories différentes
        long categories = history.stream()
                .map(h -> h.getWasteItem().getCategory())
                .distinct().count();
        addBadge(badges, "Polyvalent", "🎯",
                "5 catégories différentes scannées",
                categories >= 5);

        // Badge streak 7 jours
        addBadge(badges, "Assidu", "🔥",
                "7 jours consécutifs de scan",
                calculateStreak(history) >= 7);

        // Badge 500 points
        addBadge(badges, "Star", "⭐",
                "500 points atteints",
                user.getTotalPoints() >= 500);

        // Badge 1000 points
        addBadge(badges, "Champion", "👑",
                "1000 points atteints",
                user.getTotalPoints() >= 1000);

        return badges;
    }

    private void addBadge(List<Map<String, Object>> badges,
                          String name, String emoji,
                          String description, boolean earned) {
        Map<String, Object> badge = new HashMap<>();
        badge.put("name", name);
        badge.put("emoji", emoji);
        badge.put("description", description);
        badge.put("earned", earned);
        badges.add(badge);
    }

    // ========================
    // Défis hebdomadaires
    // ========================
    private List<Map<String, Object>> getChallenges(
            List<ScanHistory> history, long scansThisWeek) {

        List<Map<String, Object>> challenges = new ArrayList<>();

        // Défi 1 — Scanner 5 déchets cette semaine
        addChallenge(challenges,
                "Scanner 5 déchets cette semaine",
                "🎯", (int) scansThisWeek, 5, 50);

        // Défi 2 — Scanner un plastique
        long plastiquesThisWeek = history.stream()
                .filter(h -> h.getScannedAt().isAfter(
                        LocalDateTime.now().with(DayOfWeek.MONDAY)
                                .withHour(0).withMinute(0)))
                .filter(h -> h.getWasteItem().getCategory().name().equals("PLASTIQUE"))
                .count();
        addChallenge(challenges,
                "Scanner 3 plastiques",
                "🧴", (int) plastiquesThisWeek, 3, 30);

        // Défi 3 — Scanner un déchet dangereux
        long dangereuxThisWeek = history.stream()
                .filter(h -> h.getScannedAt().isAfter(
                        LocalDateTime.now().with(DayOfWeek.MONDAY)
                                .withHour(0).withMinute(0)))
                .filter(h -> h.getWasteItem().getCategory().name().equals("DANGEREUX"))
                .count();
        addChallenge(challenges,
                "Identifier 1 déchet dangereux",
                "⚠️", (int) dangereuxThisWeek, 1, 20);

        // Défi 4 — Scanner 2 jours de suite
        addChallenge(challenges,
                "Scanner 2 jours de suite",
                "🔥", Math.min(calculateStreak(history), 2), 2, 25);

        return challenges;
    }

    private void addChallenge(List<Map<String, Object>> challenges,
                              String title, String emoji,
                              int current, int target, int bonusPoints) {
        Map<String, Object> challenge = new HashMap<>();
        challenge.put("title", title);
        challenge.put("emoji", emoji);
        challenge.put("current", current);
        challenge.put("target", target);
        challenge.put("bonusPoints", bonusPoints);
        challenge.put("completed", current >= target);
        challenge.put("progress", Math.min((int)((current * 100.0) / target), 100));
        challenges.add(challenge);
    }
}