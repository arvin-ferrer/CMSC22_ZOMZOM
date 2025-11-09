package application;
	
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	 private ObservableList<Player> data;
	 private Path savePath = Paths.get("src/storage/userLogin.txt");
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
		ArrayList<Player> users = new ArrayList<>();
//        users.add(new Player("bro", "bro123", 2, 50, 200));
//        users.add(new Player("bro1", "bro123", 3, 40, 100));
//        users.add(new Player("bro2", "bro123", 1, 20, 220));
//        users.add(new Player("bro3", "bro123", 4, 30, 150));
		
        Login.saveUsers(users, savePath);
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
