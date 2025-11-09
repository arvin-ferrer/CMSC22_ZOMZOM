package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DashboardScreen {

    private Main mainApp;

    // constructor to get the main application
    public DashboardScreen(Main mainWindow) {
        this.mainApp = mainWindow;
    }

    public void showScreen() {
        VBox dashboardLayout = new VBox(20);
        dashboardLayout.setAlignment(Pos.CENTER);
        dashboardLayout.setPadding(new Insets(25));

        // get the current player from the main app
        Player currentPlayer = mainApp.getCurrentPlayer();

        Text welcomeText = new Text("Welcome, " + currentPlayer.getUsername());
        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label levelLabel = new Label("Level: " + currentPlayer.getLevel());
        Label xpLabel = new Label("XP: " + currentPlayer.getExperiencePoints() + " / " + currentPlayer.getExperienceToNextLevel());
        Label currencyLabel = new Label("Currency: " + currentPlayer.getCurrency());

        Button inventoryButton = new Button("View Inventory");
        Button rewardButton = new Button("Claim Reward");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            mainApp.setCurrentPlayer(null); // clear the player
            mainApp.showLoginScreen();     // go back to the login screen
        });

        dashboardLayout.getChildren().addAll(
                welcomeText,
                levelLabel,
                xpLabel,
                currencyLabel,
                inventoryButton,
                rewardButton,
                logoutButton
        );

        Scene scene = new Scene(dashboardLayout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM - Dashboard");
    }
}