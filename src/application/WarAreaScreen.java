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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class WarAreaScreen {

    private Main mainApp;
    private AnimationTimer gameLoop; // main game loop

    private GameMap gameMap; // handles logic for occupied squares
    private StackPane gamePane;
    private StackPane sceneRoot;
    private Scene scene;
   // list for zombies and soldiers
    private Player player;
    private List<Zombie> zombies;
    private List<Soldier> soldiers;
    
    // list to tract arrows/bullets
    private List<Projectile> projectiles;
    
    // Map to track attack cooldowns
    private java.util.Map<Soldier, Double> soldierAttackTimers;
    
    // Map to to track zombie attack cooldowns
    private java.util.Map<Zombie, Double> zombieAttackTimers;
    private long lastUpdateTime = 0;
    private Random random;
    private double spawnTimer = 0;
    
    
    // for planting
    private String selectedSoldierType = null; // currently clicked
    private ImageView selectedCardView = null; // for visual glow effect
  
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new GameMap(); // initialize the map logic
        this.zombies = new ArrayList<>();
        this.random = new Random();
        this.soldiers = new ArrayList<>();
        this.player = new Player(selectedSoldierType, selectedSoldierType, 500, 0, 0); // starting currency
        this.projectiles = new ArrayList<>();
        this.soldierAttackTimers = new HashMap<>();
        this.zombieAttackTimers = new HashMap<>();
        
        player.setCurrency(500); 

        gamePane = new StackPane();
        gamePane.setId("war-area-background");
        gamePane.setPrefSize(1280, 720);
        gamePane.setMaxSize(1280, 720);

        // transparent click area for house 
        Pane houseClickArea = new Pane();
        houseClickArea.setPrefSize(250, 720);
        houseClickArea.setStyle("-fx-background-color: transparent;");
        houseClickArea.setCursor(javafx.scene.Cursor.HAND);
        houseClickArea.setOnMouseClicked(event -> {
            System.out.println("House clicked! Retreating to safety...");
            if (gameLoop != null) {
                gameLoop.stop();
            }
            mainApp.showHomeScreen();
        });
        StackPane.setAlignment(houseClickArea, Pos.CENTER_LEFT);
        gamePane.getChildren().add(houseClickArea);

        // Grid
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid");
        for (int y = 0; y < GameMap.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < GameMap.MAP_WIDTH_TILES; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
                slot.getStyleClass().add("game-grid-cell");
                final int finalX = x;
                final int finalY = y;
                slot.setOnMouseClicked(event -> {
                    if (selectedSoldierType != null) {
                        addSoldier(selectedSoldierType, finalX, finalY);
                    }
                });
                gameGrid.add(slot, x, y);
            }
        }
        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225));
        gamePane.getChildren().add(gameGrid);

        // Seed Bank
        HBox seedBank = new HBox(10);
        seedBank.setPadding(new Insets(10));
        seedBank.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;");
        seedBank.setMaxHeight(110);
        seedBank.setMaxWidth(400);
        seedBank.setPickOnBounds(false);
        ImageView archerCard = createCard("/assets/archer-card.png", Soldier.ARCHER);
        ImageView spearmanCard = createCard("/assets/spearman-card.png", Soldier.SPEARMAN);
        seedBank.getChildren().addAll(archerCard, spearmanCard);
        StackPane.setAlignment(seedBank, Pos.TOP_LEFT);
        StackPane.setMargin(seedBank, new Insets(20, 0, 0, 20));
        gamePane.getChildren().add(seedBank);

        sceneRoot = new StackPane();
        sceneRoot.setId("scene-root");
        sceneRoot.getChildren().add(gamePane);
        StackPane.setAlignment(gamePane, Pos.CENTER);

        this.scene = new Scene(sceneRoot, 1280, 720);
        this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        // ----------------------------------------

        // Initial Spawns
        spawnZombie(0);
        spawnZombie(2);
        spawnZombie(4);
    }

    public void showScreen() {
    	mainApp.getPrimaryStage().setResizable(false);
        
        // Set the existing scene ---
        mainApp.getPrimaryStage().setScene(this.scene);
        
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - War Area");
        mainApp.getPrimaryStage().show();

        lastUpdateTime = 0;

        // start loop
        createAndStartGameLoop();
    }

    private void createAndStartGameLoop() {
        if (gameLoop != null) {
        	gameLoop.stop(); 
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) {
                    lastUpdateTime = now;
                    return;
                }
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;

                spawnTimer += deltaTime;
                if (spawnTimer >= 5.0) {
                    int randomLane = random.nextInt(6);
                    spawnZombie(randomLane);
                    spawnTimer = 0;
                }

                updateSoldiers(deltaTime);
                updateProjectiles(deltaTime);
                updateZombies(deltaTime);
                removeDeadSoldiers();
            }
        };
        gameLoop.start();
    }

    private void updateZombies(double deltaTime) {
        Iterator<Zombie> iterator = zombies.iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            if (zombie.isAlive()) {
                double zCooldown = zombieAttackTimers.getOrDefault(zombie, 0.0);
                if (zCooldown > 0) zombieAttackTimers.put(zombie, zCooldown - deltaTime);

                boolean isEating = false;
                Soldier victim = null;
                for (Soldier s : soldiers) {
                    if (s.isAlive() && s.getLane() == zombie.getLane()) {
                        double dist = zombie.getImageView().getTranslateX() - s.getImageView().getTranslateX();
                        if (dist > -20 && dist < 80) {
                            isEating = true; victim = s; break;
                        }
                    }
                }

                if (isEating && victim != null) {
                    if (zCooldown <= 0) {
                        victim.takeDamage(zombie.getDamage());
                        zombieAttackTimers.put(zombie, 1.0);
                    }
                } else {
                    zombie.update(deltaTime);
                }

                if (zombie.getImageView().getTranslateX() < -400) {
                    gamePane.getChildren().remove(zombie.getImageView());
                    iterator.remove();
                }
            } else {
                gamePane.getChildren().remove(zombie.getImageView());
                iterator.remove();
            }
        }
    }

    // for creating cards
    private ImageView createCard(String imageName, String soldierType) {
        ImageView cardView = new ImageView();
        try {
            // load image
            cardView.setImage(new Image(getClass().getResourceAsStream(imageName)));
        } catch (Exception e) {
            System.err.println("Could not load card image: " + imageName);
        }
        
        // styling ng card
        cardView.setFitWidth(96);
        cardView.setFitHeight(96);
        cardView.setPreserveRatio(true);
        cardView.setCursor(javafx.scene.Cursor.HAND); // cursor change

        // click logic
        cardView.setOnMouseClicked(e -> {
            // set the selection variable
            this.selectedSoldierType = soldierType;
            System.out.println("Selected Card: " + soldierType);
            
            // visual highlight
            if (selectedCardView != null) {
                selectedCardView.setEffect(null); // Remove glow from previous
            }
            selectedCardView = cardView;
            
            // add yellow glow
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setWidth(20);
            glow.setHeight(20);
            cardView.setEffect(glow);
        });

        return cardView;
    }
    private void updateSoldiers(double deltaTime) {
        // We use a basic loop here. Removal happens in removeDeadSoldiers() to avoid concurrent mod errors.
        for (Soldier soldier : soldiers) {
            if (!soldier.isAlive()) continue;

            // update cooldown
            double currentCooldown = soldierAttackTimers.getOrDefault(soldier, 0.0);
            if (currentCooldown > 0) {
                soldierAttackTimers.put(soldier, currentCooldown - deltaTime);
                continue;
            }

            // ARCHER
            if (soldier instanceof Archer) {
                boolean hasTarget = false;
                
                for (Zombie z : zombies) {
                	
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                        
                    	// check if zombie is to the right of soldier and on screen
                        if (z.getImageView().getTranslateX() > soldier.getImageView().getTranslateX() 
                                && z.getPositionX() < 600) { 
                            hasTarget = true;
                            break;
                        }
                    }
                }
                
                if (hasTarget) {
                    shootProjectile(soldier);
                    soldierAttackTimers.put(soldier, 1.5); 
                }
            }
            // SPEARMAN
            else if (soldier instanceof Spearman) {
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                        double dist = z.getImageView().getTranslateX() - soldier.getImageView().getTranslateX();
                        if (dist > 0 && dist < 120) {
                            z.takeDamage(soldier.getDamage());
                            soldierAttackTimers.put(soldier, 1.0); 
                            break;
                        }
                    }
                }
            }
        }
    }
    

    // handle soldier death and map updates
    private void removeDeadSoldiers() {
        Iterator<Soldier> it = soldiers.iterator();
        while(it.hasNext()) {
            Soldier s = it.next();
            if(!s.isAlive()) {
                // remove visual
                gamePane.getChildren().remove(s.getImageView());
                
                // free up the map slot so we can plant there again
                int[] pos = s.getPosition(); // Returns {col, lane}
                gameMap.setSlot(pos[0], pos[1], GameMap.SLOT_EMPTY);
                
                // remove from logical list
                it.remove();
            }
        }
    }
    
    // for shooting
    private void shootProjectile(Soldier soldier) {
    	//position of the projectile
        double startX = soldier.getImageView().getTranslateX() + 50;
        double startY = soldier.getImageView().getTranslateY(); 
        
       
        Projectile proj = new Projectile(startX, startY, soldier.getLane(), soldier.getDamage());
        
        //add to existing projectiles
        projectiles.add(proj);
        gamePane.getChildren().add(proj.getView());
    }
    
    
    // for moving the projectiles
    private void updateProjectiles(double deltaTime) {
    	
        Iterator<Projectile> it = projectiles.iterator();
        // while there is a present projectile
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(deltaTime);
            
            boolean hit = false;
            // checks every zombie if they are in the lane
            for (Zombie z : zombies) {
                if (z.isAlive() && z.getLane() == p.getLane()) {
                    double zX = z.getImageView().getTranslateX();
                    double pX = p.getX();
                    //
                    
                    //if collided
                    if (pX >= zX && pX <= zX + 80) {
                        z.takeDamage(p.getDamage());
                        hit = true;
                        break; 
                    }
                }
            }
            //removing the projectile if it hit a zombie or out of bounds
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
        int zombieType = random.nextInt(3); 
        
        Zombie newZombie = null;

        switch (zombieType) {
            case 0: newZombie = new NormalZombie(startX, lane); break;
            case 1: newZombie = new TankZombie(startX, lane); break;
            case 2: newZombie = new nurseZombie(startX, lane); break;
        }

        if (newZombie != null) {
            zombies.add(newZombie);
            gamePane.getChildren().add(newZombie.getImageView());
        }
    }
    
    
    // checks GameMap before adding
    private void addSoldier(String soldierType, int col, int lane) {
        // if slot is occupied
        if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY) {
            System.out.println("Cannot place soldier at " + col + "," + lane + " - Slot Occupied!");
            return;
        }
    
    	Soldier newSoldier = null;
    	switch (soldierType) {
    		case Soldier.ARCHER:
    			newSoldier = new Archer(col, lane, 50);
    			break;
    		case Soldier.SPEARMAN:
    			newSoldier = new Spearman(col, lane, 70);
    			break;
    	}
    	 System.out.println(player.getCurrency() + " currency left.");
         System.out.println(newSoldier.getSoldierCost() + " cost of soldier.");
    	if (newSoldier != null && player.getCurrency() >= newSoldier.getSoldierCost()) {
            soldiers.add(newSoldier);
            soldierAttackTimers.put(newSoldier, 0.0);
            gamePane.getChildren().add(newSoldier.getImageView());
            player.deductCurrency(newSoldier.getSoldierCost());
            System.out.println(player.getCurrency() + " currency left.");
            System.out.println(newSoldier.getSoldierCost() + " cost of soldier.");
            //mark as occupied
            gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);
        }
    }
}