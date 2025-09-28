package dev.smartshub.shkoth.hook.placeholder;

import dev.smartshub.shkoth.api.koth.Koth;
import dev.smartshub.shkoth.api.stat.StatType;
import dev.smartshub.shkoth.registry.KothRegistry;

import java.util.List;

public class PlaceholderHelper {

    private final KothRegistry kothRegistry;

    public PlaceholderHelper(KothRegistry kothRegistry) {
        this.kothRegistry = kothRegistry;
    }

    private static List<String> ignoredToExtract = List.of(
            "player_context",
            "koth_context",
            "aux_context",
            "total_wins",
            "solo_wins",
            "team_wins",
            "next_koth",
            "next_koth_in",
            "next_koth_in_formatted");

    public String extractKothName(String identifier) {

        if(ignoredToExtract.contains(identifier)) return null;

        if (identifier == null || identifier.isEmpty()) {
            return null;
        }

        int firstUnderscoreIndex = identifier.indexOf('_');
        if (firstUnderscoreIndex <= 0) {
            return null;
        }

        var kothID = identifier.substring(0, firstUnderscoreIndex);

        return kothRegistry.getAll().stream()
                .map(Koth::getId)
                .filter(id -> id.equalsIgnoreCase(kothID))
                .findFirst()
                .orElse(null);
    }



    public String extractPlaceholderType(String identifier) {
        if(ignoredToExtract.contains(identifier)) return identifier;

        if (identifier == null || identifier.isEmpty()) {
            return null;
        }

        int firstUnderscoreIndex = identifier.indexOf('_');

        if (firstUnderscoreIndex < 0 || firstUnderscoreIndex >= identifier.length() - 1) {
            return null;
        }

        return identifier.substring(firstUnderscoreIndex + 1);
    }

    public String formatTime(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }
}