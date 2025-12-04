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
import javafx.util.Duration;
import javafx.scene.image.Image;

public class DashboardScreen {

    private Main mainApp;
    
    // Class fields for labels
    private Label levelLabel;
    private Label xpLabel;
    private Label currencyLabel; 
    private Label burgerLabel;   
    
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
        
        String[] names = {
            "Stone", "Cloth", "Burger", "Gunpowder", "Grenade", "Medkit", "Gold"
        };

        String[] paths = {
            "/assets/stone.png", "/assets/whiteCloth.png", "/assets/burger-sprite.png", 
            "/assets/gunpowder.png", "/assets/grenade-sprite.png", "/assets/medkit.png", 
            "/assets/coin-sprite.gif"
        };

        String[] descs = {
            "Building Material", "Crafting Material", "Restores Hunger", 
            "Crafting Material", "Explosive Damage", "Heals 50 HP", "Currency"
        };
      
        int[] weights = { 50, 50, 25, 25, 1, 10, 10 };
        String[] rarityTags = { "COMMON", "COMMON", "UNCOMMON", "UNCOMMON", "LEGENDARY", "RARE", "RARE" };

        int calculatedItemIndex = 0; 
        String imagePathToLoad = "/assets/medkit.png"; // Default

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
                    int idx = finalItemIndex;
                    InventoryItem newItem = new InventoryItem(names[idx], paths[idx], descs[idx]);
                    player.addItem(newItem);
                    
                    String rarity = rarityTags[idx];
                    if (rarity.equals("LEGENDARY")) {
                    	SoundManager.getInstance().playSFX("/assets/claim.mp3");
                        message = "OMG!! LEGENDARY DROP! You found " + names[idx] + "!";
                    } else if (rarity.equals("RARE")) {
                    	SoundManager.getInstance().playSFX("/assets/claim.mp3");

                        message = "Great find! You got a Rare " + names[idx] + "!";
                    } else {
                    	SoundManager.getInstance().playSFX("/assets/claim.mp3");

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

    // --- NEW HELPER FOR ABOUT/CREDITS OVERLAY ---
    private void showInfoOverlay(String imagePath) {
        StackPane root = (StackPane) mainApp.getPrimaryStage().getScene().getRoot();

        // 1. Create a dimmer background
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.85);"); // Dark background

        // 2. Load the Image
        ImageView imageView = new ImageView();
        try {
            // Tries to load from /assets/ folder
            imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.out.println("Could not load overlay image: " + imagePath);
        }
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(600); // Adjust size as needed

        // 3. Create Close Button
        Button closeButton = new Button("CLOSE");
        closeButton.getStyleClass().add("dashboard-button");
        closeButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-size: 18px;");
        closeButton.setOnAction(e -> root.getChildren().remove(overlay));

        // 4. Arrange Layout
        VBox contentBox = new VBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(imageView, closeButton);

        overlay.getChildren().add(contentBox);
        root.getChildren().add(overlay);
    }

    public void showScreen() {
        try {
            Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        } catch (Exception e) {}
        
        Player currentPlayer = mainApp.getCurrentPlayer();
        SoundManager.getInstance().playMusic("/assets/bgmusic.mp3");
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
        
        levelLabel = new Label("LEVEL: " + currentPlayer.getLevel());
        xpLabel = new Label("XP: " + currentPlayer.getExperiencePoints() + "/" + currentPlayer.getExperienceToNextLevel());
        currencyLabel = new Label("COINS: " + currentPlayer.getCurrency());
        burgerLabel = new Label("BURGERS: " + currentPlayer.getBurger());

        levelLabel.getStyleClass().add("stat-label");
        xpLabel.getStyleClass().add("stat-label");
        currencyLabel.getStyleClass().add("stat-label");
        burgerLabel.getStyleClass().add("stat-label"); 
        
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

        // --- UPDATED BUTTONS START ---
        
        // Replaced "Stats" with "About"
        Button aboutButton = new Button("About");
        aboutButton.getStyleClass().add("dashboard-button"); 

        // Added "Credits" Button
        Button creditsButton = new Button("Credits");
        creditsButton.getStyleClass().add("dashboard-button"); 

        // --- UPDATED BUTTONS END ---

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("dashboard-button"); 

        buttonMenu.getChildren().addAll(
                playButton,
                inventoryButton,
                claimRewardButton,
                aboutButton,    // Added
                creditsButton,  // Added
                logoutButton
        );
        
        inventoryButton.setOnAction(e -> {
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound
            mainApp.showHomeScreenInventory();
        });
        
        logoutButton.setOnAction(e -> {
            Login.saveUsers(new java.util.ArrayList<>(mainApp.getData()), mainApp.getSavePath());
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound
            System.out.println("Game saved successfully.");
            mainApp.setCurrentPlayer(null); 
            mainApp.showLoginScreen();    
        });
        
        playButton.setOnAction(e -> {
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound

            mainApp.showWarAreaScreen(); 
        });
        
        claimRewardButton.setOnAction(e -> {
            claimReward();
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound

            updateLabels(); 
        });

        aboutButton.setOnAction(e -> {
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound

            showInfoOverlay("/assets/about.jpg");
        });

        creditsButton.setOnAction(e -> {
        	SoundManager.getInstance().playSFX("/assets/click.mp3"); // Play Sound

            showInfoOverlay("/assets/credits.jpg");
        });
        // ---------------------------------------

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