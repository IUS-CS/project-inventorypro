package com.inventory.service;

import com.inventory.model.Item;
import com.inventory.model.Transaction;

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
                    id         TEXT PRIMARY KEY,
                    name       TEXT NOT NULL,
                    category   TEXT NOT NULL,
                    quantity   INTEGER NOT NULL,
                    location   TEXT NOT NULL,
                    price      REAL NOT NULL DEFAULT 0.0,
                    expires_at TEXT DEFAULT '',
                    supplier   TEXT DEFAULT ''
                )
                """;
        String transactionSql = """
                CREATE TABLE IF NOT EXISTS transactions (
                    id TEXT PRIMARY KEY,
                    item_id TEXT NOT NULL,
                    type TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    timestamp TEXT NOT NULL
                )
                """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            stmt.execute(transactionSql);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
        // Migrate existing databases that predate the new columns
        String[] migrations = {
                "ALTER TABLE items ADD COLUMN price REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE items ADD COLUMN expires_at TEXT DEFAULT ''",
                "ALTER TABLE items ADD COLUMN supplier TEXT DEFAULT ''"
        };
        for (String migration : migrations) {
            try (Statement m = connection.createStatement()) {
                m.execute(migration);
            } catch (SQLException ignored) {
                /* column already exists */ }
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

        // id, name, category, quantity, location, price, expires_at, supplier
        String[][] data = {
                { "SEED0001", "Whole Milk", "Grocery", "24", "Aisle 2 - Dairy & Eggs", "3.49", "2026-05-02",
                        "Prairie Farms" },
                { "SEED0002", "White Bread", "Grocery", "18", "Aisle 3 - Bakery", "2.99", "2026-05-03",
                        "Great Value" },
                { "SEED0003", "Bananas", "Grocery", "40", "Aisle 1 - Produce", "0.59", "2026-04-30", "Kroger" },
                { "SEED0004", "Chicken Breast", "Grocery", "8", "Aisle 4 - Meat & Seafood", "8.99", "2026-05-01",
                        "Kroger" },
                { "SEED0005", "Frozen Pizza", "Grocery", "15", "Aisle 5 - Frozen Foods", "6.49", "2026-08-15",
                        "Great Value" },
                { "SEED0006", "Orange Juice", "Grocery", "12", "Aisle 6 - Beverages", "4.29", "2026-05-10",
                        "Kroger" },
                { "SEED0007", "Potato Chips", "Grocery", "30", "Aisle 7 - Snacks & Candy", "3.99", "2026-10-01",
                        "Great Value" },
                { "SEED0008", "Paper Towels", "Home & Garden", "10", "Aisle 8 - Household", "8.49", "",
                        "Kirkland" },
                { "SEED0009", "AA Batteries", "Electronics", "3", "Aisle 9 - Electronics", "7.99", "", "Tech" },
                { "SEED0010", "Ground Beef", "Grocery", "6", "Aisle 4 - Meat & Seafood", "7.49", "2026-04-30",
                        "Kroger" },
                { "SEED0011", "Cheddar Cheese", "Grocery", "14", "Aisle 2 - Dairy & Eggs", "4.79", "2026-05-12",
                        "Prairie Farms" },
                { "SEED0012", "Eggs (dozen)", "Grocery", "20", "Aisle 2 - Dairy & Eggs", "3.29", "2026-05-05",
                        "Prairie Farms" },
                { "SEED0013", "Apples", "Grocery", "35", "Aisle 1 - Produce", "1.49", "2026-05-08", "Kroger" },
                { "SEED0014", "Ice Cream", "Grocery", "4", "Aisle 5 - Frozen Foods", "5.29", "2026-07-20",
                        "Great Value" },
                { "SEED0015", "Cola 12-pack", "Grocery", "9", "Aisle 6 - Beverages", "5.99", "2026-12-01",
                        "Great Value" },
                { "SEED0016", "Dish Soap", "Home & Garden", "7", "Aisle 8 - Household", "3.49", "", "Kirkland" },
                { "SEED0017", "T-Shirt (M)", "Clothing", "12", "Aisle 10 - Clothing", "14.99", "", "Target Essentials" },
                { "SEED0018", "Basketball", "Sports", "2", "Aisle 12 - Sports & Outdoors", "24.99", "",
                        "Target Essentials" },
                { "SEED0019", "Notebook (3-pack)", "Office Supplies", "25", "Aisle 8 - Household", "6.99", "",
                        "Kirkland" },
                { "SEED0020", "Candy Bar", "Grocery", "50", "Aisle 7 - Snacks & Candy", "1.29", "2026-11-15",
                        "Great Value" },
                { "SEED0021", "Great Value Water 24pk", "Grocery", "36", "Aisle 6 - Beverages", "4.99", "2027-01-01",
                        "Great Value" },
                { "SEED0022", "Peanut Butter", "Grocery", "11", "Aisle 7 - Snacks & Candy", "3.99", "2026-09-30",
                        "Great Value" },
                { "SEED0023", "Spaghetti Noodles", "Grocery", "22", "Aisle 7 - Snacks & Candy", "1.89", "2026-12-31",
                        "Great Value" },
                { "SEED0024", "Tomato Sauce", "Grocery", "19", "Aisle 7 - Snacks & Candy", "1.99", "2026-11-30",
                        "Great Value" },
                { "SEED0025", "Butter", "Grocery", "7", "Aisle 2 - Dairy & Eggs", "4.49", "2026-05-20",
                        "Prairie Farms" },
                { "SEED0026", "Greek Yogurt", "Grocery", "16", "Aisle 2 - Dairy & Eggs", "1.99", "2026-05-04",
                        "Prairie Farms" },
                { "SEED0027", "Bacon", "Grocery", "3", "Aisle 4 - Meat & Seafood", "6.99", "2026-05-01",
                        "Kroger" },
                { "SEED0028", "Strawberries", "Grocery", "10", "Aisle 1 - Produce", "3.99", "2026-04-30",
                        "Kroger" },
                { "SEED0029", "Laundry Detergent", "Home & Garden", "5", "Aisle 8 - Household", "11.99", "",
                        "Kirkland" },
                { "SEED0030", "Trash Bags", "Home & Garden", "8", "Aisle 8 - Household", "7.99", "",
                        "Kirkland" },
                { "SEED0031", "Toothpaste", "Health & Beauty", "13", "Aisle 8 - Household", "3.49", "2026-12-31",
                        "Kirkland" },
                { "SEED0032", "Shampoo", "Health & Beauty", "9", "Aisle 8 - Household", "5.99", "2027-06-30",
                        "Kirkland" },
                { "SEED0033", "USB-C Cable", "Electronics", "4", "Aisle 9 - Electronics", "12.99", "", "Tech" },
                { "SEED0034", "Cereal", "Grocery", "27", "Aisle 3 - Bakery", "3.99", "2026-08-01", "Great Value" },
                { "SEED0035", "Frozen Chicken Nuggets", "Grocery", "1", "Aisle 5 - Frozen Foods", "5.49", "2026-05-04",
                        "Kroger" },
        };

        String sql = "INSERT INTO items (id, name, category, quantity, location, price, expires_at, supplier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String[] row : data) {
                ps.setString(1, row[0]);
                ps.setString(2, row[1]);
                ps.setString(3, row[2]);
                ps.setInt(4, Integer.parseInt(row[3]));
                ps.setString(5, row[4]);
                ps.setDouble(6, Double.parseDouble(row[5]));
                ps.setString(7, row[6]);
                ps.setString(8, row[7]);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Seed data error: " + e.getMessage());
        }
    }

    public List<Item> loadAll() {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT id, name, category, quantity, location, price, expires_at, supplier FROM items";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Item(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("location"),
                        rs.getDouble("price"),
                        rs.getString("expires_at"),
                        rs.getString("supplier")));
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
        String sql = "INSERT INTO items (id, name, category, quantity, location, price, expires_at, supplier) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getId());
            ps.setString(2, item.getName());
            ps.setString(3, item.getCategory());
            ps.setInt(4, item.getQuantity());
            ps.setString(5, item.getLocation());
            ps.setDouble(6, item.getPrice());
            ps.setString(7, item.getExpiresAt() != null ? item.getExpiresAt() : "");
            ps.setString(8, item.getSupplier() != null ? item.getSupplier() : "");
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database insert error: " + e.getMessage());
        }
    }

    public void update(Item item) {
        String sql = "UPDATE items SET name = ?, category = ?, quantity = ?, location = ?, price = ?, expires_at = ?, supplier = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, item.getName());
            ps.setString(2, item.getCategory());
            ps.setInt(3, item.getQuantity());
            ps.setString(4, item.getLocation());
            ps.setDouble(5, item.getPrice());
            ps.setString(6, item.getExpiresAt() != null ? item.getExpiresAt() : "");
            ps.setString(7, item.getSupplier() != null ? item.getSupplier() : "");
            ps.setString(8, item.getId());
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

    public void insertTransaction(Transaction t) {
        String sql = "INSERT INTO transactions (id, item_id, type, amount, timestamp) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getId());
            ps.setString(2, t.getItemId());
            ps.setString(3, t.getType());
            ps.setInt(4, t.getAmount());
            ps.setString(5, t.getTimestamp());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Transaction insert error: " + e.getMessage());
        }
    }

    public List<Transaction> loadTransactionsForItem(String itemId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT id, item_id, type, amount, timestamp FROM transactions WHERE item_id = ? ORDER BY timestamp DESC";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            rs.getString("id"),
                            rs.getString("item_id"),
                            rs.getString("type"),
                            rs.getInt("amount"),
                            rs.getString("timestamp")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Transaction load error: " + e.getMessage());
        }

        return list;
    }
}
