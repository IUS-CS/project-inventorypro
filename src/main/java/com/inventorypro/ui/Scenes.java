package com.inventorypro.ui;

import com.inventory.model.Item;
import com.inventory.model.ItemFactory;
import com.inventory.service.Database;
import com.inventory.service.InventoryService;
import com.inventorypro.ui.validation.ItemValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Scenes {

    private static final InventoryService service = new InventoryService(Database.getInstance());
    private static final ObservableList<Item> items = FXCollections.observableArrayList(service.listItems());

    public static Parent createDashboard(Stage stage) {
        Label title = new Label("Inventory Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField search = new TextField();
        search.setPromptText("Search by name, category, or location...");

        FilteredList<Item> filtered = new FilteredList<>(items, p -> true);
        search.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = (newVal == null) ? "" : newVal.trim().toLowerCase();
            if (lower.isEmpty()) {
                filtered.setPredicate(null);
            } else {
                filtered.setPredicate(item -> item.getName().toLowerCase().contains(lower)
                        || item.getCategory().toLowerCase().contains(lower)
                        || item.getLocation().toLowerCase().contains(lower));
            }
        });

        SortedList<Item> sorted = new SortedList<>(filtered);

        Label placeholder = new Label("No items yet. Click \"Add Item\" to get started.");
        search.textProperty()
                .addListener((obs, oldVal, newVal) -> placeholder.setText((newVal == null || newVal.trim().isEmpty())
                        ? "No items yet. Click \"Add Item\" to get started."
                        : "No items match \"" + newVal.trim() + "\"."));

        TableView<Item> table = new TableView<>(sorted);
        table.setPlaceholder(placeholder);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        table.setRowFactory(tv -> new TableRow<>() {
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
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select an item to edit.").showAndWait();
            }
        });

        Button remove = new Button("Remove Selected");
        remove.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.removeItem(selected.getId());
                items.remove(selected);
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select an item to remove.").showAndWait();
            }
        });

        HBox actions = new HBox(10, goAdd, edit, remove);
        actions.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(12, title, search, table, actions);
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
            Item newItem = ItemFactory.createItem(name.getText().trim(), category.getValue(),
                    qValue, location.getValue());
            service.addItem(newItem);
            items.add(newItem);
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
                status.setText("Quantity must be a whole number.");
                return;
            }

            String error = ItemValidator.validate(
                    name.getText(), category.getValue(), location.getValue(), qValue);
            if (error != null) {
                status.setText(error);
                return;
            }

            Item updated = new Item(item.getId(), name.getText().trim(),
                    category.getValue(), qValue, location.getValue());
            service.updateItem(updated);
            int idx = items.indexOf(item);
            if (idx >= 0) {
                items.set(idx, updated);
            }
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
}
