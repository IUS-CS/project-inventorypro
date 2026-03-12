package com.inventory;

import com.inventory.service.Database;

 //Entry point of the Inventory System prototype

public class Main {
    public static void main(String[] args) {
        Database.getInstance().init();
        System.out.println("Inventory System Prototype Running:");
    }
}
