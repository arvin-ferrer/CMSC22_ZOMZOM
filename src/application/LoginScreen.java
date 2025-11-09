package application;

import java.nio.file.Path;
import java.nio.file.Paths; // Make sure this is imported if using savePath directly
import java.util.ArrayList; // Make sure this is imported for saving

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene; // Import Scene
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage; 

public class LoginScreen {
	
	private Main mainApp;

    public LoginScreen(Main mainApp) {
        this.mainApp = mainApp;
    }


    public void showLoginScreen() {
        Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        
      //grid id for CSS styling
        loginGrid.setId("login-grid"); 

        Text scenetitle = new Text("Welcome, Survivor");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        scenetitle.setId("welcome-text"); 
        loginGrid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("Username:");
        loginGrid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        loginGrid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        loginGrid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        loginGrid.add(pwBox, 1, 2);

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().addAll(registerBtn, loginBtn);
        loginGrid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        actiontarget.setId("action-target"); 
        loginGrid.add(actiontarget, 1, 6);

        //Login button
        loginBtn.setOnAction(e -> {
            String user = userTextField.getText();
            String pass = pwBox.getText();
            
            Player foundPlayer = null;
            for (Player p : mainApp.getData()) { 
                if (p.getUsername().equals(user) && p.checkPassword(pass)) {
                    foundPlayer = p;
                    break;
                }
            }

            if (foundPlayer != null) {
                mainApp.setCurrentPlayer(foundPlayer); 
                mainApp.showDashboardScreen(); 
            } else {
                actiontarget.setText("Invalid username or password");
                
            }
        });
        
        registerBtn.setOnAction(e -> {
            String user = userTextField.getText();
            String pass = pwBox.getText();

            if (user.isEmpty() || pass.isEmpty()) {
                actiontarget.setText("Username and password cannot be empty.");
                return;
            }

            boolean userExists = false;
            for (Player p : mainApp.getData()) { 
                if (p.getUsername().equals(user)) {
                    userExists = true;
                    break;
                }
            }

            if (userExists) {
                actiontarget.setText("Username '" + user + "' is already taken.");
            } else {
                Player newPlayer = new Player(user, pass);
                mainApp.getData().add(newPlayer); 
                Login.saveUsers(new ArrayList<>(mainApp.getData()), mainApp.getSavePath()); 
                mainApp.setCurrentPlayer(newPlayer);
                mainApp.showDashboardScreen(); 
            }
        });


        StackPane rootPane = new StackPane();
        rootPane.setId("login-background-panel"); // ID for the background image
//        creating Title
        Text rootTitle = new Text("ZOMZOM"); 
        Text rootSubtitle = new Text("Survive the Apocalypse");
        rootTitle.setId("root-title");
        rootSubtitle.setId("root-subtitle");
        // align the title to the top-center
        StackPane.setAlignment(rootTitle, Pos.TOP_CENTER); 
        StackPane.setAlignment(rootSubtitle, Pos.TOP_CENTER);
        StackPane.setMargin(rootTitle, new Insets(50, 0, 0, 0)); 
        StackPane.setMargin(rootSubtitle, new Insets(115, 0, 0, 0));
        rootPane.getChildren().addAll(loginGrid, rootTitle, rootSubtitle); 
        Scene scene = new Scene(rootPane, 800, 600); 
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM - Login");
        mainApp.getPrimaryStage().show();
    	
    }
}