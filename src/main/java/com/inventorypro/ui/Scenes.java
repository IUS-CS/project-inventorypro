package com.inventorypro.ui;

import com.inventory.model.Item;
import com.inventory.model.ItemFactory;
import com.inventory.service.Database;
import com.inventory.service.InventoryService;
import com.inventorypro.ui.validation.ItemValidator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.PrintWriter;

public class Scenes {

    private static final InventoryService service = new InventoryService(Database.getInstance());
    private static final ObservableList<Item> items = FXCollections.observableArrayList(service.listItems());

    public static Parent createDashboard(Stage stage) {
        Label title = new Label("Inventory Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label totalItemsLabel = new Label();
        Label lowStockLabel = new Label();
        Label totalQtyLabel = new Label();
        String statStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16; "
                + "-fx-background-color: #d9edf7; -fx-background-radius: 8; -fx-text-fill: #31708f;";
        String lowStockStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16; "
                + "-fx-background-color: #fcf8e3; -fx-background-radius: 8; -fx-text-fill: #8a6d3b;";
        totalItemsLabel.setStyle(statStyle);
        totalQtyLabel.setStyle(statStyle);
        lowStockLabel.setStyle(lowStockStyle + "-fx-cursor: hand;");

        Runnable updateSummary = () -> {
            int total = items.size();
            long lowStock = items.stream().filter(i -> i.getQuantity() < 5).count();
            int totalQty = items.stream().mapToInt(Item::getQuantity).sum();
            totalItemsLabel.setText("Total Items: " + total);
            lowStockLabel.setText("Low Stock: " + lowStock);
            totalQtyLabel.setText("Total Quantity: " + totalQty);
        };
        updateSummary.run();
        items.addListener((ListChangeListener<Item>) c -> updateSummary.run());

        HBox summaryBar = new HBox(12, totalItemsLabel, lowStockLabel, totalQtyLabel);
        summaryBar.setPadding(new Insets(4, 0, 8, 0));
        summaryBar.setStyle("-fx-border-color: #ddd; -fx-border-width: 0 0 1 0; -fx-padding: 6 0 10 0;");

        TextField search = new TextField();
        search.setPromptText("Search by name, category, or location...");

        FilteredList<Item> filtered = new FilteredList<>(items, p -> true);

        final boolean[] lowStockActive = { false };
        final Button[] showAllBtn = { null };
        String lowStockNormal = lowStockStyle + "-fx-cursor: hand;";
        String lowStockHighlight = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16; "
                + "-fx-background-color: #f2dede; -fx-background-radius: 8; -fx-text-fill: #a94442; -fx-cursor: hand;";

        lowStockLabel.setOnMouseClicked(ev -> {
            lowStockActive[0] = !lowStockActive[0];
            if (lowStockActive[0]) {
                lowStockLabel.setStyle(lowStockHighlight);
                search.clear();
                filtered.setPredicate(item -> item.getQuantity() < 5);
                showAllBtn[0].setVisible(true);
            } else {
                lowStockLabel.setStyle(lowStockNormal);
                filtered.setPredicate(item -> true);
                showAllBtn[0].setVisible(false);
            }
        });

        search.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = (newVal == null) ? "" : newVal.trim().toLowerCase();

            lowStockActive[0] = false;
            lowStockLabel.setStyle(lowStockNormal);
            showAllBtn[0].setVisible(false);

            if (lower.isEmpty()) {
                filtered.setPredicate(item -> true);
            } else {
                filtered.setPredicate(
                        item -> (item.getName() != null && item.getName().toLowerCase().contains(lower)) ||
                                (item.getCategory() != null && item.getCategory().toLowerCase().contains(lower)) ||
                                (item.getLocation() != null && item.getLocation().toLowerCase().contains(lower)));
            }
});

        SortedList<Item> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
        
