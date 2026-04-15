
Each test focuses on validating a specific component of the system.

---

## Test Coverage

### 1. Item Tests
File: `ItemTest.java`

Tests:
- Creating items
- Getting item properties (name, quantity)
- Updating quantity

Expected:
- Items initialize correctly
- Quantity updates correctly

---

### 2. Inventory Service Tests
File: `InventoryServiceTest.java`

Tests:
- Adding items
- Removing items
- Updating quantities
- Finding items by ID

Expected:
- Items are correctly added/removed
- Quantity updates reflect properly

---

### 3. Transaction Tests
File: `TransactionTest.java`

Tests:
- Processing transactions
- Validating transaction behavior

Expected:
- Transactions update inventory correctly

---

### 4. Item Factory Tests
File: `ItemFactoryTest.java`

Tests:
- Creating different item types

Expected:
- Correct objects are created

---

### 5. Main Test
File: `MainTest.java`

Tests:
- Application startup

Expected:
- Program initializes without crashing

---

## How to Run Tests

Run:
```bash
mvn test
