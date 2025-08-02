package dev.smartshub.shkoth.storage.database.table;


import dev.smartshub.shkoth.storage.database.connection.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaCreator {

    public static void createSchema() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Main table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS player_stats (
                    player_id VARCHAR(36) PRIMARY KEY,
                    solo_wins INT NOT NULL DEFAULT 0,
                    team_wins INT NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);

            stmt.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_solo_wins ON player_stats(solo_wins DESC)
            """);

            stmt.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_team_wins ON player_stats(team_wins DESC)
            """);

            stmt.executeUpdate("""
                CREATE INDEX IF NOT EXISTS idx_total_wins ON player_stats((solo_wins + team_wins) DESC)
            """);

            System.out.println("Database schema created successfully!");

        } catch (SQLException e) {
            System.err.println("Error creating database schema:");
            e.printStackTrace();
        }
    }
}
