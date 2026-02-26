package com.inventory.model;

public class Item {

    private final String id;
    private final String name;
    private final String category;
    private int quantity;
    private final String location;

    public Item(String id, String name, String category, int quantity, String location) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.location = location;
    }

    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getCategory() { return category; }
    public int    getQuantity() { return quantity; }
    public String getLocation() { return location; }

    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item{id='" + id + "', name='" + name + "', category='" + category
                + "', quantity=" + quantity + ", location='" + location + "'}";
    }
}
