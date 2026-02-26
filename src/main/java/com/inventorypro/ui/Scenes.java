package com.inventorypro.ui;

import com.inventory.model.Item;
import com.inventory.service.Database;
import com.inventorypro.ui.validation.ItemValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.UUID;

public class Scenes {

    private static final ObservableList<Item> items = FXCollections.observableArrayList();

    static {
        Database.init();
        items.addAll(Database.loadAll());
    }

    public static Parent createDashboard(Stage stage) {
        Label title = new Label("Inventory Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TableView<Item> table = new TableView<>(items);
        table.setPlaceholder(new Label("No items yet. Click \"Add Item\" to get started."));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<Item, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Item, String> colCategory = new TableColumn<>("Category");
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Item, String> colLocation = new TableColumn<>("Location");
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));

        TableColumn<Item, Integer> colQty = new TableColumn<>("Quantity");
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setMaxWidth(90);
        colQty.setMinWidth(70);

        table.getColumns().add(colName);
        table.getColumns().add(colCategory);
        table.getColumns().add(colLocation);
        table.getColumns().add(colQty);

        Button goAdd = new Button("Add Item");
        goAdd.setOnAction(e -> stage.getScene().setRoot(createAddItem(stage)));

        Button remove = new Button("Remove Selected");
        remove.setOnAction(e -> {
            Item selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Database.delete(selected.getId());
                items.remove(selected);
            } else {
                new Alert(Alert.AlertType.WARNING, "Please select an item to remove.").showAndWait();
            }
        });

        HBox actions = new HBox(10, goAdd, remove);
        actions.setPadding(new Insets(10, 0, 0, 0));

        VBox root = new VBox(12, title, table, actions);
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
                "Health & Beauty", "Sports", "Automotive", "Office Supplies", "Other"
        ));
        category.setPromptText("Select a category");
        category.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> location = new ComboBox<>(FXCollections.observableArrayList(
                "Aisle 1 - Produce", "Aisle 2 - Dairy & Eggs", "Aisle 3 - Bakery",
                "Aisle 4 - Meat & Seafood", "Aisle 5 - Frozen Foods",
                "Aisle 6 - Beverages", "Aisle 7 - Snacks & Candy",
                "Aisle 8 - Household", "Aisle 9 - Electronics",
                "Aisle 10 - Clothing", "Aisle 11 - Toys & Games",
                "Aisle 12 - Sports & Outdoors", "Aisle 13 - Automotive"
        ));
        location.setPromptText("Select a location");
        location.setMaxWidth(Double.MAX_VALUE);

        TextField quantity = new TextField();
        quantity.setPromptText("Quantity");

        Label status = new Label();
        status.setStyle("-fx-text-fill: #b00020;");

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
            // Save
            String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Item newItem = new Item(id, name.getText().trim(), category.getValue(),
                    qValue, location.getValue());
            Database.insert(newItem);
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
}
