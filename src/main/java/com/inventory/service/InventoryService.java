package com.inventory.service;

import com.inventory.model.Item;
import com.inventory.model.Transaction;
import java.time.LocalDateTime;
import java.util.UUID;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InventoryService {

    private final Map<String, Item> store = new LinkedHashMap<>();
    private final Database db;

    public InventoryService() {
        this.db = null;
    }

    public InventoryService(Database db) {
        this.db = db;
        db.init();
        db.loadAll().forEach(item -> store.put(item.getId(), item));
    }

    public void addItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null.");
        }
        if (store.containsKey(item.getId())) {
            throw new IllegalArgumentException("An item with id '" + item.getId() + "' already exists.");
        }
        store.put(item.getId(), item);
        if (db != null) {
            db.insert(item);
            db.insertTransaction(new Transaction(
                    UUID.randomUUID().toString(),
                    item.getId(),
                    "ADD",
                    item.getQuantity(),
                    LocalDateTime.now().toString()));
        }
    }

    public Item findItemById(String id) {
        Item item = store.get(id);
        if (item == null) {
            throw new IllegalArgumentException("No item found with id '" + id + "'.");
        }
        return item;
    }

    public void updateQuantity(String id, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must not be negative.");
        }
        Item item = findItemById(id);
        int oldQty = item.getQuantity();
        item.setQuantity(quantity);

        if (db != null) {
            db.update(item);
            db.insertTransaction(new Transaction(
                    UUID.randomUUID().toString(),
                    item.getId(),
                    "UPDATE",
                    quantity - oldQty,
                    LocalDateTime.now().toString()));
        }
    }

    public void updateItem(Item item) {
        if (!store.containsKey(item.getId())) {
            throw new IllegalArgumentException("No item found with id '" + item.getId() + "'.");
        }
        store.put(item.getId(), item);
        if (db != null)
            db.update(item);
    }

    public void removeItem(String id) {
        if (!store.containsKey(id)) {
            throw new IllegalArgumentException("No item found with id '" + id + "'.");
        }
        Item item = store.remove(id);

        if (db != null) {
            db.delete(id);
            db.insertTransaction(new Transaction(
                    UUID.randomUUID().toString(),
                    id,
                    "DELETE",
                    item.getQuantity(),
                    LocalDateTime.now().toString()));
        }
    }

    public List<Item> listItems() {
        return new ArrayList<>(store.values());
    }

    public int size() {
        return store.size();
    }

    public void reload() {
        store.clear();
        if (db != null) {
            db.loadAll().forEach(item -> store.put(item.getId(), item));
        }
    }

    public List<Transaction> getTransactionsForItem(String itemId) {
        if (db == null) {
            return new ArrayList<>();
        }
        return db.loadTransactionsForItem(itemId);
    }
}
