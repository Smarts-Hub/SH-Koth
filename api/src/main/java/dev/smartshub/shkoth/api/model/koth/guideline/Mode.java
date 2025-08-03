package dev.smartshub.shkoth.api.model.koth.guideline;

public record Mode(
        KothType type,
        int teamSize
) {
    // Always 0 for solo mode
    public static Mode solo() {
        return new Mode(KothType.SOLO, 0);
    }

    public static Mode team(int teamSize) {
        if (teamSize <= 1) {
            throw new IllegalArgumentException("Team size must be > 1");
        }
        return new Mode(KothType.TEAM, teamSize);
    }
}
