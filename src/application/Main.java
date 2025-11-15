package application;
	
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class Main extends Application {
	 private ObservableList<Player> data;
	 private Path savePath = Paths.get("src/storage/userLogin.txt");
	 private Stage primaryStage;
	 private Player currentPlayer;
	 
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		
        // Loading existing users
        ArrayList<Player> loadedData = Login.loadUsers(savePath);
        data = FXCollections.observableArrayList(loadedData);
        
//        ArrayList<Player> users = new ArrayList<>();
//        users.add(new Player("bro", "bro123", 2, 50, 200));
//        users.add(new Player("bro1", "bro123", 3, 40, 100));
//        Login.saveUsers(users, savePath);
//        System.out.println("--- Initial users saved! ---");

    	showLoginScreen();
    }
    	
    	
//    	 creates and shows the Login Screen.
    	
    	public void showLoginScreen() {
    		LoginScreen loginScreen = new LoginScreen(this);
    		loginScreen.showLoginScreen(); // Make sure this method exists in LoginScreen
    	}
    	
    	// creates and shows the dashboard Screen
    	public void showDashboardScreen() {
    		DashboardScreen dashboard = new DashboardScreen(this);
    		dashboard.showScreen();
    	}
    	
   
    	public void showWarAreaScreen() {
    	    WarAreaScreen warArea = new WarAreaScreen(this);
    	    warArea.showScreen();
    	}
        public ObservableList<Player> getData() {
            return data;
        }

        public Stage getPrimaryStage() {
            return primaryStage;
        }
        
        public Path getSavePath() {
            return savePath;
        }

        public Player getCurrentPlayer() {
            return currentPlayer;
        }

        public void setCurrentPlayer(Player currentPlayer) {
            this.currentPlayer = currentPlayer;
        }
    	

	public static void main(String[] args) {
		launch(args);
	}
}