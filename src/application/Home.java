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
 // Simple class to define items sold in the shop
    private class ShopItem {
        String name;
        int price;
        String imagePath;
        String description;

        public ShopItem(String name, int price, String imagePath, String description) {
            this.name = name;
            this.price = price;
            this.imagePath = imagePath;
            this.description = description;
        }
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

        // this is for displaying selected item info
        Pane displaySlot = new Pane();
        displaySlot.setPrefSize(180, 180); // Size of the big box area
        displaySlot.setMaxSize(180, 180);
        // displaySlot.setStyle("-fx-background-color: rgba(255,0,0,0.3);"); // Debug red box
        ImageView selectedItemView = new ImageView();
        selectedItemView.setFitWidth(120); // Make it big
        selectedItemView.setFitHeight(120);
        selectedItemView.setPreserveRatio(true);
        selectedItemView.setLayoutX(30); // Center it in the 180 pane
        selectedItemView.setLayoutY(30);
        
        displaySlot.getChildren().add(selectedItemView);

        // Position Top-Left
        StackPane.setAlignment(displaySlot, Pos.TOP_LEFT);
        StackPane.setMargin(displaySlot, new Insets(60, 0, 0, 30));
        inventoryContainer.getChildren().add(displaySlot);

      
        javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox(10); // 10px spacing
        infoBox.setMaxSize(200, 180);
        infoBox.setAlignment(Pos.TOP_LEFT);
        // infoBox.setStyle("-fx-background-color: rgba(0,0,255,0.3);"); // Debug blue box
        
        // Label for Item Name
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("SELECT ITEM");
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 24px; -fx-text-fill: #3e2723; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);

        // Label for Description
        javafx.scene.control.Label descLabel = new javafx.scene.control.Label("Click an item to see details.");
        descLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #5d4037; -fx-font-weight: bold;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);

        infoBox.getChildren().addAll(nameLabel, descLabel);

        // Position Top-Right (next to the big box)
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(65, 0, 0, 32));
        inventoryContainer.getChildren().add(infoBox);

        GridPane inventoryGrid = new GridPane();
        inventoryGrid.setId("inventory-grid"); 
        
        int cols = 7;
        int rows = 5;
        int slotSize = 52; 

        List<InventoryItem> playerItems = mainApp.getCurrentPlayer().getInventory();
        if (playerItems == null) playerItems = new java.util.ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot"); 
                
                final int index = (y * cols) + x; 

                // Render Item Icon in Slot
                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); 
                        itemIcon.setFitHeight(40);
                        itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(6);
                        itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                    } catch (Exception e) {}
                }

                // --- CLICK LOGIC TO UPDATE DISPLAY ---
                final List<InventoryItem> finalItemsList = playerItems;
                
                slot.setOnMouseClicked(e -> {
                    if (index < finalItemsList.size()) {
                        InventoryItem clickedItem = finalItemsList.get(index);
                        
                        nameLabel.setText(clickedItem.getName().toUpperCase());
                        
                        descLabel.setText(clickedItem.getDescription());
                        
                        try {
                            selectedItemView.setImage(new Image(getClass().getResourceAsStream(clickedItem.getImagePath())));
                        } catch (Exception ex) {
                            System.out.println("Error loading display image");
                        }
                    } else {
                        nameLabel.setText("");
                        descLabel.setText("");
                        selectedItemView.setImage(null);
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
 // Field to track what the user is trying to buy
    private ShopItem currentSelectedShopItem = null;

    private void createShopOverlay() {
        shopOverlay = new StackPane();
        shopOverlay.setVisible(false);
        shopOverlay.setAlignment(Pos.CENTER);
        
        StackPane dimmer = new StackPane();
        dimmer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        dimmer.setOnMouseClicked(e -> shopOverlay.setVisible(false));
        
        StackPane shopContainer = new StackPane();
        shopContainer.setId("shop-bg");
        shopContainer.setMaxSize(480, 640);
        shopContainer.setPrefSize(480, 640);

        // ====================================================================
        // 1. DEFINE SHOP ITEMS
        // ====================================================================
        java.util.List<ShopItem> shopList = new java.util.ArrayList<>();
        // Note: You can change the prices and paths here
        shopList.add(new ShopItem("Wood", 50, "/assets/wood.png", "Construction material"));
        shopList.add(new ShopItem("Cloth", 30, "/assets/whiteCloth.png", "For crafting bandages"));
        shopList.add(new ShopItem("Gunpowder", 100, "/assets/gunpowder.png", "Explosive component"));
        shopList.add(new ShopItem("Rock", 20, "/assets/stone.png", "Basic resource"));
        shopList.add(new ShopItem("Mallet", 500, "/assets/mallet.png", "Melee weapon"));
        shopList.add(new ShopItem("Katana", 1500, "/assets/katana.png", "Sharp blade"));
        shopList.add(new ShopItem("Machine Gun", 5000, "/assets/machinegun.png", "Rapid fire"));

        // ====================================================================
        // 2. SELECTED ITEM DISPLAY (Top Area)
        // ====================================================================
        
        // Image of selected item
        ImageView selectedItemView = new ImageView();
        selectedItemView.setFitWidth(80);
        selectedItemView.setFitHeight(80);
        selectedItemView.setPreserveRatio(true);
        
        StackPane.setAlignment(selectedItemView, Pos.TOP_RIGHT);

        StackPane.setMargin(selectedItemView, new Insets(180, 70, 0, 20)); 
        shopContainer.getChildren().add(selectedItemView);

        // Info Text (Name & Price)
        javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox(5);
        infoBox.setAlignment(Pos.TOP_RIGHT);
        
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("SELECT ITEM");
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20px; -fx-text-fill: #3e2723;");
        
        javafx.scene.control.Label priceLabel = new javafx.scene.control.Label("");
        priceLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");
        
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(350, 80, 0, 0)); // Adjust to fit right box
        shopContainer.getChildren().add(infoBox);

        // BUY BUTTON
        Button buyButton = new Button("BUY");
        buyButton.getStyleClass().add("dashboard-button"); // Reuse your button style
        buyButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2e7d32;"); // Green for buy
        buyButton.setDisable(true); // Disabled until item selected
//        buyButton.setPrefWidth(90);  // Example: Smaller width (default was 250 in CSS)
//        buyButton.setPrefHeight(40);
        buyButton.setMaxHeight(40);
        buyButton.setMaxWidth(90);
        buyButton.setViewOrder(-1.0); 
        // --- BUY LOGIC ---
        buyButton.setOnAction(e -> {
            if (currentSelectedShopItem != null) {
                Player player = mainApp.getCurrentPlayer();
                
                // Check if player has enough GOLD (Currency)
                if (player.getCurrency() >= currentSelectedShopItem.price) {
                    // 1. Pay Money
                    player.deductCurrency(currentSelectedShopItem.price);
                    
                    // 2. Add to Inventory
                    // We create a new InventoryItem based on the ShopItem
                    InventoryItem newItem = new InventoryItem(
                        currentSelectedShopItem.name, 
                        currentSelectedShopItem.imagePath, 
                        currentSelectedShopItem.description
                    );
                    player.addItem(newItem);
                    
                    System.out.println("Bought " + currentSelectedShopItem.name + ". Remaining Gold: " + player.getCurrency());
                    nameLabel.setText("PURCHASED!");
                } else {
                    System.out.println("Not enough gold!");
                    nameLabel.setText("NO FUNDS!");
                }
                
            }
        });

        StackPane.setAlignment(buyButton, Pos.TOP_RIGHT);
        StackPane.setMargin(buyButton, new Insets(420, 70, 0, 0)); // Position below text
        shopContainer.getChildren().add(buyButton);


        // ====================================================================
        // 3. SHOP GRID (The Items)
        // ====================================================================
        GridPane shopGrid = new GridPane();
        shopGrid.setId("shop-grid"); 
        
        int cols = 4;
        int rows = 5;
        int slotSize = 52; 

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot"); 
                
                final int index = (y * cols) + x;
                
                // If we have an item for this slot, render it
                if (index < shopList.size()) {
                    ShopItem item = shopList.get(index);
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.imagePath)));
                        itemIcon.setFitWidth(40);
                        itemIcon.setFitHeight(40);
                        itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(6);
                        itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                    } catch (Exception e) {
                        // System.out.println("Image not found: " + item.imagePath);
                    }
                }

                // Click Logic
                slot.setOnMouseClicked(e -> {
                    if (index < shopList.size()) {
                        currentSelectedShopItem = shopList.get(index);
                        
                        // Update UI
                        nameLabel.setText(currentSelectedShopItem.name);
                        priceLabel.setText("COST: " + currentSelectedShopItem.price);
                        buyButton.setDisable(false); // Enable buy button
                        
                        try {
                            selectedItemView.setImage(new Image(getClass().getResourceAsStream(currentSelectedShopItem.imagePath)));
                        } catch (Exception ex) {}
                        
                        System.out.println("Selected Shop Item: " + currentSelectedShopItem.name);
                    } else {
                        currentSelectedShopItem = null;
                        nameLabel.setText("");
                        priceLabel.setText("");
                        selectedItemView.setImage(null);
                        buyButton.setDisable(true);
                    }
                });
                
                shopGrid.add(slot, x, y);
            }
        }

        StackPane.setAlignment(shopGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(shopGrid, new Insets(220, 0, 85, 54)); 

        shopContainer.getChildren().add(shopGrid);
        
        // Close Button
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
        craftingContainer.setId("crafting-bg"); 
        craftingContainer.setMaxSize(480, 640); 
        craftingContainer.setPrefSize(480, 640);

        // --- 1. Crafting Input Grid (3x3) ---
        GridPane inputGrid = new GridPane();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(70, 70); 
                slot.getStyleClass().add("inventory-slot");
                // TODO: Add drag-and-drop logic here later to accept items
                inputGrid.add(slot, x, y);
            }
        }
        StackPane.setAlignment(inputGrid, Pos.TOP_LEFT);
        StackPane.setMargin(inputGrid, new Insets(37, 0, 0, 40)); 
        craftingContainer.getChildren().add(inputGrid);

        // --- 2. Output Slot (1x1) ---
        Pane outputSlot = new Pane();
        outputSlot.setPrefSize(70, 70);
        outputSlot.setMaxSize(70, 70);
        outputSlot.getStyleClass().add("inventory-slot");
        StackPane.setAlignment(outputSlot, Pos.TOP_RIGHT);
        StackPane.setMargin(outputSlot, new Insets(108, 38, 0, 0));
        craftingContainer.getChildren().add(outputSlot);
        
        // --- 3. Inventory Grid (Bottom half) - NOW WITH ITEMS ---
        GridPane inventoryGrid = new GridPane();
        int cols = 7;
        int rows = 5;
        int slotSize = 52; // slightly smaller to fit width? or 55 as you had

        // --- ADDED: Get Items ---
        List<InventoryItem> playerItems = mainApp.getCurrentPlayer().getInventory();
        if (playerItems == null) playerItems = new java.util.ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(55, 55);
                slot.getStyleClass().add("inventory-slot");
                
                // --- ADDED: Render Items ---
                final int index = (y * cols) + x;
                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); 
                        itemIcon.setFitHeight(40);
                        itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(7); // Center manually
                        itemIcon.setLayoutY(7);
                        slot.getChildren().add(itemIcon);
                    } catch (Exception e) {}
                }
                
                // Click Logic
                final List<InventoryItem> finalItemsList = playerItems;
                slot.setOnMouseClicked(e -> {
                    if (index < finalItemsList.size()) {
                        System.out.println("Crafting Select: " + finalItemsList.get(index).getName());
                        // Future: Move this item to the inputGrid
                    }
                });

                inventoryGrid.add(slot, x, y);
            }
        }
        StackPane.setAlignment(inventoryGrid, Pos.BOTTOM_CENTER);
        // I adjusted your margin slightly to align better, tweak if needed
        StackPane.setMargin(inventoryGrid, new Insets(310, 0, 35, 50)); 
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