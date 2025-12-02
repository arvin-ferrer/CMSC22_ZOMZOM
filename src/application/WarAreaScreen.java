package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Soldiers.Archer;
import Soldiers.Barrier;
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
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class WarAreaScreen {

    private Main mainApp;
    private AnimationTimer gameLoop;

    private GameMap gameMap;
    private StackPane gamePane;
    private StackPane sceneRoot;
    private Scene scene;
    
    private Player player;
    private List<Zombie> zombies;
    private List<Soldier> soldiers;
    private List<Projectile> projectiles;
    
    private java.util.Map<Soldier, Double> soldierAttackTimers;
    private java.util.Map<Zombie, Double> zombieAttackTimers;
    
    private long lastUpdateTime = 0;
    private Random random;
    private double spawnTimer = 0;
    
    // Labels for the UI
    private Label burgerLabel;
    private Label coinLabel;
    
    private String selectedSoldierType = null;
    private ImageView selectedCardView = null;
  
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new GameMap();
        this.zombies = new ArrayList<>();
        this.random = new Random();
        this.soldiers = new ArrayList<>();
        
        // Level 1, 0 XP, 0 Coins, 500 Burgers to start the fight
//        this.player = new Player("Survivor", "pass", 1, 0, 0, 500); 
        this.player = mainApp.getCurrentPlayer(); // Use the getter from Main
        this.projectiles = new ArrayList<>();
        this.soldierAttackTimers = new HashMap<>();
        this.zombieAttackTimers = new HashMap<>();
        
        gamePane = new StackPane();
        gamePane.setId("war-area-background");
        gamePane.setPrefSize(1280, 720);
        gamePane.setMaxSize(1280, 720);

        // Clickable House
        Pane houseClickArea = new Pane();
//        houseClickArea.setPrefSize(250, 720);
        houseClickArea.setMaxSize(200, 320);
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
        ImageView barrierCard = createCard("/assets/barrier-card.png", Soldier.BARRIER);
        seedBank.getChildren().addAll(archerCard, spearmanCard, barrierCard);
        StackPane.setAlignment(seedBank, Pos.TOP_LEFT);
        StackPane.setMargin(seedBank, new Insets(20, 0, 0, 20));
        gamePane.getChildren().add(seedBank);

        // RESOURCE STATUS BAR (Burgers & Coins) 
        HBox resourceBar = new HBox(20); 
        resourceBar.setAlignment(Pos.CENTER_LEFT);
        resourceBar.setPadding(new Insets(5, 15, 5, 15));
        resourceBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-background-radius: 15;");
        resourceBar.setMaxHeight(50);
        resourceBar.setMaxWidth(300);

        HBox burgerBox = new HBox(10);
        burgerBox.setAlignment(Pos.CENTER_LEFT);
        ImageView burgerIcon = new ImageView();
        try {
            burgerIcon.setImage(new Image(getClass().getResourceAsStream("/assets/burger-sprite.png"))); 
        } catch (Exception e) { System.out.println("burger sprite not found"); }
        burgerIcon.setFitWidth(32);
        burgerIcon.setFitHeight(32);
        
        burgerLabel = new Label("0"); // Initial value
        burgerLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: white;");
        burgerBox.getChildren().addAll(burgerIcon, burgerLabel);

        HBox coinBox = new HBox(10);
        coinBox.setAlignment(Pos.CENTER_LEFT);
        ImageView coinIcon = new ImageView();
        try {
            coinIcon.setImage(new Image(getClass().getResourceAsStream("/assets/coin-sprite.gif"))); 
        } catch (Exception e) { System.out.println("coin sprite not found"); }
        coinIcon.setFitWidth(32);
        coinIcon.setFitHeight(32);
        
        coinLabel = new Label("0"); // Initial value
        coinLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: gold;");
        coinBox.getChildren().addAll(coinIcon, coinLabel);

        resourceBar.getChildren().addAll(burgerBox, coinBox);

        StackPane.setAlignment(resourceBar, Pos.TOP_LEFT);
        StackPane.setMargin(resourceBar, new Insets(30, 0, 0, 550)); 
        
        gamePane.getChildren().add(resourceBar);
        // ----------------------------------------------------

        sceneRoot = new StackPane();
        sceneRoot.setId("scene-root");
        sceneRoot.getChildren().add(gamePane);
        StackPane.setAlignment(gamePane, Pos.CENTER);

        this.scene = new Scene(sceneRoot, 1280, 720);
        this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        // Font loading
        try {
            Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12);
        } catch (Exception e) { System.out.println("Font not found"); }
        this.player.setBurger(5000);
        // Initial Spawns
        spawnZombie(0);
        spawnZombie(2);
        spawnZombie(4);
    }

    public void showScreen() {
    	mainApp.getPrimaryStage().setResizable(false);
        mainApp.getPrimaryStage().setScene(this.scene);
        mainApp.getPrimaryStage().setTitle("ZOMZOM 2.0 - War Area");
        mainApp.getPrimaryStage().show();

        lastUpdateTime = 0;
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
                
                if (burgerLabel != null) {
                   burgerLabel.setText(String.valueOf(player.getBurger())); 
                }
                if (coinLabel != null) {
                   coinLabel.setText(String.valueOf(player.getCurrency())); 
                }
       
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
                
                
                this.player.addCurrency(zombie.getRewardPoints()); 
                this.player.addBurger(zombie.getBurgerPoints());
//                this.player.addBurger(500); // add burger here
                System.out.println("Zombie killed! Gold: " + this.player.getCurrency());
            }
        }
    }

  
    private ImageView createCard(String imageName, String soldierType) {
        ImageView cardView = new ImageView();
        try {
            cardView.setImage(new Image(getClass().getResourceAsStream(imageName)));
        } catch (Exception e) {
            System.err.println("Could not load card image: " + imageName);
        }
        cardView.setFitWidth(96);
        cardView.setFitHeight(96);
        cardView.setPreserveRatio(true);
        cardView.setCursor(javafx.scene.Cursor.HAND); 
        cardView.setOnMouseClicked(e -> {
            this.selectedSoldierType = soldierType;
            if (selectedCardView != null) selectedCardView.setEffect(null);
            selectedCardView = cardView;
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setWidth(20);
            glow.setHeight(20);
            cardView.setEffect(glow);
        });
        return cardView;
    }

   
    private void updateSoldiers(double deltaTime) {
        for (Soldier soldier : soldiers) {
            if (!soldier.isAlive()) continue;
            double currentCooldown = soldierAttackTimers.getOrDefault(soldier, 0.0);
            if (currentCooldown > 0) {
                soldierAttackTimers.put(soldier, currentCooldown - deltaTime);
                continue;
            }
            if (soldier instanceof Archer) {
                boolean hasTarget = false;
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                        if (z.getImageView().getTranslateX() > soldier.getImageView().getTranslateX() 
                                && z.getPositionX() < 800) { 
                            hasTarget = true; break;
                        }
                    }
                }
                if (hasTarget) {
                    shootProjectile(soldier);
                    soldierAttackTimers.put(soldier, 1.5); 
                }
            } else if (soldier instanceof Spearman) {
                for (Zombie z : zombies) {
                    if (z.isAlive() && z.getLane() == soldier.getLane()) {
                        double dist = z.getImageView().getTranslateX() - soldier.getImageView().getTranslateX();
                        if (dist > 0 && dist < 240) {
                            z.takeDamage(soldier.getDamage());
                            soldierAttackTimers.put(soldier, 2.0); 
                            break;
                        }
                    }
                }
            }
        }
    }

    private void removeDeadSoldiers() {
        Iterator<Soldier> it = soldiers.iterator();
        while(it.hasNext()) {
            Soldier s = it.next();
            if(!s.isAlive()) {
                gamePane.getChildren().remove(s.getImageView());
                int[] pos = s.getPosition();
                gameMap.setSlot(pos[0], pos[1], GameMap.SLOT_EMPTY);
                it.remove();
            }
        }
    }

    private void shootProjectile(Soldier soldier) {
        double startX = soldier.getImageView().getTranslateX() + 50;
        double startY = soldier.getImageView().getTranslateY(); 
        Projectile proj = new Projectile(startX, startY, soldier.getLane(), soldier.getDamage());
        projectiles.add(proj);
        gamePane.getChildren().add(proj.getView());
    }

    private void updateProjectiles(double deltaTime) {
        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.update(deltaTime);
            boolean hit = false;
            for (Zombie z : zombies) {
                if (z.isAlive() && z.getLane() == p.getLane()) {
                    double zX = z.getImageView().getTranslateX();
                    double pX = p.getX();
                    if (pX >= zX && pX <= zX + 80) {
                        z.takeDamage(p.getDamage());
                        hit = true;
                        break; 
                    }
                }
            }
            if (hit || p.getX() > 1280) {
                p.setInactive();
                gamePane.getChildren().remove(p.getView());
                it.remove();
            }
        }
    }

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
    
    private void addSoldier(String soldierType, int col, int lane) {
        
        if (soldierType.equals(Soldier.BARRIER)) {
            // check if there is space (needs 3 slots)
            if (lane + 2 >= GameMap.MAP_HEIGHT_TILES) {
                System.out.println("Cannot place barrier: Not enough vertical space!");
                return;
            }

            // check if all 3 slots are empty
            if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY ||
                gameMap.getSlot(col, lane + 1) != GameMap.SLOT_EMPTY ||
                gameMap.getSlot(col, lane + 2) != GameMap.SLOT_EMPTY) {
                System.out.println("Cannot place barrier: Slots occupied!");
                return;
            }

            int barrierCost = 70; // cost of barrier
            
            if (player.getBurger() >= barrierCost) {
                Soldier mainBarrier = new Soldiers.Barrier(col, lane, barrierCost); 
                soldiers.add(mainBarrier);
                gamePane.getChildren().add(mainBarrier.getImageView());
                gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);

                Soldier dummy1 = new Soldiers.Barrier(col, lane + 1, barrierCost);
                dummy1.getImageView().setVisible(false); // Invisible
                soldiers.add(dummy1); 
                gameMap.setSlot(col, lane + 1, GameMap.SLOT_SOLDIER);

                Soldier dummy2 = new Soldiers.Barrier(col, lane + 2, barrierCost);
                dummy2.getImageView().setVisible(false); // Invisible
                soldiers.add(dummy2); 
                gameMap.setSlot(col, lane + 2, GameMap.SLOT_SOLDIER);

                player.deductBurger(barrierCost);
                System.out.println("Barrier placed.");
            } else {
                System.out.println("Not enough burgers!");
            }
            return; 
        }

        
        if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY) {
             System.out.println("Slot Occupied!");
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
	
	        if (newSoldier != null) {
	            int cost = newSoldier.getSoldierCost();
	            if (player.getBurger() >= cost) {
	                soldiers.add(newSoldier);
	                soldierAttackTimers.put(newSoldier, 0.0);
	                gamePane.getChildren().add(newSoldier.getImageView());
	
	                // DEDUCT BURGER
	                player.deductBurger(cost);
	                System.out.println("Soldier placed. Burgers left: " + player.getBurger());
	
	                gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);
	            } else {
	                System.out.println("Not enough burgers!");
	            }
	        }
	    }
	}