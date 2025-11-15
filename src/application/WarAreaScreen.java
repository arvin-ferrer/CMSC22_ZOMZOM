package application;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WarAreaScreen {

    private Main mainApp;
    private Map gameMap; 
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new Map();
        
    }

    public void showScreen() {
        
        StackPane rootPane = new StackPane();
        rootPane.setId("war-area-background"); // ID for CSS
        rootPane.setPrefSize(1280, 720); // set the size of the root
        
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid"); // ID for CSS
        // gameGrid.setGridLinesVisible(true); // uncomment to see grid
        
        //populate the grid with 70 transparent panes (slots)
        for (int y = 0; y < Map.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < Map.MAP_WIDTH_TILES; x++) {
            	Pane slot = new Pane();
                slot.setPrefSize(Map.TILE_WIDTH, Map.TILE_HEIGHT);
                slot.getStyleClass().add("game-grid-cell"); // style for the slot
                
                // add this slot to the grid
                gameGrid.add(slot, x, y);
            }
        }

        // this is for the position of the grid on the background
        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        
        // Top: ~150px, Left: ~25px wag nyo burahin tong comment na toh pls lang
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225)); 
        
        rootPane.getChildren().add(gameGrid);

        // scene size same as the bg image
        Scene scene = new Scene(rootPane, 1280, 720);
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        // might do this later 
//        Font.loadFont(getClass().getResourceAsStream("/application/fonts/PressStart2P-Regular.ttf"), 12);

        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - War Area");
        mainApp.getPrimaryStage().show();

        // 
        // game loop to move zombies, check for collisions, etc.
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // updateGame();
            }
        }.start();
    }
}