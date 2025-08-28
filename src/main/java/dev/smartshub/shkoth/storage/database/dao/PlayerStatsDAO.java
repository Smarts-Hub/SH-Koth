package dev.smartshub.shkoth.storage.database.dao;

import dev.smartshub.shkoth.api.stat.PlayerStats;
import dev.smartshub.shkoth.storage.database.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStatsDAO {

    private static final String SELECT_BY_ID =
            "SELECT player_id, solo_wins, team_wins FROM player_stats WHERE player_id = ?";

    private static final String INSERT_OR_UPDATE =
            "INSERT INTO player_stats (player_id, solo_wins, team_wins) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE solo_wins = VALUES(solo_wins), team_wins = VALUES(team_wins)";

    private static final String INCREMENT_SOLO_WIN =
            "INSERT INTO player_stats (player_id, solo_wins, team_wins) VALUES (?, 1, 0) " +
                    "ON DUPLICATE KEY UPDATE solo_wins = solo_wins + 1";

    private static final String INCREMENT_TEAM_WIN =
            "INSERT INTO player_stats (player_id, solo_wins, team_wins) VALUES (?, 0, 1) " +
                    "ON DUPLICATE KEY UPDATE team_wins = team_wins + 1";

    public CompletableFuture<Optional<PlayerStats>> getPlayerStats(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {

                stmt.setString(1, playerId.toString());

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        UUID id = UUID.fromString(rs.getString("player_id"));
                        int soloWins = rs.getInt("solo_wins");
                        int teamWins = rs.getInt("team_wins");
                        return Optional.of(new PlayerStats(id, soloWins, teamWins));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching player stats", e);
            }
            return Optional.empty();
        });
    }

    public CompletableFuture<Void> savePlayerStats(PlayerStats stats) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INSERT_OR_UPDATE)) {

                stmt.setString(1, stats.playerId().toString());
                stmt.setInt(2, stats.soloWins());
                stmt.setInt(3, stats.teamWins());

                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error saving player stats", e);
            }
        });
    }

    public CompletableFuture<Void> increaseSoloWin(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INCREMENT_SOLO_WIN)) {

                stmt.setString(1, playerId.toString());
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error incrementing solo wins", e);
            }
        });
    }

    public CompletableFuture<Void> increaseTeamWin(UUID playerId) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INCREMENT_TEAM_WIN)) {

                stmt.setString(1, playerId.toString());
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Error incrementing team wins", e);
            }
        });
    }

    public CompletableFuture<Void> increasePlayerWins(UUID playerId, boolean isTeamWin) {
        return isTeamWin ? increaseTeamWin(playerId) : increaseSoloWin(playerId);
    }

    public CompletableFuture<Integer> getTotalWins(UUID playerId) {
        return getPlayerStats(playerId)
                .thenApply(optionalStats ->
                        optionalStats.map(stats -> stats.soloWins() + stats.teamWins())
                                .orElse(0)
                );
    }

    public CompletableFuture<Integer> getSoloWins(UUID playerId) {
        return getPlayerStats(playerId)
                .thenApply(optionalStats ->
                        optionalStats.map(PlayerStats::soloWins)
                                .orElse(0)
                );
    }

    public CompletableFuture<Integer> getTeamWins(UUID playerId) {
        return getPlayerStats(playerId)
                .thenApply(optionalStats ->
                        optionalStats.map(PlayerStats::teamWins)
                                .orElse(0)
                );
    }

    public CompletableFuture<Integer> getWins(UUID playerId, boolean isTeam) {
        return isTeam ? getTeamWins(playerId) : getSoloWins(playerId);
    }
}