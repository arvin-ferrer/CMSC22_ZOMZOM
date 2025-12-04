package Zombies;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Zombie {
	
	protected String type; 
	public static final String NORMAL = "NORMAL";
	public static final String NURSE = "NURSE";
	public static final String TANK = "TANK";
	
    protected int health;
    protected int speed; 
    protected int damage;
    protected boolean isAlive;
    protected int rewardPoints;
    protected int burgerPoints;
    protected double size; 
    protected String specialAbility; 
    protected int attackRange; 
    protected double spawnRate; 
    protected int expValue;
    
    protected String imagePath; 
    protected ImageView imageView;
    protected int currentLane;
    protected double positionX; 
    protected double positionY;

    public Zombie(double startX, double startY, int lane, String imageFileName) {
        this.positionX = startX;
        this.positionY = startY; 
        this.currentLane = lane;
        this.isAlive = true;
        this.imagePath = imageFileName;

        try {
            Image zombieImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(zombieImage);
            this.imageView.setTranslateX(this.positionX);
            this.imageView.setTranslateY(this.positionY);
            
            this.imageView.setFitWidth(112); 
            this.imageView.setFitHeight(112);
            this.size = 1.0; 
            this.imageView.setTranslateY(this.currentLane * 96 - 192); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            this.imageView = new ImageView(); 
        }
    }

    // --- NEW: DIFFICULTY LOGIC ---
    public void applyDifficulty(int level, boolean isOddLevel) {
        // 1. Scale EXP based on level (always rewarding)
        this.expValue += (level * 5); 
        this.rewardPoints += level;

        // 2. Odd Level Logic: More Health
        if (isOddLevel) {
            double multiplier = 1.0 + (level * 0.15); // 15% increase per level
            this.health = (int) (this.health * multiplier);
            System.out.println("Odd Level Buff Applied: Zombie HP is now " + this.health);
        } else {
            // Even levels get normal scaling or small scaling
            double multiplier = 1.0 + (level * 0.05);
            this.health = (int) (this.health * multiplier);
        }
    }

    public void update(double deltaTime) {
        if (!isAlive) return;
        move(deltaTime);
    }

    protected void move(double deltaTime) {
        this.positionX -= this.speed * deltaTime;
        this.imageView.setTranslateX(this.positionX);
    }

    public void takeDamage(int amount) { 
        this.health -= amount;
        // System.out.println(this.health); // Optional Debug
        if (this.health <= 0) {
            this.isAlive = false;
            this.imageView.setVisible(false);
        }
    }
    
    // GETTERS & SETTERS (Kept same as provided)
    public boolean isAlive() {return this.isAlive;}
    public String getType() { return type; }
    public int getHealth() { return this.health;  }
    public int getSpeed() { return speed; }
    public int getDamage() { return damage; }
    public int getLane() { return currentLane; }
    public int getRewardPoints() { return rewardPoints; }
    public double getSize() { return size; }
    public String getSpecialAbility() { return specialAbility; }
    public int getAttackRange() { return attackRange; }
    public double getSpawnRate() { return spawnRate; }
    public int getExpvalue() {return this.expValue;}
    public ImageView getImageView() {return this.imageView;}
    public String getImagePath() {return imagePath; }
    public double getPositionX() { return positionX; }
    public double getPositionY() { return positionY; }
    public int getBurgerPoints() { return this.burgerPoints;}
    
    protected void setType(String type) { this.type = type; } 
    public void setHealth(int health) { this.health = health; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }
    public void setSpecialAbility(String specialAbility) { this.specialAbility = specialAbility; }
    public void setSpawnRate(double spawnRate) { this.spawnRate = spawnRate; }
    public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }
    public void setPositionY(double positionY) { this.imageView.setTranslateY(positionY); }
    public void setLane(int currentLane) { this.currentLane = currentLane; }
    public void setPositionX(double positionX) { 
        this.positionX = positionX;
        this.imageView.setTranslateX(this.positionX); 
    }
    public void setBurgerPoints(int burger) { this.burgerPoints = burger; }
    public void setExpValue(int exp) { this.expValue = exp;}
}