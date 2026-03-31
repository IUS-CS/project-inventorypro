# Architecture  
## InventoryPro Architecture

InventoryPro is a relatively straightforward desktop application, so the architecture is designed to remain simple while still maintaining clear separation of concerns. Overcomplicating the structure would increase development difficulty without providing meaningful benefits.

The system is divided into three primary layers:

- UI Layer  
- Service Layer  
- Database Layer  

---

## UI Layer

The UI layer is built using JavaFX and is responsible for presenting data and handling user interaction.

It includes components such as:

- TableView (displays inventory items)  
- Input fields (name, category, quantity, location)  
- Buttons (add, update, delete)  

The UI layer communicates exclusively with the InventoryService to perform operations and does not directly interact with the database.

Additionally, the UI leverages JavaFX’s ObservableList, which automatically updates the TableView when the underlying data changes. This eliminates the need for manual refresh logic and keeps the interface responsive.

---

## Service Layer (InventoryService)

The service layer contains the core business logic of the application.

Its responsibilities include:

- Managing inventory items in memory  
- Handling add, update, and delete operations  
- Loading persisted data on application startup  
- Synchronizing changes with the database  

The InventoryService acts as an intermediary between the UI and the database layer. This abstraction ensures that business logic remains independent of both presentation and persistence concerns.

---

## Database Layer

The database layer is responsible for persistent storage using SQLite.

It includes:

- A Database class for managing the database connection  
- SQL operations (INSERT, SELECT, UPDATE, DELETE)  

This layer follows the Singleton pattern to ensure that only one database connection exists throughout the application lifecycle. This improves performance and avoids potential data consistency issues.

---

## System Interaction

The system follows a clear and linear interaction flow:

1. The UI sends user actions to the InventoryService  
2. The InventoryService processes the request  
3. The Database layer persists or retrieves data  
4. The UI updates automatically via ObservableList 

---

## Data Flow Example

**Adding an Inventory Item:**

1. User enters item details in the UI  
2. UI calls InventoryService.addItem()  
3. InventoryService updates the in-memory list  
4. InventoryService calls the Database layer to persist the item  
5. Database writes the data to SQLite  
6. ObservableList updates the TableView automatically  
