package com.inventorypro.ui.validation;

public class ItemValidator {

    public static String validate(String name, String category, String location, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            return "Name must not be empty.";
        }
        if (category == null || category.trim().isEmpty()) {
            return "Category must not be empty.";
        }
        if (location == null || location.trim().isEmpty()) {
            return "Location must not be empty.";
        }
        if (quantity < 0) {
            return "Quantity must not be negative.";
        }
        return null;
    }
}
