package com.inventory.model;

import java.time.LocalDate;

public class Item {

    private final String id;
    private final String name;
    private final String category;
    private int quantity;
    private final String location;
    private double price;
    private String expiresAt; // "YYYY-MM-DD" or null/empty = no expiry
    private String supplier;

    private int lowStockThreshold = 5;

    public Item(String id, String name, String category, int quantity, String location,
            double price, String expiresAt, String supplier) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
        this.price = price;
        this.expiresAt = expiresAt;
        this.supplier = supplier;
    }

    public Item(String id, String name, String category, int quantity, String location) {
        this(id, name, category, quantity, location, 0.0, null, null);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public String getSupplier() {
        return supplier;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must not be negative.");
        }
        this.lowStockThreshold = threshold;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    public boolean isExpiringSoon() {
        if (expiresAt == null || expiresAt.isEmpty())
            return false;
        try {
            LocalDate expiry = LocalDate.parse(expiresAt);
            return !expiry.isAfter(LocalDate.now().plusDays(7));
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Item{id='" + id + "', name='" + name + "', category='" + category
                + "', quantity=" + quantity + ", location='" + location + "'}";
    }
}
