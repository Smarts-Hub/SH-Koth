package dev.smartshub.shkoth.model;

import java.util.UUID;

public record PlayerStats(UUID playerId, int soloWins, int teamWins) {

    public PlayerStats {
        if (playerId == null) {
            throw new IllegalArgumentException("Player ID cannot be null");
        }
        if (soloWins < 0) {
            throw new IllegalArgumentException("Solo wins cannot be negative");
        }
        if (teamWins < 0) {
            throw new IllegalArgumentException("Team wins cannot be negative");
        }
    }

    public int getTotalWins() {
        return soloWins + teamWins;
    }

    public PlayerStats withSoloWins(int newSoloWins) {
        return new PlayerStats(playerId, newSoloWins, teamWins);
    }

    public PlayerStats withTeamWins(int newTeamWins) {
        return new PlayerStats(playerId, soloWins, newTeamWins);
    }

    public PlayerStats addSoloWin() {
        return new PlayerStats(playerId, soloWins + 1, teamWins);
    }

    public PlayerStats addTeamWin() {
        return new PlayerStats(playerId, soloWins, teamWins + 1);
    }
}