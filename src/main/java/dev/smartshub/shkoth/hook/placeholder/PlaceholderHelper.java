package dev.smartshub.shkoth.hook.placeholder;

public class PlaceholderHelper {

    public String extractKothName(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            return null;
        }

        int lastUnderscoreIndex = identifier.lastIndexOf('_');
        if (lastUnderscoreIndex <= 0) {
            return "";
        }

        return identifier.substring(0, lastUnderscoreIndex);
    }

    public String extractPlaceholderType(String identifier) {
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
