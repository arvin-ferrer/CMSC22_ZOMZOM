package application;
 
import java.util.ArrayList; 
// JavaFX layout and utility imports
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene; 
// JavaFX control imports (buttons, labels, fields)
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
// JavaFX container/layout imports
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
// Platform and stage control imports
import javafx.application.Platform;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane; 


/**
 * Handles the display and logic for the application's login and registration screen.
 */
public class LoginScreen {
	
	private Main mainApp;

    // Constructor, receives the main application instance
    public LoginScreen(Main mainApp) {
        this.mainApp = mainApp;
    }


    /**
     * Initializes and displays the primary login screen UI.
     */
    public void showLoginScreen() {
        // Loads a custom font resource
        Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        
        // Creates the layout grid for login fields
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10); // Horizontal spacing
        loginGrid.setVgap(10); // Vertical spacing
      
        // Sets an ID for CSS styling
        loginGrid.setId("login-grid"); 

        // Screen Title
        Text scenetitle = new Text("Welcome, Survivor");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.setId("welcome-text"); 
        loginGrid.add(scenetitle, 0, 0, 2, 1); // Add title spanning 2 columns

        // Username Label and Text Field
        Label userName = new Label("Username:");
        loginGrid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        loginGrid.add(userTextField, 1, 1);

        // Password Label and Password Field
        Label pw = new Label("Password:");
        loginGrid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        loginGrid.add(pwBox, 1, 2);

        // Buttons for Login and Register
        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        
        // Horizontal container to hold buttons
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(registerBtn, loginBtn);
        loginGrid.add(hbBtn, 1, 4);

        // Text field to display feedback (errors, success messages)
        final Text actiontarget = new Text();
        actiontarget.setId("action-target"); 
        loginGrid.add(actiontarget, 1, 6);

        // --- Event Handling: Login ---
        loginBtn.setOnAction(e -> {
            String user = userTextField.getText();
            String pass = pwBox.getText();
            
            Player foundPlayer = null;
            // Check credentials against the stored player data
            for (Player p : mainApp.getData()) { 
                if (p.getUsername().equals(user) && p.checkPassword(pass)) {
                    foundPlayer = p;
                    break;
                }
            }

            if (foundPlayer != null) {
                // Successful login: set current player and switch screen
                mainApp.setCurrentPlayer(foundPlayer); 
                mainApp.showDashboardScreen(); 
            } else {
                // Display error message
                actiontarget.setText("Invalid username or password");
            }
        });
        
        // --- Event Handling: Registration ---
        registerBtn.setOnAction(e -> {
            String user = userTextField.getText();
            String pass = pwBox.getText();

            // Validate that fields are not empty
            if (user.isEmpty() || pass.isEmpty()) {
                actiontarget.setText("Username and password cannot be empty.");
                return;
            }

            // Check if username already exists
            boolean userExists = false;
            for (Player p : mainApp.getData()) { 
                if (p.getUsername().equals(user)) {
                    userExists = true;
                    break;
                }
            }

            if (userExists) {
                // Display error if username is taken
                actiontarget.setText("Username '" + user + "' is already taken.");
            } else {
                // Register new player
                Player newPlayer = new Player(user, pass);
                mainApp.getData().add(newPlayer); 
                // Save the updated list of users to file
                Login.saveUsers(new ArrayList<>(mainApp.getData()), mainApp.getSavePath()); 
                mainApp.setCurrentPlayer(newPlayer);
                mainApp.showDashboardScreen(); // Go to dashboard
            }
        });


        // StackPane layers the login grid over the background elements
        StackPane rootPane = new StackPane();
        rootPane.setId("login-background-panel"); // ID for the background image
        
      
        
        // Quit Button setup
        Button quitButton = new Button("QUIT");
        quitButton.setId("quit-button"); // For CSS
        quitButton.setOnAction(e -> Platform.exit()); // Closes the application
        
        StackPane.setAlignment(quitButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(quitButton, new Insets(0, 20, 20, 0)); 

        // Settings Button setup
        Button settingsButton = new Button("SETTINGS");
        settingsButton.setId("settings-button"); 
        settingsButton.setOnAction(e -> showSettingsPopup()); // Calls method to open settings window
        StackPane.setAlignment(settingsButton, Pos.BOTTOM_LEFT);
        StackPane.setMargin(settingsButton, new Insets(0, 0, 20, 20)); 
        
     // Add all UI elements to the main stack pane
        rootPane.getChildren().addAll(loginGrid,  settingsButton, quitButton);

        // --- ADD MARGIN TO THE LOGIN CARD HERE ---
        StackPane.setMargin(loginGrid, new Insets(200, 0, 0, 0));

        
        // Create the scene and apply the stylesheet
        Scene scene = new Scene(rootPane, 1920, 1080); 
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        // Set the scene on the primary stage
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM - Login");
        mainApp.getPrimaryStage().show();
    	
    }
    
    /**
     * Creates and displays a modal settings popup window.
     */
    private void showSettingsPopup() {
        // Create a new window (Stage)
        Stage popupStage = new Stage();
        // Sets modality so the main window cannot be clicked until settings is closed
        popupStage.initModality(Modality.APPLICATION_MODAL); 
        popupStage.initOwner(mainApp.getPrimaryStage());
        popupStage.setTitle("Settings");

        // Layout container for the popup
        BorderPane popupLayout = new BorderPane();
        popupLayout.setId("settings-popup"); 
        popupLayout.setPadding(new Insets(20));
        
        VBox settingsBox = new VBox(15);
        settingsBox.setAlignment(Pos.CENTER);

        // Settings title
        Text settingsTitle = new Text("SETTINGS");
        settingsTitle.setId("settings-title"); 

        // Music Volume Slider
        Label musicLabel = new Label("MUSIC VOLUME");
        musicLabel.getStyleClass().add("settings-label"); 
        Slider musicSlider = new Slider(0, 100, 75); // Min 0, Max 100, Default 75
        musicSlider.setShowTickLabels(true);
        musicSlider.setShowTickMarks(true);

        // Sound Effects (SFX) Slider
        Label sfxLabel = new Label("SOUND FX VOLUME");
        sfxLabel.getStyleClass().add("settings-label");
        Slider sfxSlider = new Slider(0, 100, 90);
        sfxSlider.setShowTickLabels(true);
        sfxSlider.setShowTickMarks(true);
        
        // Close Button
        Button closeButton = new Button("CLOSE");
        closeButton.getStyleClass().add("dashboard-button"); 
        closeButton.setOnAction(e -> popupStage.close());

        // Add sliders and labels to the VBox
        settingsBox.getChildren().addAll(settingsTitle, musicLabel, musicSlider, sfxLabel, sfxSlider);
        
        popupLayout.setCenter(settingsBox);
        popupLayout.setBottom(closeButton);
        BorderPane.setAlignment(closeButton, Pos.CENTER);
        BorderPane.setMargin(closeButton, new Insets(20, 0, 0, 0));

        // Create the scene for the popup
        Scene popupScene = new Scene(popupLayout, 400, 350);
        popupScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm()); 
        
        popupStage.setScene(popupScene);
        popupStage.showAndWait(); // Display the window and wait until it is closed
    }
}