        Label placeholder = new Label("No items yet. Click \"Add Item\" to get started.");
        search.textProperty()
                .addListener((obs, oldVal, newVal) -> placeholder.setText((newVal == null || newVal.trim().isEmpty())
                        ? "No items yet. Click \"Add Item\" to get started."
                        : "No items match \"" + newVal.trim() + "\"."));

        TableView<Item> table = new TableView<>(sorted);
        table.setPlaceholder(placeholder);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        table.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>() {
                @Override
                protected void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else if (item.getQuantity() < 5) {
                        setStyle("-fx-background-color: lightcoral;");
                    } else if (item.getQuantity() < 10) {
                        setStyle("-fx-background-color: lightyellow;");
                    } else {
                        setStyle("-fx-background-color: lightgreen;");
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    stage.getScene().setRoot(createEditItem(stage, row.getItem()));
                }
            });
            return row;
        });

        TableColumn<Item, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setSortable(true);

        TableColumn<Item, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colCategory.setSortable(true);

        TableColumn<Item, String> colLocation = new TableColumn<>("Location");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colLocation.setSortable(true);

        TableColumn<Item, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setSortable(true);
        colQty.setMaxWidth(90);
        colQty.setMinWidth(70);

        TableColumn<Item, Void> colAdj = new TableColumn<>("");
        colAdj.setSortable(false);
        colAdj.setMinWidth(80);
        colAdj.setMaxWidth(80);
        colAdj.setCellFactory(col -> new TableCell<>() {
            private final Button plus = new Button("+");
            private final Button minus = new Button("−");
            private final HBox box = new HBox(4, minus, plus);

            {
                plus.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    service.updateQuantity(item.getId(), item.getQuantity() + 1);
                    getTableView().refresh();
                });
                minus.setOnAction(e -> {
                    Item item = getTableView().getItems().get(getIndex());
                    if (item.getQuantity() > 0) {
                        service.updateQuantity(item.getId(), item.getQuantity() - 1);
                        getTableView().refresh();
                    }
                });
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        table.getColumns().addAll(colName, colCategory, colLocation, colQty, colAdj);

        Button goAdd = new Button("Add Item");
        goAdd.setOnAction(e -> stage.getScene().setRoot(createAddItem(stage)));

        Button edit = new Button("Edit Selected");
        edit.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                stage.getScene().setRoot(createEditItem(stage, selected));
            }
        });

        Button remove = new Button("Remove Selected");
        remove.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to remove \"" + selected.getName() + "\"?");
                confirm.setHeaderText("Confirm Delete");

                confirm.showAndWait().ifPresent(result -> {
                    if (result == ButtonType.OK) {
                        service.removeItem(selected.getId());
                        items.remove(selected);
                    }
                });
            }
        });

        Button clearSearch = new Button("Clear Search");
        clearSearch.setOnAction(e -> search.clear());

        Button exportCsv = new Button("Export CSV");
        exportCsv.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Inventory");
            chooser.setInitialFileName("inventory.csv");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                try (PrintWriter pw = new PrintWriter(file)) {
                    pw.println("ID,Name,Category,Quantity,Location");
                    for (Item item : items) {
                        pw.println(item.getId() + ","
                                + item.getName() + ","
                                + item.getCategory() + ","
                                + item.getQuantity() + ","
                                + item.getLocation());
                    }
                    Alert ok = new Alert(Alert.AlertType.INFORMATION);
                    ok.setHeaderText("Export Complete");
                    ok.setContentText("Exported " + items.size() + " items to " + file.getName());
                    ok.showAndWait();
                } catch (Exception ex) {
                    Alert err = new Alert(Alert.AlertType.ERROR);
                    err.setHeaderText("Export Failed");
                    err.setContentText(ex.getMessage());
                    err.showAndWait();
                }
            }
        });
    }
});

        Button goDelivery = new Button("Receive Delivery");
        goDelivery.setOnAction(e -> stage.getScene().setRoot(createDelivery(stage)));

        Button resetBtn = new Button("Reset Inventory");
        resetBtn.setOnAction(e -> {
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "This will reset all items back to the original stock levels. Continue?");
            confirm.setHeaderText("Reset Inventory");
            confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    Database.getInstance().resetToSeed();
                    service.reload();
                    items.setAll(service.listItems());
                }
            });
        });

        Button showAll = new Button("Show All Items");
        showAll.setVisible(false);
        showAll.setOnAction(e -> {
            lowStockActive[0] = false;
            lowStockLabel.setStyle(lowStockNormal);
            filtered.setPredicate(item -> true);
            showAll.setVisible(false);
        });
        showAllBtn[0] = showAll;

        HBox actions = new HBox(10, goAdd, edit, remove, clearSearch, exportCsv, goDelivery, resetBtn, showAll);
        actions.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(12, title, summaryBar, search, table, actions);
        root.setPadding(new Insets(18));
        return root;
    }

    public static Parent createAddItem(Stage stage) {
        Label title = new Label("Add Item");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField name = new TextField();
        name.setPromptText("Item name");

        ComboBox<String> category = new ComboBox<>(FXCollections.observableArrayList(
                "Grocery", "Electronics", "Clothing", "Toys", "Home & Garden",
                "Health & Beauty", "Sports", "Automotive", "Office Supplies", "Other"));
        category.setPromptText("Select a category");
        category.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> location = new ComboBox<>(FXCollections.observableArrayList(
                "Aisle 1 - Produce", "Aisle 2 - Dairy & Eggs", "Aisle 3 - Bakery",
                "Aisle 4 - Meat & Seafood", "Aisle 5 - Frozen Foods",
                "Aisle 6 - Beverages", "Aisle 7 - Snacks & Candy",
                "Aisle 8 - Household", "Aisle 9 - Electronics",
                "Aisle 10 - Clothing", "Aisle 11 - Toys & Games",
                "Aisle 12 - Sports & Outdoors", "Aisle 13 - Automotive"));
        location.setPromptText("Select a location");
        location.setMaxWidth(Double.MAX_VALUE);

        TextField quantity = new TextField();
        quantity.setPromptText("Quantity");

        Label status = new Label();
        status.setStyle("-fx-text-fill: red;");

        Button save = new Button("Save");
        save.setOnAction(e -> {
            String qText = quantity.getText().trim();
            int qValue;
            try {
                qValue = Integer.parseInt(qText);
            } catch (NumberFormatException ex) {
                status.setText("Quantity must be a whole number.");
                return;
            }

            String error = ItemValidator.validate(
                    name.getText(), category.getValue(), location.getValue(), qValue);
            if (error != null) {
                status.setText(error);
                return;
            }
            Item newItem = ItemFactory.createItem(
                    name.getText().trim(),
                    category.getValue(),
                    qValue,
                    location.getValue());

            service.addItem(newItem);
            items.add(newItem);

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setHeaderText("Item Added");
            success.setContentText("The item was added successfully.");
            success.showAndWait();

            stage.getScene().setRoot(createDashboard(stage));
        });

        Button back = new Button("Back to Dashboard");
        back.setOnAction(e -> stage.getScene().setRoot(createDashboard(stage)));

        GridPane form = new GridPane();
        form.setHgap(9);
        form.setVgap(9);
        form.add(new Label("Name:"), 0, 0);
        form.add(name, 1, 0);
        form.add(new Label("Category:"), 0, 1);
        form.add(category, 1, 1);
        form.add(new Label("Location:"), 0, 2);
        form.add(location, 1, 2);
        form.add(new Label("Quantity:"), 0, 3);
        form.add(quantity, 1, 3);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(90);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        HBox buttons = new HBox(10, save, back);

        VBox root = new VBox(12, title, form, status, buttons);
        root.setPadding(new Insets(18));
        return root;
    }

    public static Parent createEditItem(Stage stage, Item item) {
        Label title = new Label("Edit Item");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField name = new TextField(item.getName());

        ComboBox<String> category = new ComboBox<>(FXCollections.observableArrayList(
                "Grocery", "Electronics", "Clothing", "Toys", "Home & Garden",
                "Health & Beauty", "Sports", "Automotive", "Office Supplies", "Other"));
        category.setValue(item.getCategory());
        category.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> location = new ComboBox<>(FXCollections.observableArrayList(
                "Aisle 1 - Produce", "Aisle 2 - Dairy & Eggs", "Aisle 3 - Bakery",
                "Aisle 4 - Meat & Seafood", "Aisle 5 - Frozen Foods",
                "Aisle 6 - Beverages", "Aisle 7 - Snacks & Candy",
                "Aisle 8 - Household", "Aisle 9 - Electronics",
                "Aisle 10 - Clothing", "Aisle 11 - Toys & Games",
                "Aisle 12 - Sports & Outdoors", "Aisle 13 - Automotive"));
        location.setValue(item.getLocation());
        location.setMaxWidth(Double.MAX_VALUE);

        TextField quantity = new TextField(String.valueOf(item.getQuantity()));

        Label status = new Label();
        status.setStyle("-fx-text-fill: red;");

        Button save = new Button("Save Changes");
        save.setOnAction(e -> {
            String qText = quantity.getText().trim();
            int qValue;

            try {
                qValue = Integer.parseInt(qText);
            } catch (NumberFormatException ex) {
                status.setStyle("-fx-text-fill: red;");
                status.setText("Quantity must be a whole number.");
                return;
            }

            String error = ItemValidator.validate(
                    name.getText(),
                    category.getValue(),
                    location.getValue(),
                    qValue);

            if (error != null) {
                status.setStyle("-fx-text-fill: red;");
                status.setText(error);
                return;
            }

            Item updated = new Item(
                    item.getId(),
                    name.getText().trim(),
                    category.getValue(),
                    qValue,
                    location.getValue());

            service.updateItem(updated);

            int idx = items.indexOf(item);
            if (idx >= 0) {
                items.set(idx, updated);
            }

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setHeaderText("Item Updated");
            success.setContentText("The item was updated successfully.");
            success.showAndWait();

            stage.getScene().setRoot(createDashboard(stage));
});

        Button back = new Button("Cancel");
        back.setOnAction(e -> stage.getScene().setRoot(createDashboard(stage)));

        GridPane form = new GridPane();
        form.setHgap(9);
        form.setVgap(9);
        form.add(new Label("Name:"), 0, 0);
        form.add(name, 1, 0);
        form.add(new Label("Category:"), 0, 1);
        form.add(category, 1, 1);
        form.add(new Label("Location:"), 0, 2);
        form.add(location, 1, 2);
        form.add(new Label("Quantity:"), 0, 3);
        form.add(quantity, 1, 3);

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setMinWidth(90);
        ColumnConstraints c2 = new ColumnConstraints();
        c2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(c1, c2);

        HBox buttons = new HBox(10, save, back);

        VBox root = new VBox(12, title, form, status, buttons);
        root.setPadding(new Insets(18));
        return root;
    }

    public static Parent createDelivery(Stage stage) {
        Label title = new Label("Receive Truck Delivery");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label instructions = new Label("Pick an item to restock, enter how many you need, and add it to the order.");
        instructions.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        ComboBox<Item> itemPicker = new ComboBox<>(items);
        itemPicker.setPromptText("Choose an item to restock...");
        itemPicker.setMaxWidth(Double.MAX_VALUE);
        itemPicker.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + "  (" + item.getCategory() + ")");
            }
        });
        itemPicker.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName() + "  (" + item.getCategory() + ")");
            }
        });

        Label currentStockLabel = new Label("Current stock: —");
        currentStockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        itemPicker.setOnAction(e -> {
            Item sel = itemPicker.getValue();
            if (sel != null) {
                currentStockLabel.setText("Current stock: " + sel.getQuantity());
            }
        });

        TextField deliveryQty = new TextField();
        deliveryQty.setPromptText("How many?");
        deliveryQty.setPrefWidth(100);

        Label addStatus = new Label();
        addStatus.setStyle("-fx-text-fill: red;");

        ObservableList<String[]> manifest = FXCollections.observableArrayList();

        TableView<String[]> manifestTable = new TableView<>(manifest);
        manifestTable.setPlaceholder(new Label("No items added to this delivery yet."));
        manifestTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        manifestTable.setPrefHeight(200);

        TableColumn<String[], String> mColName = new TableColumn<>("Item");
        mColName.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue()[1]));

        TableColumn<String[], String> mColOld = new TableColumn<>("In Stock");
        mColOld.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue()[2]));
        mColOld.setMaxWidth(80);

        TableColumn<String[], String> mColQty = new TableColumn<>("Restocking");
        mColQty.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue()[3]));
        mColQty.setMaxWidth(80);

        TableColumn<String[], String> mColNew = new TableColumn<>("New Total");
        mColNew.setCellValueFactory(cd -> new javafx.beans.property.SimpleStringProperty(cd.getValue()[4]));
        mColNew.setMaxWidth(80);

        manifestTable.getColumns().addAll(mColName, mColOld, mColQty, mColNew);

        Label manifestCount = new Label("Delivery Order: 0 items");
        manifestCount.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        Button addToManifest = new Button("Add to Order");
        addToManifest.setOnAction(e -> {
            Item sel = itemPicker.getValue();
            if (sel == null) {
                addStatus.setText("Pick an item first.");
                return;
            }
            String qText = deliveryQty.getText().trim();
            if (qText.isEmpty()) {
                addStatus.setText("Enter how many you need.");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(qText);
            } catch (NumberFormatException ex) {
                addStatus.setText("Quantity must be a whole number.");
                return;
            }
            if (qty <= 0) {
                addStatus.setText("Quantity must be greater than 0.");
                return;
            }

            manifest.add(new String[] {
                    sel.getId(),
                    sel.getName(),
                    String.valueOf(sel.getQuantity()),
                    String.valueOf(qty),
                    String.valueOf(sel.getQuantity() + qty)
            });

            addStatus.setText("");
            manifestCount.setText("Delivery Order: " + manifest.size() + " item(s)");
            deliveryQty.clear();
            itemPicker.setValue(null);
            currentStockLabel.setText("Current stock: —");
        });

        HBox pickerRow = new HBox(10, itemPicker, deliveryQty, addToManifest);
        pickerRow.setPadding(new Insets(4, 0, 4, 0));
        HBox.setHgrow(itemPicker, Priority.ALWAYS);

        Button processDelivery = new Button("Process Delivery");
        processDelivery.setStyle("-fx-font-weight: bold;");
        processDelivery.setOnAction(e -> {
            if (manifest.isEmpty()) {
                addStatus.setText("Add items to the manifest first.");
                return;
            }
            int count = 0;
            for (String[] row : manifest) {
                String id = row[0];
                int qty = Integer.parseInt(row[3]);
                try {
                    Item item = service.findItemById(id);
                    service.updateQuantity(id, item.getQuantity() + qty);
                    count++;
                } catch (IllegalArgumentException ignored) {
                }
            }
            items.setAll(service.listItems());

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setHeaderText("Delivery Processed");
            ok.setContentText("Received stock for " + count + " item(s).");
            ok.showAndWait();
            stage.getScene().setRoot(createDashboard(stage));
        });

        Button clearManifest = new Button("Clear Order");
        clearManifest.setOnAction(e -> {
            manifest.clear();
            manifestCount.setText("Delivery Order: 0 items");
        });

        Button back = new Button("Back to Dashboard");
        back.setOnAction(e -> stage.getScene().setRoot(createDashboard(stage)));

        HBox buttons = new HBox(10, processDelivery, clearManifest, back);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(10, title, instructions, pickerRow, currentStockLabel, addStatus,
                manifestCount, manifestTable, buttons);
        root.setPadding(new Insets(18));
        return root;
    }
}
