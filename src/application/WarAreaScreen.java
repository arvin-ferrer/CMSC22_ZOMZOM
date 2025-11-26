package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Soldiers.Archer;
import Soldiers.Projectile;
import Soldiers.Soldier;
import Soldiers.Spearman;
import Zombies.NormalZombie;
import Zombies.TankZombie;
import Zombies.Zombie;
import Zombies.nurseZombie;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class WarAreaScreen {

    private Main mainApp;
    private GameMap gameMap;
    private StackPane gamePane; 
    
    // list for zombies and soldiers
    private List<Zombie> zombies;
    private List<Soldier> soldiers;
    
    // list to track arrows/bullets
    private List<Projectile> projectiles;
    
    // Map to track attack cooldowns (Soldier -> Time remaining)
    private java.util.Map<Soldier, Double> soldierAttackTimers;
    
    private long lastUpdateTime = 0;
    
    // randomizer
    private Random random;
    private double spawnTimer = 0;
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new GameMap();
        this.zombies = new ArrayList<>();
        this.random = new Random(); 
        this.soldiers = new ArrayList<>();
        
        // new lists
        this.projectiles = new ArrayList<>();
        this.soldierAttackTimers = new HashMap<>();
     }

    public void showScreen() {
        gamePane = new StackPane(); 
        gamePane.setId("war-area-background"); 
        gamePane.setPrefSize(1280, 720); 
        gamePane.setMaxSize(1280, 720); 
        
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid"); 
        
        // for calculating the placement  tiles
        for (int y = 0; y < GameMap.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < GameMap.MAP_WIDTH_TILES; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
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
        
        // game scene
        StackPane sceneRoot = new StackPane();
        sceneRoot.setId("scene-root"); 
        sceneRoot.getChildren().add(gamePane); 
        StackPane.setAlignment(gamePane, Pos.CENTER); 
        
        // main scene
        Scene scene = new Scene(sceneRoot, 1280, 720); 
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        //adding the scene
        mainApp.getPrimaryStage().setScene(scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - War Area");
        mainApp.getPrimaryStage().show();
        
        //adds soldier (for testting)
        addSoldier(Soldier.ARCHER,0,0);
        addSoldier(Soldier.ARCHER,0,1);
        addSoldier(Soldier.ARCHER,0,2);
        addSoldier(Soldier.ARCHER,0,3);
        addSoldier(Soldier.ARCHER,0,4);
        addSoldier(Soldier.ARCHER,0,5);
        
        addSoldier(Soldier.SPEARMAN, 1,0);
       
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
                
                // -------------------------------------------------------------
                // update Soldiers and Projectiles
                updateSoldiers(deltaTime);
                updateProjectiles(deltaTime);
                // -------------------------------------------------------------

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
                    } else {
                        // remove dead zombies from screen if killed by arrows
                        gamePane.getChildren().remove(zombie.getImageView());
                        iterator.remove();
                    }
                }
            }
        }.start();
    }
    
    // COMBAT LOGIC METHODS ====================================================================
    
    private void updateSoldiers(double deltaTime) {
        for (Soldier soldier : soldiers) {
            // update cooldown
            double currentCooldown = soldierAttackTimers.getOrDefault(soldier, 0.0);
            if (currentCooldown > 0) {
                soldierAttackTimers.put(soldier, currentCooldown - deltaTime);
                continue;
            }

            // ARCHER ============================================================================================
            if (soldier instanceof Archer) {
            	// for checking if it has target
                boolean hasTarget = false;
                
                //checks every current existing zombie
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                        // if zombie is within the screen
                        if (z.getImageView().getTranslateX() > soldier.getImageView().getTranslateX() && z.getPositionX() < 600) {
                            hasTarget = true;
                            break;
                        }
                    }
                }
                
                //shoots if there is a valid target
                if (hasTarget) {
                    shootProjectile(soldier);
                    soldierAttackTimers.put(soldier, 1.5); // 1.5 sec cooldown
                }
            }
            // SPEARMAN ===================================================================================
            else if (soldier instanceof Spearman) {
            	 //checks every current existing zombie
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                    	
                        double dist = z.getImageView().getTranslateX() - soldier.getImageView().getTranslateX();
                        
                        // close range check 
                        if (dist > 0 && dist < 120) {
                            z.takeDamage(soldier.getDamage());
                            soldierAttackTimers.put(soldier, 1.0); // 1 sec cooldown
                            break;
                        }
                    }
                }
            }
        }
    }
    
    // logic for shooting
    private void shootProjectile(Soldier soldier) {
        //calculate spawn position 
        // 50 and 40 are offsets to make the arrow appear near the center of the soldier
        double startX = soldier.getImageView().getTranslateX() + 50;
        double startY = soldier.getImageView().getTranslateY(); 
        
        Projectile proj = new Projectile(startX, startY, soldier.getLane(), soldier.getDamage());
        
        projectiles.add(proj);
        gamePane.getChildren().add(proj.getView());
    }
    
    //for updating the position of projectile
    private void updateProjectiles(double deltaTime) {
    	// iterator for projectiles
        Iterator<Projectile> it = projectiles.iterator();
        
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(deltaTime);
            
            boolean hit = false;
            
            // check collision
            for (Zombie z : zombies) {
                if (z.isAlive() && z.getLane() == p.getLane()) {
                    double zX = z.getImageView().getTranslateX();
                    double pX = p.getX();
                    
                    // simple collision if arrow is within zombie's width around 80px
                    if (pX >= zX && pX <= zX + 80) {
                        z.takeDamage(p.getDamage());
                        hit = true;
                        break; 
                    }
                }
            }
            
            // remove if hit or off screen
            if (hit || p.getX() > 800) {
                p.setInactive();
                gamePane.getChildren().remove(p.getView());
                it.remove();
            }
        }
    }
    
    // SPAWNERS =================================================================

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
    
    private void addSoldier(String soldierType, int col, int lane) {
    	Soldier newSoldier = null;
    	switch (soldierType) {
    		case Soldier.ARCHER:
    			newSoldier = new Archer(col, lane);
    			break;
    		case Soldier.SPEARMAN:
    			newSoldier = new Spearman(col, lane);
    			break;
    			
    	}
    	
    	if (newSoldier != null) {
            soldiers.add(newSoldier);
            // Initialize cooldown
            soldierAttackTimers.put(newSoldier, 0.0);
            gamePane.getChildren().add(newSoldier.getImageView());
        }
    }
}