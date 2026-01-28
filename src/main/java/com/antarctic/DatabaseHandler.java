package com.antarctic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:antarctic_log.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            String sql = """
                    CREATE TABLE IF NOT EXISTS encounters (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        timestamp TEXT,
                        island TEXT,
                        sex TEXT,
                        species TEXT
                    )
                    """;

            stmt.execute(sql);
            System.out.println("Database Initalized successfully");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void saveEncounter(String island, String sex, String species) {

        String sql = """
                INSERT INTO encounters (timestamp, island, sex, species)
                VALUES (datetime('now'), ?, ?, ?)
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, island);
            pstmt.setString(2, sex);
            pstmt.setString(3, species);

            pstmt.executeUpdate();

            System.out.println("Success! Saved: " + species);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void printAllEncounters() {
        String sql = "SELECT * FROM encounters";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n--- FIELD LOG HISTORY ---");
            while (rs.next()) {
                System.out.printf("ID: %d | %s | %s | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("timestamp"),
                        rs.getString("island"),
                        rs.getString("sex"),
                        rs.getString("species"));
                System.out.println("--------------------\n");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
