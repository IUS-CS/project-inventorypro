package com.inventory.service;

import com.inventory.model.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:inventory.db";

    private static Database instance;
    private Connection connection;

    private Database() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void init() {
        String sql = """
                CREATE TABLE IF NOT EXISTS items (
                    id       TEXT PRIMARY KEY,
                    name     TEXT NOT NULL,
                    category TEXT NOT NULL,
                    quantity INTEGER NOT NULL,
                    location TEXT NOT NULL
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }

    public List<Item> loadAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, quantity, location FROM items";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Item(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("location")));
            }
        } catch (SQLException e) {
            System.err.println("Database load error: " + e.getMessage());
        }
        return list;
    }

    public void insert(Item item) {
        String sql = "INSERT INTO items (id, name, category, quantity, location) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

    public void update(Item item) {
        String sql = "UPDATE items SET name = ?, category = ?, quantity = ?, location = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategory());
            ps.setInt(3, item.getQuantity());
            ps.setString(4, item.getLocation());
            ps.setString(5, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database update error: " + e.getMessage());
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database delete error: " + e.getMessage());
        }
    }
}
