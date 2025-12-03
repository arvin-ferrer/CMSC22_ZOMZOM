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
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class Home {

    private Main mainApp;
    private StackPane rootPane; 
    private StackPane inventoryOverlay; 
    private StackPane shopOverlay;
    private StackPane craftingOverlay;
    private InventoryItem[][] craftingMatrix = new InventoryItem[3][3];
    private InventoryItem currentCraftingResult = null;
    private InventoryItem selectedInventoryItem = null; // For moving items
    private GridPane inputGridUI;
    private Pane outputSlotUI;
    private ImageView outputIconView; // To show result
    
    private GridPane inventoryGrid; 
    private GridPane craftingInventoryGrid;
    private Label nameLabel;
    private Label descLabel;
    private ImageView selectedItemView;

    public Home(Main mainApp) {
        this.mainApp = mainApp;
    }
    
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
        rootPane = new StackPane();
        rootPane.setId("home-screen-background"); 
        rootPane.setPrefSize(1280, 720);

        Button returnButton = new Button("RETURN TO BATTLE");
        returnButton.getStyleClass().add("dashboard-button"); 
        returnButton.setOnAction(e -> {
            System.out.println("Exiting safe house...");
            mainApp.showWarAreaScreen();
        });
        StackPane.setAlignment(returnButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(returnButton, new Insets(0, 50, 300, 0));
        rootPane.getChildren().add(returnButton);

        Pane chestClickArea = new Pane();
        chestClickArea.setMaxSize(320, 250); 
        chestClickArea.setMinSize(320, 250); 
        chestClickArea.setStyle("-fx-background-color: transparent;"); 
        chestClickArea.setCursor(javafx.scene.Cursor.HAND);
        chestClickArea.setOnMouseClicked(e -> showInventory());
        StackPane.setAlignment(chestClickArea, Pos.TOP_RIGHT);
        StackPane.setMargin(chestClickArea, new Insets(115, 350, 0, 0)); 
        rootPane.getChildren().add(chestClickArea);
        
        createInventoryOverlay(); 
        rootPane.getChildren().add(inventoryOverlay); 

        Pane shopClickArea = new Pane();
        shopClickArea.setPrefSize(20, 20);
        shopClickArea.setMaxSize(320, 250); 
        shopClickArea.setMinSize(320, 250); 
        shopClickArea.setStyle("-fx-background-color: transparent;"); 
        shopClickArea.setCursor(javafx.scene.Cursor.HAND);
        
        Pane craftingClickArea = new Pane();
        craftingClickArea.setMaxSize(400, 150); 
        craftingClickArea.setMinSize(400, 150);
        craftingClickArea.setStyle("-fx-background-color: transparent;");
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
        shopClickArea.setOnMouseClicked(e -> showShop());
        
        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("Safe House Interior");
        mainApp.getPrimaryStage().show();
    }
       
    private void refreshInventoryGrid() {
        if (inventoryGrid == null) return;

        // 1. Clear everything currently in the grid
        inventoryGrid.getChildren().clear();

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
                        
                        if (item.getQuantity() >= 1) {
                            javafx.scene.control.Label qtyLabel = new javafx.scene.control.Label("x" + item.getQuantity());
                            qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
                            qtyLabel.setLayoutX(28); 
                            qtyLabel.setLayoutY(35); 
                            slot.getChildren().add(qtyLabel);
                        }
                    } catch (Exception e) {}
                }

                // Click Logic
                final List<InventoryItem> finalItemsList = playerItems;
                slot.setOnMouseClicked(e -> {
                    if (index < finalItemsList.size()) {
                        InventoryItem clickedItem = finalItemsList.get(index);
                        
                        nameLabel.setText(clickedItem.getName().toUpperCase());
                        
                        String qtyText = "\n\nQuantity Owned: " + clickedItem.getQuantity();
                        descLabel.setText(clickedItem.getDescription() + qtyText);
                        
                        try {
                            selectedItemView.setImage(new Image(getClass().getResourceAsStream(clickedItem.getImagePath())));
                        } catch (Exception ex) {}
                    } else {
                        nameLabel.setText("");
                        descLabel.setText("");
                        selectedItemView.setImage(null);
                    }
                });
                
                inventoryGrid.add(slot, x, y);
            }
        }
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

        // Display Slot
        Pane displaySlot = new Pane();
        displaySlot.setPrefSize(180, 180);
        displaySlot.setMaxSize(180, 180);
        this.selectedItemView = new ImageView(); // Initialize class field
        selectedItemView.setFitWidth(120); 
        selectedItemView.setFitHeight(120);
        selectedItemView.setPreserveRatio(true);
        selectedItemView.setLayoutX(30); 
        selectedItemView.setLayoutY(30);
        
        displaySlot.getChildren().add(selectedItemView);
        StackPane.setAlignment(displaySlot, Pos.TOP_LEFT);
        StackPane.setMargin(displaySlot, new Insets(60, 0, 0, 30));
        inventoryContainer.getChildren().add(displaySlot);

        // Info Box
        VBox infoBox = new VBox(10);
        infoBox.setMaxSize(200, 180);
        infoBox.setAlignment(Pos.TOP_LEFT);
        
        this.nameLabel = new Label("SELECT ITEM"); // Initialize class field
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 24px; -fx-text-fill: #3e2723; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        this.descLabel = new Label("Click an item to see details."); // Initialize class field
        descLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #5d4037; -fx-font-weight: bold;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);

        infoBox.getChildren().addAll(nameLabel, descLabel);
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(65, 0, 0, 32));
        inventoryContainer.getChildren().add(infoBox);

        this.inventoryGrid = new GridPane();
        this.inventoryGrid.setId("inventory-grid"); 
        
        // Initial population of the grid
        refreshInventoryGrid();

        StackPane.setAlignment(inventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(inventoryGrid, new Insets(250, 0, 0, 0)); 
        inventoryContainer.getChildren().add(inventoryGrid);
        // ----------------------------------------
        
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> inventoryOverlay.setVisible(false));
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(0, 0, 0, 0));
        inventoryContainer.getChildren().add(closeButton);
        
        inventoryOverlay.getChildren().addAll(dimmer, inventoryContainer);
    }

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

        java.util.List<ShopItem> shopList = new java.util.ArrayList<>();
        shopList.add(new ShopItem("Wood", 50, "/assets/wood.png", "Construction material"));
        shopList.add(new ShopItem("Cloth", 30, "/assets/whiteCloth.png", "For crafting bandages"));
        shopList.add(new ShopItem("Gunpowder", 100, "/assets/gunpowder.png", "Explosive component"));
        shopList.add(new ShopItem("Rock", 20, "/assets/stone.png", "Basic resource"));
        shopList.add(new ShopItem("Mallet", 500, "/assets/mallet.png", "Melee weapon"));
        shopList.add(new ShopItem("Katana", 1500, "/assets/katana.png", "Sharp blade"));
        shopList.add(new ShopItem("Machine Gun", 5000, "/assets/machinegun.png", "Rapid fire"));

        ImageView selectedItemView = new ImageView();
        selectedItemView.setFitWidth(80);
        selectedItemView.setFitHeight(80);
        selectedItemView.setPreserveRatio(true);
        StackPane.setAlignment(selectedItemView, Pos.TOP_RIGHT);
        StackPane.setMargin(selectedItemView, new Insets(180, 70, 0, 20)); 
        shopContainer.getChildren().add(selectedItemView);

        javafx.scene.layout.VBox infoBox = new javafx.scene.layout.VBox(5);
        infoBox.setAlignment(Pos.TOP_RIGHT);
        javafx.scene.control.Label nameLabel = new javafx.scene.control.Label("SELECT ITEM");
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20px; -fx-text-fill: #3e2723;");
        javafx.scene.control.Label priceLabel = new javafx.scene.control.Label("");
        priceLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(350, 80, 0, 0)); 
        shopContainer.getChildren().add(infoBox);

        Button buyButton = new Button("BUY");
        buyButton.getStyleClass().add("dashboard-button"); 
        buyButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2e7d32;"); 
        buyButton.setDisable(true); 
        buyButton.setMaxHeight(40);
        buyButton.setMaxWidth(90);
        buyButton.setViewOrder(-1.0); 
        
        buyButton.setOnAction(e -> {
            if (currentSelectedShopItem != null) {
                Player player = mainApp.getCurrentPlayer();
                if (player.getCurrency() >= currentSelectedShopItem.price) {
                    player.deductCurrency(currentSelectedShopItem.price);
                    
                    InventoryItem newItem = new InventoryItem(
                        currentSelectedShopItem.name, 
                        currentSelectedShopItem.imagePath, 
                        currentSelectedShopItem.description
                    );
                    player.addItem(newItem);
                    
                    System.out.println("Bought " + currentSelectedShopItem.name + ". Remaining Gold: " + player.getCurrency());
                    nameLabel.setText("PURCHASED!");
                    
                  
                    refreshInventoryGrid();
                    
                } else {
                    System.out.println("Not enough gold!");
                    nameLabel.setText("NO FUNDS!");
                }
            }
        });

        StackPane.setAlignment(buyButton, Pos.TOP_RIGHT);
        StackPane.setMargin(buyButton, new Insets(420, 70, 0, 0)); 
        shopContainer.getChildren().add(buyButton);

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
                    } catch (Exception e) {}
                }
                slot.setOnMouseClicked(e -> {
                    if (index < shopList.size()) {
                        currentSelectedShopItem = shopList.get(index);
                        nameLabel.setText(currentSelectedShopItem.name);
                        priceLabel.setText("COST: " + currentSelectedShopItem.price);
                        buyButton.setDisable(false); 
                        try {
                            selectedItemView.setImage(new Image(getClass().getResourceAsStream(currentSelectedShopItem.imagePath)));
                        } catch (Exception ex) {}
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

        //3x3 grid
        this.inputGridUI = new GridPane();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(70, 70); 
                slot.getStyleClass().add("inventory-slot");
                
                final int finalX = x;
                final int finalY = y;
                
                // click to place item
                slot.setOnMouseClicked(e -> {
                    if (selectedInventoryItem != null) {
                        // Place item logic
                        placeItemInCraftingGrid(finalX, finalY);
                    } else {
                        // Remove item logic (if clicked empty hand)
                        removeItemFromCraftingGrid(finalX, finalY);
                    }
                });
                
                inputGridUI.add(slot, x, y);
            }
        }
        StackPane.setAlignment(inputGridUI, Pos.TOP_LEFT);
        StackPane.setMargin(inputGridUI, new Insets(37, 0, 0, 40)); 
        craftingContainer.getChildren().add(inputGridUI);

        // output slot
        this.outputSlotUI = new Pane();
        outputSlotUI.setPrefSize(70, 70);
        outputSlotUI.setMaxSize(70, 70);
        outputSlotUI.getStyleClass().add("inventory-slot");
        
        this.outputIconView = new ImageView();
        outputIconView.setFitWidth(50);
        outputIconView.setFitHeight(50);
        outputIconView.setLayoutX(10);
        outputIconView.setLayoutY(10);
        outputSlotUI.getChildren().add(outputIconView);

        // click to craft
        outputSlotUI.setOnMouseClicked(e -> {
            craftItem();
        });

        StackPane.setAlignment(outputSlotUI, Pos.TOP_RIGHT);
        StackPane.setMargin(outputSlotUI, new Insets(108, 38, 0, 0));
        craftingContainer.getChildren().add(outputSlotUI);
        
        // Inventory Grid ---
        this.craftingInventoryGrid = new GridPane(); 
        this.craftingInventoryGrid.setId("inventory-grid");
        refreshCraftingGrid(); // This method needs a small update to handle selection

        StackPane.setAlignment(craftingInventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(craftingInventoryGrid, new Insets(290, 0, 35, 0));
        craftingContainer.getChildren().add(craftingInventoryGrid);

        // close Button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> craftingOverlay.setVisible(false));
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        StackPane.setMargin(closeButton, new Insets(10, 10, 0, 0));
        craftingContainer.getChildren().add(closeButton);
        
        craftingOverlay.getChildren().addAll(dimmer, craftingContainer);
    }
    private void placeItemInCraftingGrid(int x, int y) {
        if (selectedInventoryItem == null) return;
        
        // decrement from player inventory
        if (selectedInventoryItem.getQuantity() > 0) {
            selectedInventoryItem.addQuantity(-1);
            
            // create a single item for the grid
            InventoryItem singleItem = new InventoryItem(selectedInventoryItem.getName(), selectedInventoryItem.getImagePath(), "");
            // singleItem.setQuantity(1); // default is 1, uncomment this line if needed
            
            // add to matrix
            craftingMatrix[x][y] = singleItem;
            
            // Refresh UIs
            refreshCraftingGrid(); // Update inventory counts
            refreshInventoryGrid(); // Update inventory UI
            updateCraftingGridUI(); // Show item in crafting table
            checkRecipes(); // See if we made something
        }
    }
    private void removeItemFromCraftingGrid(int x, int y) {
        if (craftingMatrix[x][y] != null) {
            // return item to player
            mainApp.getCurrentPlayer().addItem(craftingMatrix[x][y]);
            craftingMatrix[x][y] = null;
            
            refreshCraftingGrid();
            updateCraftingGridUI();
            checkRecipes();
        }
    }
    private void updateCraftingGridUI() {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int index = (y * 3) + x;
                Pane slot = (Pane) inputGridUI.getChildren().get(index);
                slot.getChildren().clear();
                
                if (craftingMatrix[x][y] != null) {
                    try {
                        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(craftingMatrix[x][y].getImagePath())));
                        icon.setFitWidth(50);
                        icon.setFitHeight(50);
                        icon.setLayoutX(10);
                        icon.setLayoutY(10);
                        slot.getChildren().add(icon);
                    } catch (Exception e) {}
                }
            }
        }
    }

    private void checkRecipes() {
        // count items in grid
        int woodCount = 0;
        int clothCount = 0;
        int gunpowderCount = 0;
        int rockCount = 0;
        int bandageCount = 0;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if (craftingMatrix[x][y] != null) {
                    String name = craftingMatrix[x][y].getName();
                    if (name.equals("Wood")) woodCount++;
                    if (name.equals("Cloth")) clothCount++;
                    if (name.equals("Gunpowder")) gunpowderCount++;
                    if (name.equals("Rock")) rockCount++;
                    if (name.equals("Bandage")) bandageCount++;
                }
            }
        }

        // crafting logic
        currentCraftingResult = null;
        outputIconView.setImage(null);

        // 3x wood = barrier
        if (woodCount == 3 && clothCount == 0 && gunpowderCount == 0 && rockCount == 0 && bandageCount == 0) {
            setResult("Barrier", "/assets/barrier-card.png");
        }
        // 3x cloth = bandage
        else if (clothCount == 3 && woodCount == 0 && gunpowderCount == 0 && rockCount == 0 && bandageCount == 0) {
            setResult("Bandage", "/assets/bandage.png");
        }
        // 5x gunpowder + 3x rock = grenade
        else if (gunpowderCount == 5 && rockCount == 3 && woodCount == 0 && clothCount == 0 && bandageCount == 0) {
            setResult("Grenade", "/assets/grenade.png");
        }
        // 1x bandage + 3x cloth = medkit
        else if (bandageCount == 1 && clothCount == 3 && woodCount == 0 && gunpowderCount == 0 && rockCount == 0) {
            setResult("Medkit", "/assets/medkit.png");
        }
    }

    private void setResult(String name, String path) {
        // Create the result item
        currentCraftingResult = new InventoryItem(name, path, "Crafted Item");
        try {
            outputIconView.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception e) {}
    }

    private void craftItem() {
        if (currentCraftingResult != null) {
        	// add result to inventory
            mainApp.getCurrentPlayer().addItem(currentCraftingResult);
            
            // clear crafting grid
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    craftingMatrix[x][y] = null;
                }
            }
            
            // reset UI
            updateCraftingGridUI();
            outputIconView.setImage(null);
            currentCraftingResult = null;
            refreshCraftingGrid();
            refreshInventoryGrid();
            System.out.println("Crafting Successful!");
        }
    }
    private void refreshCraftingGrid() {

        if (craftingInventoryGrid == null) return;
        craftingInventoryGrid.getChildren().clear();
        
        List<InventoryItem> playerItems = mainApp.getCurrentPlayer().getInventory();
        int cols = 7; int rows = 5; int slotSize = 52;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot");
                
                final int index = (y * cols) + x;
                
                // render icon
                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); itemIcon.setFitHeight(40); itemIcon.setLayoutX(6); itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                        if (item.getQuantity() > 1) {
                            Label qtyLabel = new Label("x" + item.getQuantity());
                            qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
                            qtyLabel.setLayoutX(28); qtyLabel.setLayoutY(35);
                            slot.getChildren().add(qtyLabel);
                        }
                    } catch (Exception e) {}
                }

                // click logic for selection
                slot.setOnMouseClicked(e -> {
                    if (index < playerItems.size()) {
                        selectedInventoryItem = playerItems.get(index);
                        System.out.println("Selected for crafting: " + selectedInventoryItem.getName());
//                        refreshInventoryGrid(); // to clear previous selection
                        // add a visual border to show selected
                         slot.setStyle("-fx-border-color: yellow; -fx-border-width: 2px;");
                    } else {
                        selectedInventoryItem = null;
                    }
                });

                craftingInventoryGrid.add(slot, x, y);
            }
        }
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
        
        refreshCraftingGrid(); 
        
        craftingOverlay.setVisible(true);
    }
    
}