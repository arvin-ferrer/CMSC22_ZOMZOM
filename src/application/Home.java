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
    
    // Crafting Logic Variables
    private InventoryItem[][] craftingMatrix = new InventoryItem[3][3];
    private InventoryItem currentCraftingResult = null;
    private InventoryItem selectedInventoryItem = null; // The item currently selected to place
    
    // UI References
    private GridPane inputGridUI;
    private Pane outputSlotUI;
    private ImageView outputIconView; 
    private GridPane inventoryGrid; 
    private GridPane craftingInventoryGrid;
    private Label nameLabel;
    private Label descLabel;
    private ImageView selectedItemView;
    
    // NEW: Equip Button Reference
    private Button equipButton;

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

        // Chest/Inventory Click Area
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

        // Crafting Click Area
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
        
        // Shop Click Area
        Pane shopClickArea = new Pane();
        shopClickArea.setPrefSize(320, 250);
        shopClickArea.setMaxSize(320, 250); 
        shopClickArea.setStyle("-fx-background-color: transparent;"); 
        shopClickArea.setCursor(javafx.scene.Cursor.HAND);
        shopClickArea.setOnMouseClicked(e -> showShop());
        
        StackPane.setAlignment(shopClickArea, Pos.TOP_LEFT);
        StackPane.setMargin(shopClickArea, new Insets(50, 0, 0, 50)); 
        rootPane.getChildren().add(shopClickArea);
        
        createShopOverlay();
        rootPane.getChildren().add(shopOverlay);
        
        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("Safe House Interior");
        mainApp.getPrimaryStage().show();
    }
       
    // --- INVENTORY LOGIC (UPDATED FOR MULTIPLE WEAPONS) ---
    private void refreshInventoryGrid() {
        if (inventoryGrid == null) return;
        inventoryGrid.getChildren().clear();

        int cols = 7; int rows = 5; int slotSize = 52; 

        Player player = mainApp.getCurrentPlayer();
        List<InventoryItem> playerItems = player.getInventory();
        
        // NEW: Get the list of equipped weapons
        List<String> equippedWeapons = player.getEquippedWeapons();
        
        if (playerItems == null) playerItems = new java.util.ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(slotSize, slotSize);
                slot.getStyleClass().add("inventory-slot"); 
                
                final int index = (y * cols) + x; 

                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    
                    // HIGHLIGHT: Check if this specific item is in the equipped list
                    if (equippedWeapons.contains(item.getName())) {
                        slot.setStyle("-fx-border-color: #39ff14; -fx-border-width: 2px; -fx-border-radius: 5px;");
                    }
                    
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); itemIcon.setFitHeight(40); itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(6); itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                        
                        if (item.getQuantity() >= 1) {
                            Label qtyLabel = new Label("x" + item.getQuantity());
                            qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
                            qtyLabel.setLayoutX(28); qtyLabel.setLayoutY(35); 
                            slot.getChildren().add(qtyLabel);
                        }
                    } catch (Exception e) {}
                }

                // Click to view details
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
                        
                        // --- UPDATED: EQUIP BUTTON LOGIC ---
                        if (clickedItem.isWeapon()) {
                            equipButton.setVisible(true);
                            equipButton.setDisable(false); // Reset disabled state
                            
                            boolean isEquipped = player.isWeaponEquipped(clickedItem.getName());
                            int equippedCount = player.getEquippedWeapons().size();

                            if (isEquipped) {
                                // CASE 1: Item is equipped -> Allow Unequip
                                equipButton.setText("UNEQUIP");
                                equipButton.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-font-weight: bold;");
                                equipButton.setOnAction(ev -> {
                                    player.unequipWeapon(clickedItem.getName());
                                    refreshInventoryGrid(); 
                                    slot.getOnMouseClicked().handle(e); // Refresh detail view
                                });
                            } else {
                                // CASE 2: Item not equipped
                                if (equippedCount >= 2) {
                                    // Max limit reached -> Disable button
                                    equipButton.setText("MAX (2)");
                                    equipButton.setStyle("-fx-background-color: #333; -fx-text-fill: gray;");
                                    equipButton.setDisable(true);
                                } else {
                                    // Limit not reached -> Allow Equip
                                    equipButton.setText("EQUIP");
                                    equipButton.setStyle("-fx-background-color: #39ff14; -fx-text-fill: black; -fx-font-weight: bold;");
                                    equipButton.setOnAction(ev -> {
                                        player.equipWeapon(clickedItem.getName());
                                        refreshInventoryGrid();
                                        slot.getOnMouseClicked().handle(e); 
                                    });
                                }
                            }
                        } else {
                            // Not a weapon, hide button
                            equipButton.setVisible(false);
                        }
                        
                    } else {
                        // Empty Slot
                        nameLabel.setText(""); descLabel.setText(""); selectedItemView.setImage(null);
                        equipButton.setVisible(false);
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
        this.selectedItemView = new ImageView();
        selectedItemView.setFitWidth(120); selectedItemView.setFitHeight(120); selectedItemView.setPreserveRatio(true);
        selectedItemView.setLayoutX(30); selectedItemView.setLayoutY(30);
        displaySlot.getChildren().add(selectedItemView);
        StackPane.setAlignment(displaySlot, Pos.TOP_LEFT);
        StackPane.setMargin(displaySlot, new Insets(60, 0, 0, 30));
        inventoryContainer.getChildren().add(displaySlot);

        // Info Box
        VBox infoBox = new VBox(10);
        infoBox.setMaxSize(200, 200); // Slightly taller for button
        infoBox.setAlignment(Pos.TOP_LEFT);
        
        this.nameLabel = new Label("SELECT ITEM");
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 24px; -fx-text-fill: #3e2723; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        
        this.descLabel = new Label("Click an item to see details.");
        descLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #5d4037; -fx-font-weight: bold;");
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(200);

        // Equip Button
        this.equipButton = new Button("EQUIP");
        equipButton.setPrefWidth(120);
        equipButton.setVisible(false); 

        infoBox.getChildren().addAll(nameLabel, descLabel, equipButton);
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(65, 0, 0, 32));
        inventoryContainer.getChildren().add(infoBox);

        this.inventoryGrid = new GridPane();
        this.inventoryGrid.setId("inventory-grid"); 
        refreshInventoryGrid();

        StackPane.setAlignment(inventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(inventoryGrid, new Insets(250, 0, 0, 0)); 
        inventoryContainer.getChildren().add(inventoryGrid);
        
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> inventoryOverlay.setVisible(false));
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        inventoryContainer.getChildren().add(closeButton);
        
        inventoryOverlay.getChildren().addAll(dimmer, inventoryContainer);
    }

    // --- SHOP LOGIC ---
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
        shopList.add(new ShopItem(InventoryItem.WOOD, 50, InventoryItem.WOOD_IMAGE, "Construction material"));
        shopList.add(new ShopItem(InventoryItem.CLOTH, 30, InventoryItem.CLOTH_IMAGE, "For crafting bandages"));
        shopList.add(new ShopItem(InventoryItem.GUNPOWDER, 100, InventoryItem.GUNPOWDER_IMAGE, "Explosive component"));
        shopList.add(new ShopItem(InventoryItem.STONE, 20, InventoryItem.STONE_IMAGE, "Basic resource"));
        shopList.add(new ShopItem("Mallet", 500, "/assets/mallet-card.png", "Melee weapon"));
        shopList.add(new ShopItem("Katana", 1500, "/assets/katana-card.png", "Sharp blade"));
        shopList.add(new ShopItem("Machine Gun", 5000, "/assets/gun-card.png", "Rapid fire"));

        ImageView selectedItemView = new ImageView();
        selectedItemView.setFitWidth(80); selectedItemView.setFitHeight(80); selectedItemView.setPreserveRatio(true);
        StackPane.setAlignment(selectedItemView, Pos.TOP_RIGHT);
        StackPane.setMargin(selectedItemView, new Insets(180, 70, 0, 20)); 
        shopContainer.getChildren().add(selectedItemView);

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.TOP_RIGHT);
        Label nameLabel = new Label("SELECT ITEM");
        nameLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20px; -fx-text-fill: #3e2723;");
        Label priceLabel = new Label("");
        priceLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px; -fx-text-fill: gold; -fx-font-weight: bold;");
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        StackPane.setAlignment(infoBox, Pos.TOP_RIGHT);
        StackPane.setMargin(infoBox, new Insets(350, 80, 0, 0)); 
        shopContainer.getChildren().add(infoBox);

        Button buyButton = new Button("BUY");
        buyButton.getStyleClass().add("dashboard-button"); 
        buyButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2e7d32;"); 
        buyButton.setDisable(true); 
        buyButton.setMaxHeight(40); buyButton.setMaxWidth(90); buyButton.setViewOrder(-1.0); 
        
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
                    
                    nameLabel.setText("PURCHASED!");
                    refreshInventoryGrid();
                    refreshCraftingGrid();
                } else {
                    nameLabel.setText("NO FUNDS!");
                }
            }
        });

        StackPane.setAlignment(buyButton, Pos.TOP_RIGHT);
        StackPane.setMargin(buyButton, new Insets(420, 70, 0, 0)); 
        shopContainer.getChildren().add(buyButton);

        GridPane shopGrid = new GridPane();
        shopGrid.setId("shop-grid"); 
        int cols = 4; int rows = 5; int slotSize = 52; 

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
                        itemIcon.setFitWidth(40); itemIcon.setFitHeight(40); itemIcon.setPreserveRatio(true);
                        itemIcon.setLayoutX(6); itemIcon.setLayoutY(6);
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
                        nameLabel.setText(""); priceLabel.setText(""); selectedItemView.setImage(null);
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

    // --- CRAFTING LOGIC ---

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

        // 3x3 Input Grid
        this.inputGridUI = new GridPane();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(70, 70); 
                slot.getStyleClass().add("inventory-slot");
                
                final int finalX = x;
                final int finalY = y;
                
                // Click Logic: Place if item selected, Remove if empty handed
                slot.setOnMouseClicked(e -> {
                    if (selectedInventoryItem != null) {
                        placeItemInCraftingGrid(finalX, finalY);
                    } else {
                        removeItemFromCraftingGrid(finalX, finalY);
                    }
                });
                
                inputGridUI.add(slot, x, y);
            }
        }
        StackPane.setAlignment(inputGridUI, Pos.TOP_LEFT);
        StackPane.setMargin(inputGridUI, new Insets(37, 0, 0, 40)); 
        craftingContainer.getChildren().add(inputGridUI);

        // Output Slot
        this.outputSlotUI = new Pane();
        outputSlotUI.setPrefSize(70, 70);
        outputSlotUI.setMaxSize(70, 70);
        outputSlotUI.getStyleClass().add("inventory-slot");
        
        this.outputIconView = new ImageView();
        outputIconView.setFitWidth(50); outputIconView.setFitHeight(50);
        outputIconView.setLayoutX(10); outputIconView.setLayoutY(10);
        outputSlotUI.getChildren().add(outputIconView);

        // Click to Craft
        outputSlotUI.setOnMouseClicked(e -> craftItem());

        StackPane.setAlignment(outputSlotUI, Pos.TOP_RIGHT);
        StackPane.setMargin(outputSlotUI, new Insets(108, 38, 0, 0));
        craftingContainer.getChildren().add(outputSlotUI);
        
        // Player Inventory at Bottom
        this.craftingInventoryGrid = new GridPane(); 
        this.craftingInventoryGrid.setId("inventory-grid");
        refreshCraftingGrid(); 

        StackPane.setAlignment(craftingInventoryGrid, Pos.BOTTOM_CENTER);
        StackPane.setMargin(craftingInventoryGrid, new Insets(290, 0, 35, 0));
        craftingContainer.getChildren().add(craftingInventoryGrid);

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
        
        // Only place if slot is empty (prevents overwriting)
        if (craftingMatrix[x][y] == null) {
            if (selectedInventoryItem.getQuantity() > 0) {
                // Decrement from player
                selectedInventoryItem.addQuantity(-1);
                
                // Create single copy for grid
                InventoryItem singleItem = new InventoryItem(selectedInventoryItem.getName(), selectedInventoryItem.getImagePath(), "");
                craftingMatrix[x][y] = singleItem;
                
                // Refresh UIs
                refreshCraftingGrid(); 
                refreshInventoryGrid(); 
                updateCraftingGridUI(); 
                checkRecipes(); 
            }
        }
    }

    private void removeItemFromCraftingGrid(int x, int y) {
        if (craftingMatrix[x][y] != null) {
            // Return item to player
            mainApp.getCurrentPlayer().addItem(craftingMatrix[x][y]);
            
            // Remove from grid
            craftingMatrix[x][y] = null;
            
            refreshCraftingGrid();
            refreshInventoryGrid();
            updateCraftingGridUI();
            checkRecipes();
        }
    }

    private void updateCraftingGridUI() {
        // Redraws the 3x3 input grid based on the matrix
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int index = (y * 3) + x;
                Pane slot = (Pane) inputGridUI.getChildren().get(index);
                slot.getChildren().clear();
                
                if (craftingMatrix[x][y] != null) {
                    try {
                        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream(craftingMatrix[x][y].getImagePath())));
                        icon.setFitWidth(50); icon.setFitHeight(50); icon.setLayoutX(10); icon.setLayoutY(10);
                        slot.getChildren().add(icon);
                    } catch (Exception e) {}
                }
            }
        }
    }

    private void checkRecipes() {
        // Reset output
        currentCraftingResult = null;
        outputIconView.setImage(null);

        // 1. RECIPE: BARRIER 
        if (checkPattern(
            null, null, null,
            null, null, null,
            InventoryItem.WOOD, InventoryItem.WOOD, InventoryItem.WOOD
        )) {
            setResult(InventoryItem.BARRIER, InventoryItem.BARRIER_IMAGE);
            return;
        }

        // 2. RECIPE: BANDAGE
        if (checkPattern(
            null, null, null,
            InventoryItem.CLOTH, InventoryItem.CLOTH, InventoryItem.CLOTH,
            null, null, null
        )) {
            setResult(InventoryItem.BANDAGE, InventoryItem.BANDAGE_IMAGE);
            return;
        }

        // 3. RECIPE: MEDKIT
        if (checkPattern(
            null, InventoryItem.CLOTH, null,
            InventoryItem.CLOTH, InventoryItem.BANDAGE, InventoryItem.CLOTH,
            null, null, null
        )) {
            setResult(InventoryItem.MEDKIT, InventoryItem.MEDKIT_IMAGE);
            return;
        }

        // 4. RECIPE: GRENADE
        if (checkPattern(
            InventoryItem.GUNPOWDER, InventoryItem.STONE,     InventoryItem.GUNPOWDER,
            InventoryItem.STONE,     InventoryItem.GUNPOWDER, InventoryItem.STONE,
            InventoryItem.GUNPOWDER, InventoryItem.GUNPOWDER, InventoryItem.GUNPOWDER
        )) {
            setResult(InventoryItem.GRENADE, InventoryItem.GRENADE_IMAGE);
            return;
        }
    }

    /**
     * Helper method to compare the crafting matrix against a specific 3x3 pattern.
     */
    private boolean checkPattern(
            String r0c0, String r1c0, String r2c0,  // Row 1 (y=0)
            String r0c1, String r1c1, String r2c1,  // Row 2 (y=1)
            String r0c2, String r1c2, String r2c2   // Row 3 (y=2)
    ) {
        return 
            matchSlot(0, 0, r0c0) && matchSlot(1, 0, r1c0) && matchSlot(2, 0, r2c0) &&
            matchSlot(0, 1, r0c1) && matchSlot(1, 1, r1c1) && matchSlot(2, 1, r2c1) &&
            matchSlot(0, 2, r0c2) && matchSlot(1, 2, r1c2) && matchSlot(2, 2, r2c2);
    }

    private boolean matchSlot(int x, int y, String expectedName) {
        InventoryItem itemInSlot = craftingMatrix[x][y];
        if (expectedName == null) {
            return itemInSlot == null;
        }
        if (itemInSlot != null) {
            return itemInSlot.getName().equals(expectedName);
        }
        return false;
    }

    private void setResult(String name, String path) {
        currentCraftingResult = new InventoryItem(name, path, "Crafted Item");
        try {
            outputIconView.setImage(new Image(getClass().getResourceAsStream(path)));
        } catch (Exception e) {}
    }

    private void craftItem() {
        if (currentCraftingResult != null) {
            mainApp.getCurrentPlayer().addItem(currentCraftingResult);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    craftingMatrix[x][y] = null;
                }
            }
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
                
                if (index < playerItems.size()) {
                    InventoryItem item = playerItems.get(index);
                    try {
                        ImageView itemIcon = new ImageView(new Image(getClass().getResourceAsStream(item.getImagePath())));
                        itemIcon.setFitWidth(40); itemIcon.setFitHeight(40); itemIcon.setLayoutX(6); itemIcon.setLayoutY(6);
                        slot.getChildren().add(itemIcon);
                        if (item.getQuantity() >= 1) {
                            Label qtyLabel = new Label("x" + item.getQuantity());
                            qtyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
                            qtyLabel.setLayoutX(28); qtyLabel.setLayoutY(35);
                            slot.getChildren().add(qtyLabel);
                        }
                    } catch (Exception e) {}
                }

                slot.setOnMouseClicked(e -> {
                    if (index < playerItems.size()) {
                        selectedInventoryItem = playerItems.get(index);
                        refreshCraftingGrid(); 
                        slot.setStyle("-fx-border-color: yellow; -fx-border-width: 3px; -fx-border-radius: 5px;");
                    } else {
                        selectedInventoryItem = null;
                        refreshCraftingGrid();
                    }
                });

                craftingInventoryGrid.add(slot, x, y);
            }
        }
    }
    
    public void showInventory() { 
        System.out.println("Opening Inventory...");
        refreshInventoryGrid(); // Ensure refreshed before showing
        inventoryOverlay.setVisible(true);
    }
    private void showShop() { shopOverlay.setVisible(true); }
    private void showCrafting() {
        selectedInventoryItem = null;
        refreshCraftingGrid(); 
        craftingOverlay.setVisible(true);
    }
}