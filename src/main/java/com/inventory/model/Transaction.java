package com.inventory.model;

public class Transaction {

    private final String id;
    private final String itemId;
    private final String type;
    private final int amount;
    private final String timestamp;

    public Transaction(String id, String itemId, String type, int amount, String timestamp) {
        this.id = id;
        this.itemId = itemId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getItemId() { return itemId; }
    public String getType() { return type; }
    public int getAmount() { return amount; }
    public String getTimestamp() { return timestamp; }
}
