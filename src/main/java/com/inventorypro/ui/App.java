package com.inventorypro.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public void start(Stage stage) {
        stage.setTitle("InventoryPro");
        Scene scene = new Scene(Scenes.createDashboard(stage), 900, 550);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
