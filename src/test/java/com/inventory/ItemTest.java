package com.inventory;

import com.inventory.model.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {

    @Test
    void constructor_storesAllFields() {
        Item item = new Item("X-001", "Bolt", "Hardware", 10, "Bin A");
        assertEquals("X-001", item.getId());
        assertEquals("Bolt", item.getName());
        assertEquals("Hardware", item.getCategory());
        assertEquals(10, item.getQuantity());
        assertEquals("Bin A", item.getLocation());
    }

    @Test
    void constructor_zeroQuantity_isAllowed() {
        Item item = new Item("X-002", "Nut", "Hardware", 0, "Bin B");
        assertEquals(0, item.getQuantity());
    }

    @Test
    void constructor_negativeQuantity_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new Item("X-003", "Screw", "Hardware", -1, "Bin C"));
    }

    @Test
    void setQuantity_updatesValue() {
        Item item = new Item("X-004", "Washer", "Hardware", 5, "Bin D");
        item.setQuantity(20);
        assertEquals(20, item.getQuantity());
    }

    @Test
    void setQuantity_toZero_isAllowed() {
        Item item = new Item("X-005", "Rivet", "Hardware", 3, "Bin E");
        item.setQuantity(0);
        assertEquals(0, item.getQuantity());
    }

    @Test
    void setQuantity_negativeValue_throws() {
        Item item = new Item("X-006", "Pin", "Hardware", 5, "Bin F");
        assertThrows(IllegalArgumentException.class, () -> item.setQuantity(-5));
    }

    @Test
    void toString_containsIdAndName() {
        Item item = new Item("X-007", "Gear", "Mechanical", 2, "Shelf G");
        String str = item.toString();
        assertTrue(str.contains("X-007"));
        assertTrue(str.contains("Gear"));
    }
}
