package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Random;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.animation.RotateTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.transform.Rotate;
import javafx.scene.image.Image;

public class DashboardScreen {

    private Main mainApp;
    
    // These are the class fields we need to assign to!
    private Label levelLabel;
    private Label xpLabel;
    private Label currencyLabel; // Coins
    private Label burgerLabel;   // Burgers
    
    private Random random = new Random();
    private long lastClaimTime = 0;
    private static final long COOLDOWN_MS = 5000; 

    public DashboardScreen(Main mainWindow) {
        this.mainApp = mainWindow;
    }
    private ImageView animationIcon;
    private void claimReward() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClaimTime < COOLDOWN_MS) {
            long secondsLeft = (COOLDOWN_MS - (currentTime - lastClaimTime)) / 1000;
            showAlert("Please Wait", "Reward is on cooldown! Wait " + (secondsLeft + 1) + " more seconds.");
            return;
        }

        int roll = random.nextInt(100);
        
        // --- ARRAYS REORDERED TO MATCH RARITY (Common -> Legendary) ---
        String[] names = {
            "Stone",       // Index 0: Common
            "Cloth",       // Index 1: Common
            "Burger",      // Index 2: Uncommon (Item version)
            "Gunpowder",   // Index 3: Uncommon
            "Grenade",     // Index 4: Rare
            "Medkit",      // Index 5: Rare
            "Gold"         // Index 6: Legendary (Item version)
        };

        String[] paths = {
            "/assets/stone.png", 
            "/assets/whiteCloth.png", 
            "/assets/burger-sprite.png", 
            "/assets/gunpowder.png", 
            "/assets/grenade-sprite.png", 
            "/assets/medkit.png", 
            "/assets/coin-sprite.gif"
        };

        String[] descs = {
            "Building Material", 
            "Crafting Material", 
            "Restores Hunger", 
            "Crafting Material", 
            "Explosive Damage", 
            "Heals 50 HP", 
            "Currency"
        };
      
        int[] weights = {
            50, // Stone (Common)
            50, // Cloth (Common)
            25, // Burger (Uncommon)
            25, // Gunpowder (Uncommon)
            10, // Grenade (Rare)
            10, // Medkit (Rare)
            1   // Gold (Legendary)
        };

        String[] rarityTags = {
            "COMMON", "COMMON", "UNCOMMON", "UNCOMMON", "RARE", "RARE", "LEGENDARY"
        };

        int calculatedItemIndex = 0; 

        String imagePathToLoad = "/assets/medkit.png"; // Default fallback

        if (roll < 40) {
            imagePathToLoad = "/assets/coin-sprite.gif"; 
        } 
        else if (roll < 80) {
            imagePathToLoad = "/assets/burger-sprite.png"; 
        } 
        else {
            int totalWeight = 0;
            for (int w : weights) totalWeight += w;

            int randomTicket = random.nextInt(totalWeight);
            
            for (int i = 0; i < weights.length; i++) {
                randomTicket -= weights[i];
                if (randomTicket < 0) {
                    calculatedItemIndex = i;
                    break;
                }
            }
            
            imagePathToLoad = paths[calculatedItemIndex];
        }

        if (animationIcon == null) {
            animationIcon = new ImageView();
            animationIcon.setFitWidth(100);
            animationIcon.setFitHeight(100);
        }

        try {
            animationIcon.setImage(new Image(getClass().getResourceAsStream(imagePathToLoad)));
        } catch (Exception e) {
            System.out.println("Could not load image: " + imagePathToLoad);
            try { animationIcon.setImage(new Image(getClass().getResourceAsStream("/assets/medkit.png"))); } catch (Exception ex) {}
        }

        StackPane root = (StackPane) mainApp.getPrimaryStage().getScene().getRoot();
        if (!root.getChildren().contains(animationIcon)) {
            root.getChildren().add(animationIcon);
        }
        animationIcon.setVisible(true);
        
        RotateTransition rotate = new RotateTransition(Duration.seconds(1.5), animationIcon);
        rotate.setByAngle(720); 
        rotate.setCycleCount(1);
        rotate.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);

        final int finalRoll = roll;
        final int finalItemIndex = calculatedItemIndex;

        rotate.setOnFinished(e -> {
            animationIcon.setVisible(false);
            root.getChildren().remove(animationIcon); 
            
            Platform.runLater(() -> {
                lastClaimTime = System.currentTimeMillis();
                Player player = mainApp.getCurrentPlayer();
                String message = "";
                
                if (finalRoll < 40) {
                    int amount = 50 + random.nextInt(101); 
                    player.addCurrency(amount);
                    message = "You found " + amount + " Coins!";
                } 
                else if (finalRoll < 80) {
                    int amount = 20 + random.nextInt(81); 
                    player.addBurger(amount);
                    message = "You gathered " + amount + " Burgers!";
                } 
                else {
                    // item Drop Logic
                    int idx = finalItemIndex;
                    
                    InventoryItem newItem = new InventoryItem(names[idx], paths[idx], descs[idx]);
                    player.addItem(newItem);
                    
                    // custom message based on rarity
                    String rarity = rarityTags[idx];
                    
                    if (rarity.equals("LEGENDARY")) {
                        message = "OMG!! LEGENDARY DROP! You found " + names[idx] + "!";
                    } else if (rarity.equals("RARE")) {
                        message = "Great find! You got a Rare " + names[idx] + "!";
                    } else {
                        message = "You found " + names[idx] + " (" + rarity + ").";
                    }
                }
                
                updateLabels();
                showAlert("Reward Claimed", message);
            });
        });

        rotate.play();
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("ZOMZOM Reward");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void showScreen() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        } catch (Exception e) {}
        
        Player currentPlayer = mainApp.getCurrentPlayer();

        // BorderPane setup
        BorderPane topBar = new BorderPane();
        topBar.setId("dashboard-top-bar");
        topBar.setPadding(new Insets(15, 30, 15, 30)); 

        Text scenetitle = new Text("ZOMZOM");
        scenetitle.setId("dashboard-title-text"); 
        topBar.setLeft(scenetitle);
        BorderPane.setAlignment(scenetitle, Pos.CENTER_LEFT); 
        
        Label playerInfo = new Label("SURVIVOR: " + currentPlayer.getUsername());
        playerInfo.setId("player-info-label"); 
        topBar.setCenter(playerInfo);
        BorderPane.setAlignment(playerInfo, Pos.CENTER);

        HBox statsBox = new HBox(30); 
        statsBox.setAlignment(Pos.CENTER_RIGHT);
        
        // --- FIX: ASSIGN TO CLASS FIELDS (Removed 'Label' type declaration) ---
        
        levelLabel = new Label("LEVEL: " + currentPlayer.getLevel());
        xpLabel = new Label("XP: " + currentPlayer.getExperiencePoints() + "/" + currentPlayer.getExperienceToNextLevel());
        currencyLabel = new Label("COINS: " + currentPlayer.getCurrency());
        
        // --- ADDED BURGER LABEL ---
        burgerLabel = new Label("BURGERS: " + currentPlayer.getBurger());

        // Styling
        levelLabel.getStyleClass().add("stat-label");
        xpLabel.getStyleClass().add("stat-label");
        currencyLabel.getStyleClass().add("stat-label");
        burgerLabel.getStyleClass().add("stat-label"); // Style the burger label too
        
        // Add ALL labels to the box
        statsBox.getChildren().addAll(levelLabel, xpLabel, burgerLabel, currencyLabel);
        
        topBar.setRight(statsBox);
        BorderPane.setAlignment(statsBox, Pos.CENTER_RIGHT);

        // Menu Buttons
        VBox buttonMenu = new VBox(25); 
        buttonMenu.setAlignment(Pos.CENTER_LEFT);
        buttonMenu.setId("dashboard-vbox"); 

        Text menuTitle = new Text("MAIN MENU");
        menuTitle.setId("dashboard-menu-title"); 
        VBox.setMargin(menuTitle, new Insets(0, 0, 10, 0)); 
        buttonMenu.getChildren().add(menuTitle);
        
        Button playButton = new Button("Play");
        playButton.getStyleClass().add("dashboard-button"); 
        
        Button inventoryButton = new Button("Inventory");
        inventoryButton.getStyleClass().add("dashboard-button"); 

        Button claimRewardButton = new Button("Claim Reward");
        claimRewardButton.getStyleClass().add("dashboard-button"); 

        Button statsButton = new Button("Stats");
        statsButton.getStyleClass().add("dashboard-button"); 

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("dashboard-button"); 

        buttonMenu.getChildren().addAll(
                playButton,
                inventoryButton,
                claimRewardButton,
                statsButton,
                logoutButton
        );
        
        inventoryButton.setOnAction(e -> {
            mainApp.showHomeScreenInventory();
        });
        
        logoutButton.setOnAction(e -> {
            Login.saveUsers(new java.util.ArrayList<>(mainApp.getData()), mainApp.getSavePath());
            System.out.println("Game saved successfully.");
            mainApp.setCurrentPlayer(null); 
            mainApp.showLoginScreen();    
        });
        
        playButton.setOnAction(e -> {
            mainApp.showWarAreaScreen(); 
        });
        
        claimRewardButton.setOnAction(e -> {
            claimReward();
            updateLabels(); // This will now work because fields are initialized!
        });

        StackPane rootPane = new StackPane();
        rootPane.setId("dashboard-background-panel"); 
       
        rootPane.getChildren().addAll(topBar, buttonMenu); 
        
        StackPane.setAlignment(topBar, Pos.TOP_CENTER); 
        
        StackPane.setAlignment(buttonMenu, Pos.CENTER_LEFT);
        StackPane.setMargin(buttonMenu, new Insets(100, 0, 0, 50)); 

        
        Scene scene = new Scene(rootPane, 1280, 720); 
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM - Dashboard");
        mainApp.getPrimaryStage().show();
    }
     
    
    // This method updates the CLASS FIELDS text
    public void updateLabels() {
        Player p = mainApp.getCurrentPlayer();
        if (p != null) {
            if (levelLabel != null) levelLabel.setText("LEVEL: " + p.getLevel());
            if (xpLabel != null) xpLabel.setText("XP: " + p.getExperiencePoints() + "/" + p.getExperienceToNextLevel());
            if (burgerLabel != null) burgerLabel.setText("BURGERS: " + p.getBurger());
            if (currencyLabel != null) currencyLabel.setText("COINS: " + p.getCurrency());
        }
    }
}