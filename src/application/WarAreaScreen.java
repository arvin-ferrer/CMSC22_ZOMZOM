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
import java.util.Iterator;
import java.util.Random; // 1. Import Random

public class WarAreaScreen {

    private Main mainApp;
    private Map gameMap;
    
    private StackPane gamePane; 
    private List<Zombie> zombies;
    private long lastUpdateTime = 0;
    
    // randomizer
    private Random random;
    private double spawnTimer = 0;
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new Map();
        this.zombies = new ArrayList<>();
        this.random = new Random(); 
     }

    public void showScreen() {
        gamePane = new StackPane(); 
        gamePane.setId("war-area-background"); 
        gamePane.setPrefSize(1280, 720); 
        gamePane.setMaxSize(1280, 720); 
        
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid"); 
        
        for (int y = 0; y < Map.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < Map.MAP_WIDTH_TILES; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(Map.TILE_WIDTH, Map.TILE_HEIGHT);
                slot.getStyleClass().add("game-grid-cell"); 
                gameGrid.add(slot, x, y);
            }
        }
        
        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225)); 
        
        gamePane.getChildren().add(gameGrid);

        // initial zombie wave
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

        Scene scene = new Scene(sceneRoot, 1280, 720); 
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
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
                
                // for spawning
                spawnTimer += deltaTime;
                
                // spawns every 3 seconds
                if (spawnTimer >= 3.0) {
                    // random number for choosing spawn lane
                    int randomLane = random.nextInt(6); 
                    spawnZombie(randomLane);
                    
                    // reset timer
                    spawnTimer = 0;
                }
                // for removing zombie-----------------------------
                
                //iteratator for current zombies
                Iterator<Zombie> iterator = zombies.iterator();
                //loop for constant checking of current zombies
                while (iterator.hasNext()) {
                    Zombie zombie = iterator.next();
                    
                    if (zombie.isAlive()) {
                        zombie.update(deltaTime);
                        
                        //gets the x coordinate of zombie
                        double currentX = zombie.getImageView().getTranslateX();
                        
                        //removes and unrenders the zombie if it reaches the end;
                        if (currentX < -400) {
                            gamePane.getChildren().remove(zombie.getImageView());
                            iterator.remove();
                        }
                    }
                }
            }
        }.start();
    }
    
    private void spawnZombie(int lane) {
    	double startX = 1280; 
        
       
        // randomizer for zombie types
        int zombieType = random.nextInt(3); // 0, 1, 2
        
        Zombie newZombie = null;

        switch (zombieType) {
            case 0:
                newZombie = new NormalZombie(startX, lane);
                break;
            case 1:
                newZombie = new TankZombie(startX, lane);
                break;
            case 2:
                newZombie = new nurseZombie(startX, lane);
                break;
        }

        if (newZombie != null) {
            zombies.add(newZombie);
            gamePane.getChildren().add(newZombie.getImageView());
        }
    }
}