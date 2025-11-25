package application;

import java.util.ArrayList;
import java.util.List; 	
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
    
    private StackPane gamePane; 
    private List<Zombie> zombies;
    private long lastUpdateTime = 0;
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new Map();
        this.zombies = new ArrayList<>(); 
     }

    public void showScreen() {
        gamePane = new StackPane(); 
        gamePane.setId("war-area-background"); 
        gamePane.setPrefSize(1280, 720); 
        gamePane.setMaxSize(1280, 720);  // lock the size 
        
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid"); 
        // gameGrid.setGridLinesVisible(true); // Uncomment to see grid lines for debugging
        
        // populate the grid with 70 transparent panes (slots)
        for (int y = 0; y < Map.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < Map.MAP_WIDTH_TILES; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(Map.TILE_WIDTH, Map.TILE_HEIGHT);
                slot.getStyleClass().add("game-grid-cell"); // Style for the slot
                gameGrid.add(slot, x, y);
            }
        }

        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        
        // Top: ~150px, Left: ~225px 
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225)); 
        
        gamePane.getChildren().add(gameGrid);

        spawnZombie(0);
        spawnZombie(1);
        spawnZombie(2);
        spawnZombie(3);
        spawnZombie(4);
        spawnZombie(5);

      StackPane sceneRoot = new StackPane();
        sceneRoot.setId("scene-root"); 
        sceneRoot.getChildren().add(gamePane); 
        StackPane.setAlignment(gamePane, Pos.CENTER); 

        Scene scene = new Scene(sceneRoot, 1280, 720); // window size
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        // Load font (if needed)
        // Font.loadFont(getClass().getResourceAsStream("/application/fonts/PressStart2P-Regular.ttf"), 12);

        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - War Area");
        mainApp.getPrimaryStage().show();
        
        // main game loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                    return;
                }
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0; 
                lastUpdateTime = now;
                
                // remove dead zombies from the list and pane, later na toh
                for (Zombie zombie : zombies) {
                    if (zombie.isAlive()) { // Only update if alive
                        zombie.update(deltaTime);
                    }
                }
                
                // other game updates (soldiers, projectiles, etc.) add here
            }
        }.start();
    }
    
    private void spawnZombie(int lane) {
    	// starting position of the zombie 
    	double startX = 1280; 
        
        NormalZombie zombie = new NormalZombie(startX, lane);
        TankZombie tankZombie = new TankZombie(startX, lane);
        nurseZombie nurse = new nurseZombie(startX, lane);
        zombies.add(zombie); // keep track of the zombie
        zombies.add(tankZombie);
        zombies.add(nurse);
        gamePane.getChildren().add(zombie.getImageView());
    	gamePane.getChildren().add(tankZombie.getImageView());
    	gamePane.getChildren().add(nurse.getImageView());
    }
}