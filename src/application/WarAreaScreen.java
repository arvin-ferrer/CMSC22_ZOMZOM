package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Items.Item;
import Soldiers.Archer;
import Soldiers.Barrier;
import Soldiers.MainCharacter;
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
import javafx.scene.control.Button;
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
    
    // HP Labels Map for standard soldiers
    private Map<Soldier, Label> healthLabels; 
    
    private java.util.Map<Soldier, Double> soldierAttackTimers;
    private java.util.Map<Zombie, Double> zombieAttackTimers;
    
    private long lastUpdateTime = 0;
    private Random random;
    private double spawnTimer = 0;
    
    // Labels for the UI
    private Label burgerLabel;
    private Label coinLabel;
    private Label levelLabel; 
    
    private String selectedSoldierType = null;
    private ImageView selectedCardView = null;
  
    private MainCharacter mainCharacter;
    
    // NEW: Tracking level changes to trigger effects
    private int trackedLevel;
    
    public WarAreaScreen(Main mainApp) {
        this.mainApp = mainApp;
        this.gameMap = new GameMap();
        this.zombies = new ArrayList<>();
        this.random = new Random();
        this.soldiers = new ArrayList<>();
        this.healthLabels = new HashMap<>(); 
        
        this.player = mainApp.getCurrentPlayer(); 
        this.trackedLevel = player.getLevel(); // Initialize tracker
        
        // 1. SET DEFAULT WEAPON
        this.player.setEquippedWeapon(InventoryItem.HAND);

        this.projectiles = new ArrayList<>();
        this.soldierAttackTimers = new HashMap<>();
        this.zombieAttackTimers = new HashMap<>();
        
        // 2. INITIALIZE GAME PANE FIRST
        gamePane = new StackPane();
        gamePane.setId("war-area-background");
        gamePane.setPrefSize(1280, 720);
        gamePane.setMaxSize(1280, 720);
        
        // 3. SETUP MAIN CHARACTER
        this.mainCharacter = new MainCharacter(0, 2);
        
        // Apply Level Scaling immediately
        this.mainCharacter.applyLevelScaling(player.getLevel());
        
        this.mainCharacter.setWeaponSprite(InventoryItem.HAND); // Set initial sprite
        this.soldiers.add(this.mainCharacter); 
        this.gameMap.setSlot(0, 2, GameMap.SLOT_SOLDIER); 
        
        // Clickable House (Exit)
        Pane houseClickArea = new Pane();
        houseClickArea.setMaxSize(200, 320);
        houseClickArea.setStyle("-fx-background-color: transparent;");
        houseClickArea.setCursor(javafx.scene.Cursor.HAND);
        houseClickArea.setOnMouseClicked(event -> {
            if (gameLoop != null) gameLoop.stop();
            mainApp.showHomeScreen();
        });
        StackPane.setAlignment(houseClickArea, Pos.CENTER_LEFT);
        gamePane.getChildren().add(houseClickArea);
        
        // --- ADD MAIN CHARACTER AND HEALTH BAR (FIXED) ---
        gamePane.getChildren().add(mainCharacter.getImageView());
        gamePane.getChildren().add(mainCharacter.getHealthLabel()); 

        // --- INVENTORY SETUP ---
        player.addItem(new InventoryItem(InventoryItem.BANDAGE, InventoryItem.BANDAGE_IMAGE, "Heals 50 HP"));
        player.addItem(new InventoryItem("Grenade", InventoryItem.GRENADE_IMAGE,"Boom")); 
        player.addItem(new InventoryItem(InventoryItem.STONE, InventoryItem.STONE_IMAGE,"Required for building barriers"));
        player.addItem(new InventoryItem(InventoryItem.CLOTH, InventoryItem.CLOTH_IMAGE,"Required for bandages"));
        player.addItem(new InventoryItem(InventoryItem.MEDKIT, InventoryItem.MEDKIT_IMAGE,"Heals 80 HP"));
        player.addItem(new InventoryItem(InventoryItem.WOOD, InventoryItem.WOOD_IMAGE,"Required for building barriers"));
        
        // Add Weapons
        player.addItem(new InventoryItem(InventoryItem.KATANA, InventoryItem.KATANA_IMAGE, "Slash"));
        player.addItem(new InventoryItem(InventoryItem.MACHINE_GUN, InventoryItem.MACHINE_GUN_IMAGE, "Pew Pew"));
        
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
                    // If soldier selected -> Plant
                    if (selectedSoldierType != null && !selectedSoldierType.equals(Soldier.MAIN_CHARACTER)) {
                        addSoldier(selectedSoldierType, finalX, finalY);
                    }
                    // If nothing selected (or weapon equipped) -> Move Zom
                    else {
                        updateZom(finalX, finalY);
                    }
                });
                gameGrid.add(slot, x, y);
            }
        }
        StackPane.setAlignment(gameGrid, Pos.TOP_LEFT);
        StackPane.setMargin(gameGrid, new Insets(150, 0, 0, 225));
        gamePane.getChildren().add(gameGrid);

        // --- SEED BANK (SOLDIERS + WEAPONS) ---
        HBox seedBank = new HBox(10);
        seedBank.setPadding(new Insets(10));
        seedBank.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;");
        seedBank.setMaxHeight(110);
        seedBank.setMaxWidth(600); 
        seedBank.setPickOnBounds(false);
        
        ImageView archerCard = createCard("/assets/archer-card.png", Soldier.ARCHER);
        ImageView spearmanCard = createCard("/assets/spearman-card.png", Soldier.SPEARMAN);
        
        // Hand Icon (To unequip weapons)
        ImageView handCard = createCard(InventoryItem.HAND_IMAGE, InventoryItem.HAND);

        seedBank.getChildren().addAll(archerCard, spearmanCard, handCard);

        // --- DYNAMICALLY ADD WEAPONS ---
        int weaponsAdded = 0;

        if (findItem(InventoryItem.MALLET) != null && weaponsAdded < 2) {
            seedBank.getChildren().add(createCard(InventoryItem.MALLET_IMAGE, InventoryItem.MALLET));
            weaponsAdded++;
        }
        
        if (findItem(InventoryItem.KATANA) != null && weaponsAdded < 2) {
            seedBank.getChildren().add(createCard(InventoryItem.KATANA_IMAGE, InventoryItem.KATANA));
            weaponsAdded++;
        }
            
        if (findItem(InventoryItem.MACHINE_GUN) != null && weaponsAdded < 2) {
            seedBank.getChildren().add(createCard(InventoryItem.MACHINE_GUN_IMAGE, InventoryItem.MACHINE_GUN));
            weaponsAdded++;
        }

        StackPane.setAlignment(seedBank, Pos.TOP_LEFT);
        StackPane.setMargin(seedBank, new Insets(20, 0, 0, 20));
        gamePane.getChildren().add(seedBank);

        // Resources UI
        HBox resourceBar = new HBox(20); 
        resourceBar.setAlignment(Pos.CENTER_LEFT);
        resourceBar.setPadding(new Insets(5, 15, 5, 15));
        resourceBar.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6); -fx-background-radius: 15;");
        resourceBar.setMaxHeight(50);
        resourceBar.setMaxWidth(550); 

        // Main Menu Button
        Button menuButton = new Button("MAIN MENU");
        menuButton.getStyleClass().add("dashboard-button"); 
        menuButton.setStyle("-fx-background-color: #8B0000; -fx-text-fill: white; -fx-font-family: 'Zombies Brainless'; -fx-font-size: 16px;");
        menuButton.setOnAction(e -> {
            if (gameLoop != null) gameLoop.stop();
            mainApp.showDashboardScreen(); 
        });
        StackPane.setAlignment(menuButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(menuButton, new Insets(0, 30, 30, 0)); 
        gamePane.getChildren().add(menuButton);

        // Stats Labels
        HBox burgerBox = new HBox(10);
        burgerBox.setAlignment(Pos.CENTER_LEFT);
        ImageView burgerIcon = new ImageView();
        try { burgerIcon.setImage(new Image(getClass().getResourceAsStream("/assets/burger-sprite.png"))); } catch (Exception e) {}
        burgerIcon.setFitWidth(32); burgerIcon.setFitHeight(32);
        burgerLabel = new Label("0"); 
        burgerLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: white;");
        burgerBox.getChildren().addAll(burgerIcon, burgerLabel);

        HBox coinBox = new HBox(10);
        coinBox.setAlignment(Pos.CENTER_LEFT);
        ImageView coinIcon = new ImageView();
        try { coinIcon.setImage(new Image(getClass().getResourceAsStream("/assets/coin-sprite.gif"))); } catch (Exception e) {}
        coinIcon.setFitWidth(32); coinIcon.setFitHeight(32);
        coinLabel = new Label("0");
        coinLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: gold;");
        coinBox.getChildren().addAll(coinIcon, coinLabel);

        HBox levelBox = new HBox(10);
        levelBox.setAlignment(Pos.CENTER_LEFT);
        Label lvlTitle = new Label("LVL");
        lvlTitle.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: #39ff14;"); 
        levelLabel = new Label("1 [0/100]");
        levelLabel.setStyle("-fx-font-family: 'Zombies Brainless'; -fx-font-size: 20; -fx-text-fill: white;");
        levelBox.getChildren().addAll(lvlTitle, levelLabel);

        resourceBar.getChildren().addAll(burgerBox, coinBox, levelBox);
        StackPane.setAlignment(resourceBar, Pos.BOTTOM_CENTER);
        StackPane.setMargin(resourceBar, new Insets(30, 100, 0, 0)); 
        gamePane.getChildren().add(resourceBar);

        sceneRoot = new StackPane();
        sceneRoot.setId("scene-root");
        sceneRoot.getChildren().add(gamePane);
        StackPane.setAlignment(gamePane, Pos.CENTER);

        this.scene = new Scene(sceneRoot, 1280, 720);
        this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        
        try { Font.loadFont(getClass().getResourceAsStream("/application/fonts/Zombies Brainless.ttf"), 12); } catch (Exception e) {}
        
        // Item Bank (Consumables Only)
        HBox itemBank = new HBox(10);
        itemBank.setPadding(new Insets(10));
        itemBank.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 10;");
        itemBank.setMaxHeight(110);
        itemBank.setMaxWidth(400);
        itemBank.setPickOnBounds(false);
        
        ImageView grenadeCard = createCard("/assets/grenade-card.png", "Grenade"); 
        ImageView bandageCard = createCard("/assets/bandage-card.png", Item.BANDAGE);
        ImageView medkitCard = createCard("/assets/medkit-card.png", Item.MEDKIT);
        ImageView barrierCard = createCard("/assets/barrier-card.png", Soldier.BARRIER);

        itemBank.getChildren().addAll(grenadeCard, medkitCard, bandageCard, barrierCard);

        StackPane.setAlignment(itemBank, Pos.TOP_RIGHT);
        StackPane.setMargin(itemBank, new Insets(20, 50, 0, 20));
        gamePane.getChildren().add(itemBank);
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
        if (gameLoop != null) gameLoop.stop(); 

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime == 0) { lastUpdateTime = now; return; }
                double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
                lastUpdateTime = now;
                
                updateUI();
                
                // CHECK FOR LEVEL UP
                if (player.getLevel() > trackedLevel) {
                    trackedLevel = player.getLevel();
                    // Update Main Character Stats on Level Up
                    mainCharacter.applyLevelScaling(trackedLevel);
                    System.out.println("WarArea: Level Up detected! Scaling Main Character to level " + trackedLevel);
                }
       
                spawnTimer += deltaTime;
                
                // CALCULATE SPAWN THRESHOLD BASED ON LEVEL
                double spawnThreshold = 5.0; // Default
                if (player.getLevel() >= 50) {
                    spawnThreshold = 1.0;
                } else if (player.getLevel() >= 30) {
                    spawnThreshold = 3.0;
                }
                
                if (spawnTimer >= spawnThreshold) {
                    spawnLevelBasedZombie();
                    spawnTimer = 0;
                }

                updateSoldiers(deltaTime);
                updateProjectiles(deltaTime);
                updateZombies(deltaTime);
                updateGrenades(deltaTime); 
                removeDeadSoldiers();
            }
        };
        gameLoop.start();
    }
    
    private void updateUI() {
        if (burgerLabel != null) burgerLabel.setText(String.valueOf(player.getBurger())); 
        if (coinLabel != null) coinLabel.setText(String.valueOf(player.getCurrency())); 
        
        if (levelLabel != null) {
            levelLabel.setText(player.getLevel() + " [" + player.getExperiencePoints() + "/" + player.getExperienceToNextLevel() + "]");
        }
    }
    
    // --- LEVEL BASED SPAWNER LOGIC ---
    private void spawnLevelBasedZombie() {
        int level = player.getLevel();
        
        // Even Levels: Increase lanes
        // Level 1: 2 + 0 = 2 lanes
        // Level 2: 2 + 1 = 3 lanes
        // Max 6.
        int activeSpawners = 2 + (level / 2);
        if (activeSpawners > 6) activeSpawners = 6;
        
        int randomLane = random.nextInt(activeSpawners);
        
        // Odd Levels: Increase HP/Difficulty
        boolean isOdd = (level % 2 != 0);

        spawnZombie(randomLane, level, isOdd);
    }
    
    // Helper to actually spawn
    private void spawnZombie(int lane, int level, boolean isOdd) {
        double startX = 1280; 
        int zombieType = random.nextInt(3); 
        Zombie newZombie = null;
        switch (zombieType) {
            case 0: newZombie = new NormalZombie(startX, lane); break;
            case 1: newZombie = new TankZombie(startX, lane); break;
            case 2: newZombie = new nurseZombie(startX, lane); break;
        }
        if (newZombie != null) {
            // Apply buffs
            newZombie.applyDifficulty(level, isOdd);
            
            zombies.add(newZombie);
            gamePane.getChildren().add(newZombie.getImageView());
        }
    }

    // --- COMBAT HELPERS ---
    
    private int getWeaponDamage(String weapon) {
        if (weapon == null) return 20;
        switch (weapon) {
            case InventoryItem.KATANA: return 150;     
            case InventoryItem.MALLET: return 80;      
            case InventoryItem.MACHINE_GUN: return 40; 
            default: return 20;                        
        }
    }

    private double getWeaponAttackSpeed(String weapon) {
        if (weapon == null) return 1.0;
        switch (weapon) {
            case InventoryItem.KATANA: return 1.0;
            case InventoryItem.MALLET: return 1.5;
            case InventoryItem.MACHINE_GUN: return 0.2; 
            default: return 1.0;
        }
    }

    private void updateSoldiers(double deltaTime) {
        for (Soldier soldier : soldiers) {
            if (!soldier.isAlive()) continue;
            soldier.update(deltaTime);

            if (healthLabels.containsKey(soldier)) {
                Label hpLabel = healthLabels.get(soldier);
                hpLabel.setText(String.valueOf(soldier.getHealth()));
                hpLabel.setTranslateX(soldier.getImageView().getTranslateX() + 30);
                hpLabel.setTranslateY(soldier.getImageView().getTranslateY() - 20);
                hpLabel.toFront();
            }

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
                                && z.getPositionX() < 600) {
                            hasTarget = true; break;
                        }
                    }
                }
                if (hasTarget) {
                    shootProjectile(soldier);
                    soldierAttackTimers.put(soldier, 1.5); 
                }
            } 
            else if (soldier instanceof Spearman) {
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
            // --- MAIN CHARACTER WEAPON COMBAT ---
            else if (soldier instanceof MainCharacter) {
                String weapon = player.getEquippedWeapon();
                
                // Scale Weapon Damage based on level logic (or just rely on MainCharacter.damage)
                // Since MC damage is already scaled in applyLevelScaling(), we can use that,
                // OR scale the base weapon damage dynamically here. 
                // Let's scale base weapon damage for consistency with switching weapons.
                int baseWepDamage = getWeaponDamage(weapon);
                double dmgMultiplier = 1.0 + (player.getLevel() * 0.05);
                int finalDamage = (int)(baseWepDamage * dmgMultiplier);
                
                double speed = getWeaponAttackSpeed(weapon);
                
                // Machine Gun (Ranged)
                if (InventoryItem.MACHINE_GUN.equals(weapon)) {
                    boolean hasTarget = false;
                    for (Zombie z : zombies) {
                        if (z.isAlive() && z.getLane() == soldier.getLane()) {
                            // Check for zombies to the right
                            if (z.getImageView().getTranslateX() > soldier.getImageView().getTranslateX() 
                                    && z.getPositionX() < 600) { 
                                hasTarget = true; break;
                            }
                        }
                    }
                    if (hasTarget) {
                        shootProjectile(soldier, finalDamage); 
                        soldierAttackTimers.put(soldier, speed); 
                    }
                } 
                // Melee
                else {
                    for (Zombie z : zombies) {
                        if (z.isAlive() && z.getLane() == soldier.getLane()) {
                            double dist = z.getImageView().getTranslateX() - soldier.getImageView().getTranslateX();
                            if (dist > 0 && dist < 120) { 
                                z.takeDamage(finalDamage); 
                                soldierAttackTimers.put(soldier, speed); 
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    // Standard shoot (Soldiers)
    private void shootProjectile(Soldier soldier) {
        shootProjectile(soldier, soldier.getDamage());
    }

    // Overload for Custom Damage (MC)
    private void shootProjectile(Soldier soldier, int dmgOverride) {
        // Adjust Start Position
        double startX = soldier.getImageView().getTranslateX();
        double startY = soldier.getImageView().getTranslateY(); 
        
        String projectileType = "/assets/arrow-sprite.png"; // Default Arrow

        if (soldier instanceof MainCharacter) {
            // USE "BULLET" IF GUN IS EQUIPPED
            if (InventoryItem.MACHINE_GUN.equals(player.getEquippedWeapon())) {
                projectileType = "BULLET"; 
            }
        }

        Projectile proj = new Projectile(startX, startY, soldier.getLane(), dmgOverride, projectileType);
        projectiles.add(proj);
        gamePane.getChildren().add(proj.getView());
    }

    private void createHealthLabel(Soldier s) {
        if (s instanceof Soldiers.Grenade) return;
        if (s instanceof MainCharacter) return; // Do not create duplicate for MC

        Label hpLabel = new Label(String.valueOf(s.getHealth()));
        hpLabel.setStyle("-fx-text-fill: #ff0000; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
        hpLabel.setTranslateX(s.getImageView().getTranslateX() + 30);
        hpLabel.setTranslateY(s.getImageView().getTranslateY() - 20);
        
        gamePane.getChildren().add(hpLabel);
        healthLabels.put(s, hpLabel);
    }

    private void updateGrenades(double deltaTime) {
        for (Soldier s : soldiers) {
            if (s instanceof Soldiers.Grenade) {
                Soldiers.Grenade g = (Soldiers.Grenade) s;
                if (g.hasExploded() && !g.isDamageDealt()) {
                    System.out.println("BOOM! Grenade exploded at Lane: " + g.getLane());
                    for (Zombie z : zombies) {
                        if (z.isAlive()) {
                            boolean isLaneHit = Math.abs(g.getLane() - z.getLane()) <= 1;
                            double dist = Math.abs(g.getImageView().getTranslateX() - z.getImageView().getTranslateX());
                            boolean isXHit = dist < 160; 

                            if (isLaneHit && isXHit) {
                                z.takeDamage(10000); 
                            }
                        }
                    }
                    g.setDamageDealt(true);
                }
            }
        }
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
                this.player.addExperience(zombie.getExpvalue());
            }
        }
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
                        hit = true; break; 
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

    private void removeDeadSoldiers() {
        Iterator<Soldier> it = soldiers.iterator();
        while(it.hasNext()) {
            Soldier s = it.next();
            if(!s.isAlive()) {
                gamePane.getChildren().remove(s.getImageView());
                
                // Remove HP Label
                if (healthLabels.containsKey(s)) {
                    Label lbl = healthLabels.remove(s);
                    gamePane.getChildren().remove(lbl);
                }
                // Handle MC removal (if you want game over logic, add here)
                if (s instanceof MainCharacter) {
                     gamePane.getChildren().remove(((MainCharacter) s).getHealthLabel());
                }
                
                int[] pos = s.getPosition();
                gameMap.setSlot(pos[0], pos[1], GameMap.SLOT_EMPTY); 
                it.remove();
            }
        }
    }
    
    private void updateZom(int targetCol, int targetLane) {
        if (!mainCharacter.isAlive()) return;
        int[] currentPos = mainCharacter.getPosition();
        if (currentPos[0] == targetCol && currentPos[1] == targetLane) return;
        if (gameMap.getSlot(targetCol, targetLane) != GameMap.SLOT_EMPTY) {
             System.out.println("Slot Occupied! Cannot move there.");
             return;
        }
        gameMap.setSlot(currentPos[0], currentPos[1], GameMap.SLOT_EMPTY);
        mainCharacter.moveTo(targetCol, targetLane);
        gameMap.setSlot(targetCol, targetLane, GameMap.SLOT_SOLDIER);
    }

    private ImageView createCard(String imageName, String itemName) {
        ImageView cardView = new ImageView();
        try { cardView.setImage(new Image(getClass().getResourceAsStream(imageName))); } catch (Exception e) {}
        cardView.setFitWidth(96); cardView.setFitHeight(96); cardView.setPreserveRatio(true); cardView.setCursor(javafx.scene.Cursor.HAND); 
        
        cardView.setOnMouseClicked(e -> {
            // WEAPON CLICK: Equip, Update Sprite, Enable Movement
            if (itemName.equals(InventoryItem.MALLET) || 
                itemName.equals(InventoryItem.KATANA) || 
                itemName.equals(InventoryItem.MACHINE_GUN) ||
                itemName.equals(InventoryItem.HAND)) {
                
                player.setEquippedWeapon(itemName);
                mainCharacter.setWeaponSprite(itemName); // Change Sprite!
                
                DropShadow equipGlow = new DropShadow();
                equipGlow.setColor(Color.LIMEGREEN); equipGlow.setWidth(40); equipGlow.setHeight(40);
                cardView.setEffect(equipGlow);
                new java.util.Timer().schedule(new java.util.TimerTask() {
                    @Override public void run() { cardView.setEffect(null); }
                }, 500);
                
                // Reset Selection to enable movement
                this.selectedSoldierType = null;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = null;
                return;
            }

            // UNIT/PLANTING LOGIC
            if (this.selectedSoldierType != null && this.selectedSoldierType.equals(itemName)) {
                this.selectedSoldierType = null;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = null;
            } else {
                this.selectedSoldierType = itemName;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = cardView;
                DropShadow glow = new DropShadow();
                glow.setColor(Color.GOLD); glow.setWidth(20); glow.setHeight(20);
                cardView.setEffect(glow);
            }
        });
        return cardView;
    }

    private void addSoldier(String soldierType, int col, int lane) {
        // GRENADE LOGIC
        if (soldierType.equals(Item.BOMB) || soldierType.equals("Grenade")) {
            if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY) {
                System.out.println("Slot Occupied! Cannot throw grenade here."); return;
            }
            InventoryItem grenadeItem = findItem("Grenade");
            if (grenadeItem != null && grenadeItem.getQuantity() > 0) {
                Soldiers.Grenade grenade = new Soldiers.Grenade(col, lane);
                soldiers.add(grenade);
                gamePane.getChildren().add(grenade.getImageView());
                gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);
                grenadeItem.addQuantity(-1);
                
                // Reset Selection
                this.selectedSoldierType = null;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = null;
            } 
            return; 
        }
     // interaction with zom
        if (mainCharacter != null && mainCharacter.getCol() == col && mainCharacter.getLane() == lane) {
             // check if we are holding a healing item
            System.out.println("Used " + soldierType + " on Zom.");

             if (soldierType.equals("Medkit") || soldierType.equals("Bandage")) {
                 useHealingItem(soldierType);
             } else {
                 System.out.println("Cannot plant on top of Zom!");
             }
             return; 
        }
        // ----------------------------------

        if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY) {
             System.out.println("Slot Occupied!");
             return;
        }
        // --- BARRIER LOGIC ---

        
        // BARRIER LOGIC
        if (soldierType.equals(Soldier.BARRIER)) {
            if (lane + 2 >= GameMap.MAP_HEIGHT_TILES) { System.out.println("No vertical space!"); return; }
            if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY ||
                gameMap.getSlot(col, lane + 1) != GameMap.SLOT_EMPTY ||
                gameMap.getSlot(col, lane + 2) != GameMap.SLOT_EMPTY) { System.out.println("Slots occupied!"); return; }

            InventoryItem barrierItem = findItem("Barrier");
            if (barrierItem != null && barrierItem.getQuantity() > 0) {
                Soldier mainBarrier = new Soldiers.Barrier(col, lane, 0); 
                soldiers.add(mainBarrier);
                gamePane.getChildren().add(mainBarrier.getImageView());
                gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);
                createHealthLabel(mainBarrier); 

                Soldier dummy1 = new Soldiers.Barrier(col, lane + 1, 0); dummy1.getImageView().setVisible(false);
                soldiers.add(dummy1); gameMap.setSlot(col, lane + 1, GameMap.SLOT_SOLDIER);

                Soldier dummy2 = new Soldiers.Barrier(col, lane + 2, 0); dummy2.getImageView().setVisible(false);
                soldiers.add(dummy2); gameMap.setSlot(col, lane + 2, GameMap.SLOT_SOLDIER);

                barrierItem.addQuantity(-1); 
                
                // Reset Selection
                this.selectedSoldierType = null;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = null;
            } 
            return; 
        }

        if (mainCharacter != null && mainCharacter.getCol() == col && mainCharacter.getLane() == lane) return;
        if (gameMap.getSlot(col, lane) != GameMap.SLOT_EMPTY) return;
        
	    Soldier newSoldier = null;
	    switch (soldierType) {
	        case Soldier.ARCHER: newSoldier = new Archer(col, lane, 50); break;
	        case Soldier.SPEARMAN: newSoldier = new Spearman(col, lane, 70); break;
	    }
	
	    if (newSoldier != null) {
	        int cost = newSoldier.getSoldierCost();
	        if (player.getBurger() >= cost) {
                
                // SCALE SOLDIER BEFORE ADDING
                newSoldier.applyLevelScaling(player.getLevel());

	            soldiers.add(newSoldier);
	            soldierAttackTimers.put(newSoldier, 0.0);
	            gamePane.getChildren().add(newSoldier.getImageView());
	            player.deductBurger(cost);
	            gameMap.setSlot(col, lane, GameMap.SLOT_SOLDIER);
	            createHealthLabel(newSoldier); 
	            
	            // Reset Selection
	            this.selectedSoldierType = null;
                if (selectedCardView != null) selectedCardView.setEffect(null);
                selectedCardView = null;
	        } else { System.out.println("Not enough burgers!"); }
	    }
    }
    
    private void useHealingItem(String itemType) {
        // find item
        InventoryItem item = findItem(itemType);
        
        if (item != null && item.getQuantity() > 0) {
            // heal amount based on item type
            int healAmount = 0;
            if (itemType.equals("Medkit")) healAmount = 100;
            else if (itemType.equals("Bandage")) healAmount = 50;
            
            // Just call heal, the MainCharacter logic handles max HP clamping
            mainCharacter.heal(healAmount);
            item.addQuantity(-1);
            System.out.println("Used " + itemType + ". Quantity remaining: " + item.getQuantity());
        } else {
            System.out.println("You don't have any " + itemType + "s!");
        }
    }
    private InventoryItem findItem(String itemName) {
	    for (InventoryItem item :  mainApp.getCurrentPlayer().getInventory()) {
	        if (item.getName().equalsIgnoreCase(itemName)) {
	            return item;
	        }
	    }
	    return null;
	}
}