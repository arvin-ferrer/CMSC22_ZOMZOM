package application;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane; 
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class DashboardScreen {

    private Main mainApp;

    // constructor to get the main application
    public DashboardScreen(Main mainWindow) {
        this.mainApp = mainWindow;
    }

    public void showScreen() {
    	Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        Player currentPlayer = mainApp.getCurrentPlayer();

       
        // create the borderpane and apply the style
        BorderPane topBar = new BorderPane();
        topBar.setId("dashboard-top-bar");
        // Padding: top, right, bottom, left
        topBar.setPadding(new Insets(15, 30, 15, 30)); 

        Text scenetitle = new Text("ZOMZOM");
        scenetitle.setId("dashboard-title-text"); 
        topBar.setLeft(scenetitle);
        BorderPane.setAlignment(scenetitle, Pos.CENTER_LEFT); 
        
        //Player Info in the Center
        Label playerInfo = new Label("SURVIVOR: " + currentPlayer.getUsername());
        playerInfo.setId("player-info-label"); 
        topBar.setCenter(playerInfo);
        BorderPane.setAlignment(playerInfo, Pos.CENTER);

        
        HBox statsBox = new HBox(30); // 30px spacing between stats
        statsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label levelLabel = new Label("LEVEL: " + currentPlayer.getLevel());
        Label xpLabel = new Label("XP: " + currentPlayer.getExperiencePoints() + "/" + currentPlayer.getExperienceToNextLevel());
        Label currencyLabel = new Label("COINS: " + currentPlayer.getCurrency());
        
        levelLabel.getStyleClass().add("stat-label");
        xpLabel.getStyleClass().add("stat-label");
        currencyLabel.getStyleClass().add("stat-label");
        
        statsBox.getChildren().addAll(levelLabel, xpLabel, currencyLabel);
        topBar.setRight(statsBox);
        BorderPane.setAlignment(statsBox, Pos.CENTER_RIGHT);


        VBox buttonMenu = new VBox(25); 
        buttonMenu.setAlignment(Pos.CENTER_LEFT);
        buttonMenu.setId("dashboard-vbox"); 

        Text menuTitle = new Text("MAIN MENU");
        menuTitle.setId("dashboard-menu-title"); // ID for styling
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
        
        logoutButton.setOnAction(e -> {
            mainApp.setCurrentPlayer(null); 
//            mainApp.showLoginScreen();    
            Platform.exit(); 
        });
        playButton.setOnAction(e -> {
            mainApp.showWarAreaScreen(); // 
        });

        StackPane rootPane = new StackPane();
        rootPane.setId("dashboard-background-panel"); 
       
        rootPane.getChildren().addAll(topBar, buttonMenu); 

        
        StackPane.setAlignment(topBar, Pos.TOP_CENTER); 
        
        StackPane.setAlignment(buttonMenu, Pos.CENTER_LEFT);
        StackPane.setMargin(buttonMenu, new Insets(100, 0, 0, 50)); 


        Scene scene = new Scene(rootPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM - Dashboard");
        mainApp.getPrimaryStage().show();
    }
}