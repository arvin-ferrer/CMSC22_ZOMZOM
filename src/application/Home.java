package application;

import java.util.List; 
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image; 
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
// import javafx.scene.text.Font; 

public class Home {

    private Main mainApp;
    private StackPane rootPane; 
    private StackPane inventoryOverlay; 
    private StackPane shopOverlay;
    
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
        StackPane.setAlignment(returnButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(returnButton, new Insets(0, 50, 300, 0));
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

        StackPane.setAlignment(chestClickArea, Pos.TOP_RIGHT);
        // Margins: Top ~115px, Left ~600px 
        StackPane.setMargin(chestClickArea, new Insets(115, 350, 0, 0)); 
        
        rootPane.getChildren().add(chestClickArea);

        
        createInventoryOverlay(); 
        rootPane.getChildren().add(inventoryOverlay); 

        
        
        Pane shopClickArea = new Pane();
        shopClickArea.setPrefSize(20, 20);
        shopClickArea.setMaxSize(320, 250); // max size
        shopClickArea.setMinSize(320, 250); // min size
        shopClickArea.setStyle("-fx-background-color: transparent;"); // invisible
        shopClickArea.setStyle("-fx-background-color: rgba(255, 0, 0, 0.3);"); // comment this out to make invisible
        shopClickArea.setCursor(javafx.scene.Cursor.HAND);
      
        
        
        StackPane.setAlignment(shopClickArea, Pos.TOP_LEFT);
        StackPane.setMargin(shopClickArea, new Insets(50, 0, 0, 50)); 
        rootPane.getChildren().add(shopClickArea);
        createShopOverlay();
        rootPane.getChildren().add(shopOverlay);
        shopClickArea.setOnMouseClicked(e -> {
			showShop();
		});
        
        
        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("Safe House Interior");
        mainApp.getPrimaryStage().show();
    }

    private void createInventoryOverlay() {
        inventoryOverlay = new StackPane();
        inventoryOverlay.setVisible(false);
        inventoryOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        dimmer.setOnMouseClicked(e -> inventoryOverlay.setVisible(false));
        	
        StackPane inventoryContainer = new StackPane();
        inventoryContainer.setId("inventory-bg");
        inventoryContainer.setMaxSize(480, 640);
        inventoryContainer.setPrefSize(480, 640);

        GridPane inventoryGrid = new GridPane();
        inventoryGrid.setId("inventory-grid"); 
        
        int cols = 7;
        int rows = 5;
        int slotSize = 52; 

        List<InventoryItem> playerItems = mainApp.getCurrentPlayer().getInventory();
        
        if (playerItems == null || playerItems.isEmpty()) {
            System.out.println("Inventory empty (Old Save File detected). Adding starter pack...");
            playerItems.add(new InventoryItem("Medkit", "/assets/medkit.png", "Heals 50 HP"));
            playerItems.add(new InventoryItem("Grenade", "/assets/grenade-sprite.png", "Boom"));
    
        }
//         -------------------------------

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot"); 
                
                final int index = (y * cols) + x; 

                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    try {
                        System.out.println("Loading item: " + item.getName());
                        
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); 
                        itemIcon.setFitHeight(40);
                        itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(6);
                        itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                    } catch (Exception e) {
                        System.err.println("ERROR: Could not load image: " + item.getImagePath());
                    }
                }

                // Click Logic
                final List<InventoryItem> finalItemsList = playerItems;
                slot.setOnMouseClicked(e -> {
                    if (index < finalItemsList.size()) {
                        InventoryItem clickedItem = finalItemsList.get(index);
                        System.out.println("Selected: " + clickedItem.getName());
                    }
                });
                
                inventoryGrid.add(slot, x, y);
            }
        }

        StackPane.setAlignment(inventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(inventoryGrid, new Insets(250, 0, 0, 0)); 

        inventoryContainer.getChildren().add(inventoryGrid);
        
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> inventoryOverlay.setVisible(false));
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(0, 0, 0, 0));
        inventoryContainer.getChildren().add(closeButton);
        
        inventoryOverlay.getChildren().addAll(dimmer, inventoryContainer);
    }
    private void createShopOverlay() {
        shopOverlay = new StackPane();
        shopOverlay.setVisible(false); // Start hidden
        shopOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        
        dimmer.setOnMouseClicked(e -> shopOverlay.setVisible(false));
        
        StackPane shopContainer = new StackPane();
        shopContainer.setId("shop-bg"); // CSS ID
        shopContainer.setMaxSize(418, 370);
        shopContainer.setPrefSize(418, 370);

        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        
        closeButton.setOnAction(e -> shopOverlay.setVisible(false));
        
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));

        shopContainer.getChildren().add(closeButton);
        
        shopOverlay.getChildren().addAll(dimmer, shopContainer);
    }

    private void showInventory() {
        System.out.println("Opening Inventory...");
        inventoryOverlay.setVisible(true);
    }
    
    private void showShop() {
		System.out.println("Opening Shop...");
		shopOverlay.setVisible(true);
	}
    
    
}