# Organization — InventoryPro

## Team Members

- Dustin De Luna
- Jared Miller
- Nnaemeka Onochie

## Repo Structure

```
project-inventorypro/
├── doc/
│ ├── designpatterns.md # Design pattern decisions and plans
│ ├── organization.md # Team structure and repo layout
│ ├── proposal.md # Original project proposal
│ ├── sprint1.md # Sprint 1 ceremony minutes
│ └── TODO.md # Sprint task tracking
├── images/ # Screenshots
├── src/
│ ├── main/java/com/inventory/
│ │ ├── Main.java # Application bootstrap
│ │ ├── model/
│ │ │ ├── Item.java # Core data model (includes low stock logic)
│ │ │ ├── ItemFactory.java # Factory for creating Items
│ │ │ └── Transaction.java # Tracks inventory changes
│ │ └── service/
│ │ ├── Database.java # SQLite database + transaction logging
│ │ └── InventoryService.java # Business logic + persistence layer
│ └── main/java/com/inventorypro/
│ ├── model/ # (reserved for future models)
│ └── ui/
│ ├── App.java # JavaFX entry point
│ ├── Scenes.java # Dashboard, Add, Edit, and History screens
│ └── validation/
│ └── ItemValidator.java # Input validation rules
├── src/test/java/com/inventory/
│ ├── InventoryServiceTest.java # Unit tests for InventoryService
│ └── MainTest.java # Smoke test for Main
├── AUTHORS
├── LICENSE
├── .gitignore
├── pom.xml
└── README.md
```
## Team Roles

**Dustin De Luna — Frontend Developer**
Responsible for the database and front end

**Jared Miller — Tester**
Responsible for writing and running unit tests.

**Nnaemeka Onochie — Full-Stack Developer & Project Lead**
Responsible for overall project coordination, connecting the frontend and backend, managing the repository, conducting code reviews, and handling deployment.
