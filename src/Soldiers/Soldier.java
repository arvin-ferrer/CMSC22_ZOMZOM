package Soldiers;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Soldier {
    // constants
    public static final String ARCHER = "Archer";
    public static final String SPEARMAN = "Spearman";
    public static final String BARRIER = "Barrier"; 
    public static final String MAIN_CHARACTER = "MainCharacter"; 
    
    // game attributes
    protected int health;
    protected int damage;
    protected String type; 
    protected boolean isAlive; 
    protected int SoldierCost;
    
    // NEW: Base stats to calculate scaling correctly
    protected int baseHealth;
    protected int baseDamage;
    
    // positions
    private int lane;
    private int col;
    
    // for rendering
    protected String imagePath;
    protected ImageView imageView;

    public Soldier(int col, int lane, int width, int height, String imageFileName) {
        this.lane = lane;
        this.col = col;
        this.isAlive = true;
        this.imagePath = imageFileName;

        // load the GIF
        try {
            Image soldierImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(soldierImage);
            
            updateVisualPosition(); 
            
            this.imageView.setFitWidth(width); 
            this.imageView.setFitHeight(height); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load image: " + imagePath);
            this.imageView = new ImageView(); 
        }
        
        // Initialize base stats equal to current stats
        // Subclasses set damage/health after super(), so we might update this in setters too.
        this.baseHealth = this.health;
        this.baseDamage = this.damage;
    }

    public void update(double deltaTime) {
        if (!isAlive) return;
    }
    
    // --- NEW: LEVEL SCALING LOGIC ---
    public void applyLevelScaling(int level) {
        // If level is 1, no change needed
        if (level <= 1) return;

        // Logic: +10% Health and +5% Damage per level
        double hpMultiplier = 1.0 + (level * 0.10); 
        double dmgMultiplier = 1.0 + (level * 0.05);
        
        // If base stats weren't set correctly (e.g. 0), grab current health
        if (this.baseHealth == 0) this.baseHealth = this.health;
        if (this.baseDamage == 0) this.baseDamage = this.damage;

        // Apply
        this.damage = (int) (this.baseDamage * dmgMultiplier);
        
        // Only scale health for non-MainCharacter soldiers here
        // (MainCharacter has special health bar logic)
        if (!this.type.equals(MAIN_CHARACTER)) {
            this.health = (int) (this.baseHealth * hpMultiplier);
        }
    }

    protected void updateVisualPosition() {
        this.imageView.setTranslateX((this.col * 96) - 360); 
        this.imageView.setTranslateY((this.lane * 96) - 192); 
    }

    public void takeDamage(int amount) { 
        this.health -= amount;
        if (this.health <= 0) {
            this.isAlive = false;
            this.imageView.setVisible(false);
        }
    }

    public boolean isAlive() { return this.isAlive; }

    // setters
    public void setHealth(int health) { 
        this.health = health;
        if (this.baseHealth == 0) this.baseHealth = health; // Capture base
    }
    
    public void setDamage(int damage) { 
        this.damage = damage; 
        if (this.baseDamage == 0) this.baseDamage = damage; // Capture base
    }
    
    protected void setType(String type) { this.type = type; } 
    public void setSoldierCost(int cost) { this.SoldierCost = cost; }
    
    public void setLane(int currentLane) { 
        this.lane = currentLane; 
        updateVisualPosition(); 
    }
    
    public void setCol(int currentCol) {
        this.col = currentCol;
        updateVisualPosition();
    }
    
    // getters
    public ImageView getImageView() { return this.imageView; }
    public int getHealth() { return health; }
    public String getType() { return type; }
    public int getDamage() { return damage; }
    public String getImagePath() { return imagePath; }
    public int getSoldierCost() { return this.SoldierCost; }
    
    public int getLane() { return this.lane; }
    public int getCol() { return this.col; } 

    public int[] getPosition() {
        int[] coordinates = {this.col, this.lane};
        return coordinates;
    }
}