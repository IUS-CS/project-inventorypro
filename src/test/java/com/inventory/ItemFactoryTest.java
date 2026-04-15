package com.inventory;

import com.inventory.model.Item;
import com.inventory.model.ItemFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemFactoryTest {

    @Test
    void createItem_returnsItemWithCorrectFields() {
        Item item = ItemFactory.createItem("Apples", "Produce", 50, "Aisle 3");
        assertEquals("Apples", item.getName());
        assertEquals("Produce", item.getCategory());
        assertEquals(50, item.getQuantity());
        assertEquals("Aisle 3", item.getLocation());
    }

    @Test
    void createItem_generatesNonNullId() {
        Item item = ItemFactory.createItem("Oranges", "Produce", 10, "Aisle 4");
        assertNotNull(item.getId());
        assertFalse(item.getId().isBlank());
    }

    @Test
    void createItem_generatesEightCharId() {
        Item item = ItemFactory.createItem("Milk", "Dairy", 20, "Cooler 1");
        // ID is first 8 chars of a UUID in uppercase
        assertEquals(8, item.getId().length());
    }

    @Test
    void createItem_twoCalls_produceDifferentIds() {
        Item a = ItemFactory.createItem("Bread", "Bakery", 5, "Shelf 2");
        Item b = ItemFactory.createItem("Butter", "Dairy", 3, "Cooler 2");
        assertNotEquals(a.getId(), b.getId());
    }

    @Test
    void createItem_zeroQuantity_isAllowed() {
        Item item = ItemFactory.createItem("Salt", "Condiments", 0, "Shelf 9");
        assertEquals(0, item.getQuantity());
    }
}
