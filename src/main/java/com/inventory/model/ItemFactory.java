package com.inventory.model;

import java.util.UUID;

public class ItemFactory {

    /**
     * Creates a new Item with an auto-generated unique ID 
     */
    public static Item createItem(String name, String category, int quantity, String location) {
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Item(id, name, category, quantity, location);
    }

    /**
     * Creates a new Item with price, expiry date, and supplier.
     */
    public static Item createItem(String name, String category, int quantity, String location,
            double price, String expiresAt, String supplier) {
        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Item(id, name, category, quantity, location, price, expiresAt, supplier);
    }
}
