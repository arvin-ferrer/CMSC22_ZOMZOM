package application;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Inventory {
    private Main mainApp;
    private FlowPane itemContainer; 

    public Inventory(Main mainApp) {
        this.mainApp = mainApp;
    }
    
    public void showScreen() {
        StackPane rootPane = new StackPane();
        rootPane.setId("inventory-screen-background"); 
        rootPane.setPrefSize(1280, 720);

        // 1. Header
        Label title = new Label("INVENTORY");
        title.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 40px; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(40, 0, 0, 0));

        // 2. Item Container
        itemContainer = new FlowPane();
        itemContainer.setPadding(new Insets(20));
        itemContainer.setHgap(20);
        itemContainer.setVgap(20);
        itemContainer.setAlignment(Pos.CENTER);
        itemContainer.setPrefWrapLength(1000); 
        itemContainer.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 20;");

        ScrollPane scrollPane = new ScrollPane(itemContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxSize(1000, 500);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setPannable(true);

        // 3. Populate Items
        refreshInventoryItems();

        // 4. Return Button
        Button returnButton = new Button("RETURN TO BATTLE");
        returnButton.getStyleClass().add("dashboard-button"); 
        returnButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-size: 18px;");
        returnButton.setOnAction(e -> {
            mainApp.showWarAreaScreen();
        });

        StackPane.setAlignment(returnButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(returnButton, new Insets(0, 0, 50, 0));

        rootPane.getChildren().addAll(title, scrollPane, returnButton);

        Scene scene = new Scene(rootPane, 1280, 720);
        try {
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        } catch (Exception e) {}

        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("Inventory Screen");
        mainApp.getPrimaryStage().show();
    }

    private void refreshInventoryItems() {
        itemContainer.getChildren().clear();
        
        Player player = mainApp.getCurrentPlayer();
        List<InventoryItem> items = player.getInventory();
        
        // Count currently equipped weapons
        int equippedCount = player.getEquippedWeapons().size();

        if (items.isEmpty()) {
            Label emptyLbl = new Label("Inventory is empty.");
            emptyLbl.setTextFill(Color.WHITE);
            emptyLbl.setFont(Font.font(20));
            itemContainer.getChildren().add(emptyLbl);
            return;
        }

        for (InventoryItem item : items) {
            // Create Card Node
            VBox card = new VBox(10);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(15));
            card.setPrefSize(180, 220);
            
            // Check if this specific item is equipped
            boolean isEquipped = player.isWeaponEquipped(item.getName());
            
            // Card Style
            if (isEquipped) {
                // Green border for equipped
                card.setStyle("-fx-background-color: rgba(0, 50, 0, 0.8); -fx-background-radius: 10; -fx-border-color: #39ff14; -fx-border-width: 3; -fx-border-radius: 10;");
            } else {
                // Standard dark background
                card.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-background-radius: 10; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 10;");
            }

            // Image
            ImageView iv = new ImageView();
            try {
                iv.setImage(new Image(getClass().getResourceAsStream(item.getImagePath())));
            } catch (Exception e) {
                System.err.println("Img missing: " + item.getImagePath());
            }
            iv.setFitWidth(80);
            iv.setFitHeight(80);
            iv.setPreserveRatio(true);

            // Name Label
            Label nameLbl = new Label(item.getName());
            nameLbl.setTextFill(Color.WHITE);
            nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            nameLbl.setWrapText(true);

            // Quantity Label
            Label qtyLbl = new Label("Qty: " + item.getQuantity());
            qtyLbl.setTextFill(Color.LIGHTGRAY);

            // Equip/Unequip Button (Only if it's a weapon)
            Button actionBtn = new Button();
            actionBtn.setPrefWidth(120);
            
            if (item.isWeapon()) {
                if (isEquipped) {
                    actionBtn.setText("UNEQUIP");
                    actionBtn.setStyle("-fx-background-color: #555; -fx-text-fill: white; -fx-cursor: hand;");
                    actionBtn.setOnAction(e -> {
                        player.unequipWeapon(item.getName());
                        refreshInventoryItems(); // Refresh to remove border
                    });
                } else {
                    actionBtn.setText("EQUIP");
                    
                    // Logic: If we already have 2 weapons, disable this button
                    if (equippedCount >= 2) {
                        actionBtn.setDisable(true);
                        actionBtn.setText("MAX (2)");
                        actionBtn.setStyle("-fx-background-color: #333; -fx-text-fill: gray;");
                    } else {
                        actionBtn.setStyle("-fx-background-color: #39ff14; -fx-text-fill: black; -fx-font-weight: bold; -fx-cursor: hand;");
                        actionBtn.setOnAction(e -> {
                            player.equipWeapon(item.getName());
                            refreshInventoryItems(); // Refresh to add border
                        });
                    }
                }
            } else {
                // Consumable
                actionBtn.setText(item.getDescription());
                actionBtn.setPrefWidth(140);
                actionBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gray; -fx-font-size: 10px;");
                actionBtn.setDisable(true); 
            }
            
            card.getChildren().addAll(iv, nameLbl, qtyLbl, actionBtn);
            itemContainer.getChildren().add(card);
        }
    }
}