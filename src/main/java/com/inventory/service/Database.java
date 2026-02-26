package com.inventory.service;

import com.inventory.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    // Creates the items table
    public static void init() {
        String sql = """
                CREATE TABLE IF NOT EXISTS items (
                    id       TEXT PRIMARY KEY,
                    name     TEXT NOT NULL,
                    category TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    location TEXT NOT NULL
                )
                """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }

    public static List<Item> loadAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, quantity, location FROM items";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Item(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("location")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Database load error: " + e.getMessage());
        }
        return list;
    }

    public static void insert(Item item) {
        String sql = "INSERT INTO items (id, name, category, quantity, location) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getCategory());
            ps.setInt(4, item.getQuantity());
            ps.setString(5, item.getLocation());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database insert error: " + e.getMessage());
        }
    }

    public static void delete(String id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database delete error: " + e.getMessage());
        }
    }
}
