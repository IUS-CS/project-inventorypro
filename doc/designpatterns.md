# Design Patterns — InventoryPro

## Introduction

InventoryPro is a JavaFX desktop app backed by SQLite for tracking inventory items — name, category, quantity, and location. This document covers what we're done and possible future ideas we can implement

## Patterns We're done

**Database**
We only ever want one connection to the SQLite file. the older method wasteful and wasn't savind items. 

**Inventory**
InventoryService and Database were completely disconnected so this fix adds a Database parameter to InventoryService's constructor so it loads persisted items on startup and delegates every write to the database automatically. 

**Observer — `ObservableList` / `FilteredList`**
 Our `TableView` automatically reflects any changes to the list without us having to manually refresh it. 

## Patterns We're Thinking About Adding

`A Command pattern` could be used to show inventory operations such as:  
-Adding items   
-Removing items   
-Updating quantities   
This would make it easier for features like undo/redo functionality and maintain a history of actions.  

`A Factory Pattern`   
A Factory could be used to standardize the creation of inventory items or categories. This would simplify object creation and make it easier to extend the system with new item types.
