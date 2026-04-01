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
        seedData();
    }

    private void seedData() {
        String countSql = "SELECT COUNT(*) FROM items";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(countSql)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        } catch (SQLException e) {
            System.err.println("Seed check error: " + e.getMessage());
            return;
        }

        String[][] data = {
                { "SEED0001", "Whole Milk", "Grocery", "24", "Aisle 2 - Dairy & Eggs" },
                { "SEED0002", "White Bread", "Grocery", "18", "Aisle 3 - Bakery" },
                { "SEED0003", "Bananas", "Grocery", "40", "Aisle 1 - Produce" },
                { "SEED0004", "Chicken Breast", "Grocery", "8", "Aisle 4 - Meat & Seafood" },
                { "SEED0005", "Frozen Pizza", "Grocery", "15", "Aisle 5 - Frozen Foods" },
                { "SEED0006", "Orange Juice", "Grocery", "12", "Aisle 6 - Beverages" },
                { "SEED0007", "Potato Chips", "Grocery", "30", "Aisle 7 - Snacks & Candy" },
                { "SEED0008", "Paper Towels", "Home & Garden", "10", "Aisle 8 - Household" },
                { "SEED0009", "AA Batteries", "Electronics", "3", "Aisle 9 - Electronics" },
                { "SEED0010", "Ground Beef", "Grocery", "6", "Aisle 4 - Meat & Seafood" },
                { "SEED0011", "Cheddar Cheese", "Grocery", "14", "Aisle 2 - Dairy & Eggs" },
                { "SEED0012", "Eggs (dozen)", "Grocery", "20", "Aisle 2 - Dairy & Eggs" },
                { "SEED0013", "Apples", "Grocery", "35", "Aisle 1 - Produce" },
                { "SEED0014", "Ice Cream", "Grocery", "4", "Aisle 5 - Frozen Foods" },
                { "SEED0015", "Cola 12-pack", "Grocery", "9", "Aisle 6 - Beverages" },
                { "SEED0016", "Dish Soap", "Home & Garden", "7", "Aisle 8 - Household" },
                { "SEED0017", "T-Shirt (M)", "Clothing", "12", "Aisle 10 - Clothing" },
                { "SEED0018", "Basketball", "Sports", "2", "Aisle 12 - Sports & Outdoors" },
                { "SEED0019", "Notebook (3-pack)", "Office Supplies", "25", "Aisle 8 - Household" },
                { "SEED0020", "Candy Bar", "Grocery", "50", "Aisle 7 - Snacks & Candy" },
                { "SEED0021", "Great Value Water 24pk", "Grocery", "36", "Aisle 6 - Beverages" },
                { "SEED0022", "Peanut Butter", "Grocery", "11", "Aisle 7 - Snacks & Candy" },
                { "SEED0023", "Spaghetti Noodles", "Grocery", "22", "Aisle 7 - Snacks & Candy" },
                { "SEED0024", "Tomato Sauce", "Grocery", "19", "Aisle 7 - Snacks & Candy" },
                { "SEED0025", "Butter", "Grocery", "7", "Aisle 2 - Dairy & Eggs" },
                { "SEED0026", "Greek Yogurt", "Grocery", "16", "Aisle 2 - Dairy & Eggs" },
                { "SEED0027", "Bacon", "Grocery", "3", "Aisle 4 - Meat & Seafood" },
                { "SEED0028", "Strawberries", "Grocery", "10", "Aisle 1 - Produce" },
                { "SEED0029", "Laundry Detergent", "Home & Garden", "5", "Aisle 8 - Household" },
                { "SEED0030", "Trash Bags", "Home & Garden", "8", "Aisle 8 - Household" },
                { "SEED0031", "Toothpaste", "Health & Beauty", "13", "Aisle 8 - Household" },
                { "SEED0032", "Shampoo", "Health & Beauty", "9", "Aisle 8 - Household" },
                { "SEED0033", "USB-C Cable", "Electronics", "4", "Aisle 9 - Electronics" },
                { "SEED0034", "Cereal", "Grocery", "27", "Aisle 3 - Bakery" },
                { "SEED0035", "Frozen Chicken Nuggets", "Grocery", "1", "Aisle 5 - Frozen Foods" },
        };

        String sql = "INSERT INTO items (id, name, category, quantity, location) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : data) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.setString(3, row[2]);
                ps.setInt(4, Integer.parseInt(row[3]));
                ps.setString(5, row[4]);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Seed data error: " + e.getMessage());
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

    public void resetToSeed() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM items");
        } catch (SQLException e) {
            System.err.println("Database reset error: " + e.getMessage());
        }
        seedData();
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
