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
    private GameMap gameMap; // handles logic for occupied squares
    private StackPane gamePane; 
    private Player player;
    // list for zombies and soldiers
    private List<Zombie> zombies;
    private List<Soldier> soldiers;
    
    // list to track arrows/bullets
    private List<Projectile> projectiles;
    
    // Map to track attack cooldowns 
    private java.util.Map<Soldier, Double> soldierAttackTimers;
    
    // --- NEW: Map to track Zombie attack cooldowns ---
    private java.util.Map<Zombie, Double> zombieAttackTimers;
    
    private long lastUpdateTime = 0;
    
    // randomizer
    private Random random;
    private double spawnTimer = 0;
    
    // for planting
    private String selectedSoldierType = null; //  currently clicked
    private ImageView selectedCardView = null; // for visual glow effect
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new GameMap(); // initialize the map logic
        this.zombies = new ArrayList<>();
        this.random = new Random(); 
        this.soldiers = new ArrayList<>();
        this.player = new Player(selectedSoldierType, selectedSoldierType, 500, 0, 0); // starting currency
        
        // new lists
        this.projectiles = new ArrayList<>();
        this.soldierAttackTimers = new HashMap<>();
        // Initialize zombie timer map
        this.zombieAttackTimers = new HashMap<>();
        
        player.setCurrency(500); 
     }

    public void showScreen() {
        gamePane = new StackPane(); 
        gamePane.setId("war-area-background"); 
        gamePane.setPrefSize(1280, 720); 
        gamePane.setMaxSize(1280, 720); 
        mainApp.getPrimaryStage().setResizable(false);

        
        GridPane gameGrid = new GridPane();
        gameGrid.setId("game-grid"); 
        
        // for calculating the placement tiles
        for (int y = 0; y < GameMap.MAP_HEIGHT_TILES; y++) {
            for (int x = 0; x < GameMap.MAP_WIDTH_TILES; x++) {
                Pane slot = new Pane();
                slot.setPrefSize(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
                slot.getStyleClass().add("game-grid-cell"); 
                
                // add logic for planting
                
                final int finalX = x;
                final int finalY = y;
                //event handler for adding soldier
                slot.setOnMouseClicked(event -> {
                    // if we have a soldier selected from the cards
                    if (selectedSoldierType != null) {
                        System.out.println("Planting " + selectedSoldierType + " at: " + finalX + ", " + finalY);
                        addSoldier(selectedSoldierType, finalX, finalY);
                        
                        // deselect after planting? If you want to plant multiple, remove these lines.
                         selectedSoldierType = null;
                         if (selectedCardView != null) selectedCardView.setEffect(null);
                    }
                });
                // ------------------------------------------
                
                gameGrid.add(slot, x, y);
            }
        }
        
        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225)); 
        
        gamePane.getChildren().add(gameGrid);
        
        // card ui
        HBox seedBank = new HBox(10); // 10px spacing between cards
        seedBank.setPadding(new Insets(10));
        seedBank.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;"); //semi-transparent back
        seedBank.setMaxHeight(110);
        seedBank.setMaxWidth(400);

        // create the soldier cards
        ImageView archerCard = createCard("/assets/archer-card.png", Soldier.ARCHER);
        ImageView spearmanCard = createCard("/assets/spearman-card.png", Soldier.SPEARMAN);
        
        

        seedBank.getChildren().addAll(archerCard, spearmanCard);
        
        // position the seed bank at Top Left
        StackPane.setAlignment(seedBank, Pos.TOP_LEFT);
        StackPane.setMargin(seedBank, new Insets(20, 0, 0, 20)); 
        gamePane.getChildren().add(seedBank);
        // --------------------------------

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
        
        //adds soldier (for testing)
        addSoldier(Soldier.ARCHER,0,0);
        addSoldier(Soldier.ARCHER,0,1);
        addSoldier(Soldier.ARCHER,0,2);
        
        // test for trying to add another soldier at 0,0. It should fail and print console error.
        addSoldier(Soldier.SPEARMAN,0,0); 
        
        addSoldier(Soldier.ARCHER,0,3);
        addSoldier(Soldier.ARCHER,0,4);
        addSoldier(Soldier.ARCHER,0,5);
        
        addSoldier(Soldier.SPEARMAN, 4,0);
        addSoldier(Soldier.SPEARMAN, 5,1);
        addSoldier(Soldier.SPEARMAN, 6,2);
        addSoldier(Soldier.SPEARMAN, 7,3);
        addSoldier(Soldier.SPEARMAN, 8,4);
        addSoldier(Soldier.SPEARMAN, 9,5);
       
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
                if (spawnTimer >= 3.0) {
                    int randomLane = random.nextInt(6); 
                    spawnZombie(randomLane);
                    int randomSpawnTimer = random.nextInt(4); // 0 to 2 seconds
                    spawnTimer = randomSpawnTimer;
                }
                
                // -------------------------------------------------------------
                updateSoldiers(deltaTime);
                updateProjectiles(deltaTime);
                // -------------------------------------------------------------

                // -------------------------------------------------------------
                // UPDATED: Logic to handle Zombie Movement AND Attacking
                // -------------------------------------------------------------
                Iterator<Zombie> iterator = zombies.iterator();
                while (iterator.hasNext()) {
                    Zombie zombie = iterator.next();
                    
                    if (zombie.isAlive()) {
                        
                        // 1. manage Zombie Attack Timer
                        double zCooldown = zombieAttackTimers.getOrDefault(zombie, 0.0);
                        if (zCooldown > 0) {
                            zombieAttackTimers.put(zombie, zCooldown - deltaTime);
                        }

                        // 2. check for collision with any soldier
                        boolean isEating = false;
                        Soldier victim = null;

                        for (Soldier s : soldiers) {
                            if (s.isAlive() && s.getLane() == zombie.getLane()) {
                                // Calculate distance between Zombie and Soldier
                                double dist = zombie.getImageView().getTranslateX() - s.getImageView().getTranslateX();
                                
                                // Overlap threshold (approx 60-80px depending on sprite size)
                                // If zombie is to the right of soldier, but touching
                                if (dist > -20 && dist < 80) { 
                                    isEating = true;
                                    victim = s;
                                    break; 
                                }
                            }
                        }

                        if (isEating && victim != null) {
                            // ATTACK LOGIC: Don't move, just eat
                            if (zCooldown <= 0) {
                                System.out.println("Zombie attacking soldier!");
                                victim.takeDamage(zombie.getDamage());
                                zombieAttackTimers.put(zombie, 1.0); // 1 second cooldown between bites
                            }
                        } else {
                            // MOVEMENT LOGIC: Only move if not eating
                            zombie.update(deltaTime);
                        }
                        
                        // 3. Remove if out of bounds
                        double currentX = zombie.getImageView().getTranslateX();
                        if (currentX < -400) {
                            gamePane.getChildren().remove(zombie.getImageView());
                            iterator.remove();
                        }
                    } else {
                        // Remove dead zombie
                        gamePane.getChildren().remove(zombie.getImageView());
                        iterator.remove();
                    }
                }
                
                // remove dead Soldiers & free up map squares
                removeDeadSoldiers();
            }
        }.start();
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
    // ------------------------------------

    // COMBAT LOGIC METHODS ====================================================================
    
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