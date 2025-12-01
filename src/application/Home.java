package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
// import javafx.scene.text.Font; 

public class Home {

    private Main mainApp;
    private StackPane rootPane; 
    private StackPane inventoryOverlay; 

    public Home(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void showScreen() {
        // root pane with background
        rootPane = new StackPane();
        rootPane.setId("home-screen-background"); 
        rootPane.setPrefSize(1280, 720);

        // return to battle button
        Button returnButton = new Button("RETURN TO BATTLE");
        returnButton.getStyleClass().add("dashboard-button"); 
        returnButton.setOnAction(e -> {
            System.out.println("Exiting safe house...");
            mainApp.showWarAreaScreen();
        });
        StackPane.setAlignment(returnButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(returnButton, new Insets(0, 0, 50, 0));
        rootPane.getChildren().add(returnButton);

        // clickable area of chest
        Pane chestClickArea = new Pane();
//        chestClickArea.setPrefSize(20, 20);
        chestClickArea.setMaxSize(320, 250); // max size
        chestClickArea.setMinSize(320, 250); // min size
        chestClickArea.setStyle("-fx-background-color: transparent;"); // invisible
        chestClickArea.setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);"); // comment this out to make invisible
        chestClickArea.setCursor(javafx.scene.Cursor.HAND);

        // show inventory
        chestClickArea.setOnMouseClicked(e -> {
            showInventory();
        });

        StackPane.setAlignment(chestClickArea, Pos.TOP_LEFT);
        // Margins: Top ~115px, Left ~600px 
        StackPane.setMargin(chestClickArea, new Insets(115, 0, 0, 600)); 
        
        rootPane.getChildren().add(chestClickArea);


        createInventoryOverlay(); 
        rootPane.getChildren().add(inventoryOverlay); 

        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - Safe House Interior");
        mainApp.getPrimaryStage().show();
    }

    private void createInventoryOverlay() {
        inventoryOverlay = new StackPane();
        inventoryOverlay.setVisible(false); // Start hidden
        inventoryOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        dimmer.setOnMouseClicked(e -> inventoryOverlay.setVisible(false));
        
        StackPane inventoryContainer = new StackPane();
        inventoryContainer.setId("inventory-bg"); // css id
        inventoryContainer.setMaxSize(480, 640); // size of image
        inventoryContainer.setPrefSize(480, 640);

        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> inventoryOverlay.setVisible(false));
        
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));

        inventoryContainer.getChildren().add(closeButton);
        
        inventoryOverlay.getChildren().addAll(dimmer, inventoryContainer);
    }

    private void showInventory() {
        System.out.println("Opening Inventory...");
        inventoryOverlay.setVisible(true);
    }
}