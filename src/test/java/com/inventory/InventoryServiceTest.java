package com.inventory;

import com.inventory.model.Item;
import com.inventory.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link InventoryService}.
 *
 * <p>
 * Each test creates a fresh service instance via {@code setUp()} so tests
 * are fully independent of one another.
 */
public class InventoryServiceTest {

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService();
    }

    @Test
    void addItem_shouldIncreaseSize() {
        service.addItem(new Item("A-001", "Widget", "General", 5, "Shelf 1"));
        assertEquals(1, service.size());
    }

    @Test
    void addItem_shouldBeRetrievableById() {
        service.addItem(new Item("A-002", "Gadget", "Electronics", 3, "Bin 4"));
        Item found = service.findItemById("A-002");
        assertEquals("Gadget", found.getName());
        assertEquals("Electronics", found.getCategory());
        assertEquals(3, found.getQuantity());
    }

    @Test
    void addItem_duplicateId_shouldThrow() {
        service.addItem(new Item("A-003", "Widget", "General", 1, "Shelf 2"));
        assertThrows(IllegalArgumentException.class,
                () -> service.addItem(new Item("A-003", "Other Widget", "General", 1, "Shelf 3")));
    }

    @Test
    void addItem_nullItem_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.addItem(null));
    }

    @Test
    void findItemById_missingId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.findItemById("DOES-NOT-EXIST"));
    }

    @Test
    void updateQuantity_shouldChangeQuantity() {
        service.addItem(new Item("B-001", "Cable", "Electronics", 10, "Drawer A"));
        service.updateQuantity("B-001", 4);
        assertEquals(4, service.findItemById("B-001").getQuantity());
    }

    @Test
    void updateQuantity_toZero_shouldBeAllowed() {
        service.addItem(new Item("B-002", "Pen", "Office", 5, "Desk"));
        service.updateQuantity("B-002", 0);
        assertEquals(0, service.findItemById("B-002").getQuantity());
    }

    @Test
    void updateQuantity_negativeValue_shouldThrow() {
        service.addItem(new Item("B-003", "Tape", "Office", 8, "Drawer B"));
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity("B-003", -1));
    }

    @Test
    void updateQuantity_unknownId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity("FAKE-999", 5));
    }

    @Test
    void removeItem_shouldDecreaseSizeByOne() {
        service.addItem(new Item("C-001", "Marker", "Office", 20, "Supply Room"));
        service.removeItem("C-001");
        assertEquals(0, service.size());
    }

    @Test
    void removeItem_shouldMakeItemUnfindable() {
        service.addItem(new Item("C-002", "Stapler", "Office", 3, "Shelf 5"));
        service.removeItem("C-002");
        assertThrows(IllegalArgumentException.class, () -> service.findItemById("C-002"));
    }

    @Test
    void removeItem_unknownId_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> service.removeItem("NO-SUCH-ID"));
    }

    @Test
    void listItems_emptyService_returnsEmptyList() {
        List<Item> items = service.listItems();
        assertTrue(items.isEmpty());
    }

    @Test
    void listItems_returnsAllAddedItems() {
        service.addItem(new Item("D-001", "Hammer", "Tools", 2, "Tool Chest"));
        service.addItem(new Item("D-002", "Wrench", "Tools", 4, "Tool Chest"));
        assertEquals(2, service.listItems().size());
    }

    @Test
    void itemConstructor_negativeQuantity_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Item("E-001", "Bad Item", "General", -10, "Nowhere"));
    }
}
