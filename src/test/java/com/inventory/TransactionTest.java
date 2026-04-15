package com.inventory;

import com.inventory.model.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void constructor_storesAllFields() {
        Transaction t = new Transaction("tx-001", "item-001", "ADD", 10, "2026-04-14T10:00:00");
        assertEquals("tx-001", t.getId());
        assertEquals("item-001", t.getItemId());
        assertEquals("ADD", t.getType());
        assertEquals(10, t.getAmount());
        assertEquals("2026-04-14T10:00:00", t.getTimestamp());
    }

    @Test
    void constructor_negativeAmount_isAllowed() {
        Transaction t = new Transaction("tx-002", "item-002", "DELETE", -5, "2026-04-14T11:00:00");
        assertEquals(-5, t.getAmount());
    }

    @Test
    void constructor_zeroAmount_isAllowed() {
        Transaction t = new Transaction("tx-003", "item-003", "UPDATE", 0, "2026-04-14T12:00:00");
        assertEquals(0, t.getAmount());
    }

    @Test
    void getType_returnsCorrectType() {
        Transaction t = new Transaction("tx-004", "item-004", "UPDATE", 3, "2026-04-14T13:00:00");
        assertEquals("UPDATE", t.getType());
    }
}
