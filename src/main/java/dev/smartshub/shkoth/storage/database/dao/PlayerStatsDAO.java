package dev.smartshub.shkoth.storage.database.dao;

import dev.smartshub.shkoth.model.PlayerStats;
import dev.smartshub.shkoth.storage.database.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerStatsDAO {

    // SQL queries
    private static final String SELECT_BY_ID =
            "SELECT player_id, solo_wins, team_wins FROM player_stats WHERE player_id = ?";

    private static final String INSERT_OR_UPDATE =
            "INSERT INTO player_stats (player_id, solo_wins, team_wins) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE solo_wins = VALUES(solo_wins), team_wins = VALUES(team_wins)";



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
                e.printStackTrace();
            }

            return Optional.empty();
        });
    }


    public CompletableFuture<Boolean> savePlayerStats(PlayerStats stats) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(INSERT_OR_UPDATE)) {

                stmt.setString(1, stats.playerId().toString());
                stmt.setInt(2, stats.soloWins());
                stmt.setInt(3, stats.teamWins());

                return stmt.executeUpdate() > 0;

            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}