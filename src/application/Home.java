package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class Home {

    private Main mainApp;

    public Home(Main mainApp) {
        this.mainApp = mainApp;
    }

    public void showScreen() {
        StackPane rootPane = new StackPane();
        rootPane.setId("home-screen-background"); // id for css
        rootPane.setPrefSize(1280, 720);

        Button returnButton = new Button("RETURN TO BATTLE");
        
        returnButton.getStyleClass().add("dashboard-button"); 

        returnButton.setOnAction(e -> {
            System.out.println("Exiting safe house");
            mainApp.showWarAreaScreen();
        });

        StackPane.setAlignment(returnButton, Pos.BOTTOM_CENTER);
        StackPane.setMargin(returnButton, new Insets(0, 0, 50, 0));

        rootPane.getChildren().add(returnButton);

        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

      
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("Safe House Interior");
        mainApp.getPrimaryStage().show();
    }
}