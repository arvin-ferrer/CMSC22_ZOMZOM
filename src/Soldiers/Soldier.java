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
    
    // positions (Keep these private, use setters!)
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
            
            updateVisualPosition(); // Helper method to set X/Y
            
            // set image size
            this.imageView.setFitWidth(width); 
            this.imageView.setFitHeight(height); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load image: " + imagePath);
            this.imageView = new ImageView(); 
        }
    }

    public void update(double deltaTime) {
        if (!isAlive) return;
    }

    // --- NEW HELPER FOR POSITIONING ---
    protected void updateVisualPosition() {
        // (col * TILE_WIDTH) - Offset
        this.imageView.setTranslateX((this.col * 96) - 360); // 96 is tile width
        // (lane * TILE_HEIGHT) - Offset
        this.imageView.setTranslateY((this.lane * 96) - 192); // 96 is tile height
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
    public void setHealth(int health) { this.health = health; }
    public void setDamage(int damage) { this.damage = damage; }
    protected void setType(String type) { this.type = type; } 
    public void setSoldierCost(int cost) { this.SoldierCost = cost; }
    
    // --- FIXED POSITION SETTERS ---
    public void setLane(int currentLane) { 
        this.lane = currentLane; 
        updateVisualPosition(); 
    }
    
    // ADDED THIS METHOD
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
    public int getCol() { return this.col; } // Added Getter

    public int[] getPosition() {
        int[] coordinates = {this.col, this.lane};
        return coordinates;
    }
}