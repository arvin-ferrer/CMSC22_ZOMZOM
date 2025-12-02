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
    private StackPane craftingOverlay;
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
      
        Pane craftingClickArea = new Pane();
        craftingClickArea.setMaxSize(400, 150); 
        craftingClickArea.setMinSize(400, 150);
        craftingClickArea.setStyle("-fx-background-color: transparent;");
         craftingClickArea.setStyle("-fx-background-color: rgba(0, 255, 0, 0.3);"); // Debug: Green box
        craftingClickArea.setCursor(javafx.scene.Cursor.HAND);

        craftingClickArea.setOnMouseClicked(e -> showCrafting());

        StackPane.setAlignment(craftingClickArea, Pos.BOTTOM_CENTER);
        StackPane.setMargin(craftingClickArea, new Insets(0, 0, 0, 0)); 
        
        rootPane.getChildren().add(craftingClickArea);
        
        createCraftingOverlay();
        rootPane.getChildren().add(craftingOverlay);
        
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
        shopOverlay.setVisible(false); // start hidden
        shopOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        dimmer.setOnMouseClicked(e -> shopOverlay.setVisible(false));
        
        StackPane shopContainer = new StackPane();
        shopContainer.setId("shop-bg"); // CSS ID
        shopContainer.setMaxSize(480, 640);
        shopContainer.setPrefSize(480, 640);

        // shop grid
        GridPane shopGrid = new GridPane();
        shopGrid.setId("shop-grid"); 
        
        int cols = 4;
        int rows = 5;
        int slotSize = 52; 

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot"); // reuse the inventory slot style
                // click logic
                final int finalX = x;
                final int finalY = y;
                slot.setOnMouseClicked(e -> {
                    System.out.println("Clicked Shop Slot: " + finalX + "," + finalY);
                    // add logic to buy item here
                });
                
                shopGrid.add(slot, x, y);
            }
        }

        StackPane.setAlignment(shopGrid, Pos.BOTTOM_CENTER);
        // Top, Right, Bottom, Left
        StackPane.setMargin(shopGrid, new Insets(220, 0, 85, 54)); 

        shopContainer.getChildren().add(shopGrid);
        
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> shopOverlay.setVisible(false));
        
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(145, 40, 0, 0));

        shopContainer.getChildren().add(closeButton);
        shopOverlay.getChildren().addAll(dimmer, shopContainer);
    }
    
    private void createCraftingOverlay() {
        craftingOverlay = new StackPane();
        craftingOverlay.setVisible(false);
        craftingOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        dimmer.setOnMouseClicked(e -> craftingOverlay.setVisible(false));
        
        StackPane craftingContainer = new StackPane();
        craftingContainer.setId("crafting-bg"); // CSS ID for the new image
        craftingContainer.setMaxSize(480, 640); // 480x640 portrait orientation
        craftingContainer.setPrefSize(480, 640);

        // --- 1. Crafting Input Grid (3x3) ---
        GridPane inputGrid = new GridPane();
        // inputGrid.setGridLinesVisible(true);
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(60, 60); // Slots for 3x3
                slot.getStyleClass().add("inventory-slot");
                inputGrid.add(slot, x, y);
            }
        }
        
        StackPane.setAlignment(inputGrid, Pos.TOP_LEFT);
        // Adjust these margins to fit the 3x3 grid in your generated image
        StackPane.setMargin(inputGrid, new Insets(50, 0, 0, 40)); 
        
        craftingContainer.getChildren().add(inputGrid);

        // --- 2. Output Slot (1x1) ---
        Pane outputSlot = new Pane();
        outputSlot.setPrefSize(60, 60);
        outputSlot.getStyleClass().add("inventory-slot");
        
        StackPane.setAlignment(outputSlot, Pos.TOP_RIGHT);
        // Adjust to fit the single box on the right of the image
        StackPane.setMargin(outputSlot, new Insets(110, 60, 0, 0));
        
        craftingContainer.getChildren().add(outputSlot);
        
        // --- 3. Inventory Grid (Bottom half) ---
        // Reuse logic to show player inventory so they can drag items up
        GridPane inventoryGrid = new GridPane();
        int cols = 7;
        int rows = 5;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(52, 52);
                slot.getStyleClass().add("inventory-slot");
                inventoryGrid.add(slot, x, y);
            }
        }
        StackPane.setAlignment(inventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(inventoryGrid, new Insets(0, 0, 35, 0));
        craftingContainer.getChildren().add(inventoryGrid);


        // Close Button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> craftingOverlay.setVisible(false));
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));
        craftingContainer.getChildren().add(closeButton);
        
        craftingOverlay.getChildren().addAll(dimmer, craftingContainer);
    }
    private void showInventory() {
        System.out.println("Opening Inventory...");
        inventoryOverlay.setVisible(true);
    }
    
    private void showShop() {
		System.out.println("Opening Shop...");
		shopOverlay.setVisible(true);
	}
    private void showCrafting() {
        System.out.println("Opening Crafting Table...");
        craftingOverlay.setVisible(true);
    }
    
    
}