package Zombies;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Zombie {
	
	protected String type; // "Normal", "Speed", "Flying", "Tank"
	public static final String NORMAL = "NORMAL";
	public static final String NURSE = "NURSE";
	public static final String TANK = "TANK";
	
	
	//gameplay attributes
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
    
    // position and rendering attributes
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

        // load the GIF
        try {
            Image zombieImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(zombieImage);
            
            // set initial position
            this.imageView.setTranslateX(this.positionX);
            this.imageView.setTranslateY(this.positionY);
            
            // rendering size adjustments
            this.imageView.setFitWidth(112); 
            this.imageView.setFitHeight(112);
            this.size = 1.0; 
            this.imageView.setTranslateY(this.currentLane * 96 - 192); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            e.printStackTrace();
            this.imageView = new ImageView(); 
        }
    }



    public void update(double deltaTime) {
        if (!isAlive) return;
        move(deltaTime);
        // add other update logic here like attack(), checkCollision() etc.
    }

    protected void move(double deltaTime) {
        this.positionX -= this.speed * deltaTime;
        this.imageView.setTranslateX(this.positionX);
    }

    public void takeDamage(int amount) { // Damage amount is int
        this.health -= amount;
        if (this.health <= 0) {
            this.isAlive = false;
            this.imageView.setVisible(false);
            // add a death animation or remove from parent here later
        }
    }
    
    // GETTERS ===============================================================================
    public boolean isAlive() {return this.isAlive;}
    
    public String getType() { return type; }
    
    // gameplay getters ---------------------------------------------------------------------
    public int getHealth() { return this.health;  }
   
    public int getSpeed() { return speed; }

    public int getDamage() { return damage; }
       
    public int getLane() { return currentLane; }

    public int getRewardPoints() { return rewardPoints; }
    
    public double getSize() { return size; }
    
    public String getSpecialAbility() { return specialAbility; }
    
    public int getAttackRange() { return attackRange; }
    
    public double getSpawnRate() { return spawnRate; }

    
    // position and rendering getters ----------------------------------------------------------------
    public ImageView getImageView() {return this.imageView;}
    
    public String getImagePath() {return imagePath; }
    
    public double getPositionX() { return positionX; }
    
    public double getPositionY() { return positionY; }
    public int getBurgerPoints() { return this.burgerPoints;}

    
    
    // SETTERS =======================================================================================
    protected void setType(String type) { this.type = type; } 
    
    // gameplay setters ------------------------------------------------------
    public void setHealth(int health) { this.health = health; }
    
    public void setDamage(int damage) { this.damage = damage; }
    
    public void setSpeed(int speed) { this.speed = speed; }
    
    public void setAttackRange(int attackRange) { this.attackRange = attackRange; }
    
    public void setSpecialAbility(String specialAbility) { this.specialAbility = specialAbility; }
    
    public void setSpawnRate(double spawnRate) { this.spawnRate = spawnRate; }
    
    public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }
    
    // position  and rendering setters ----------------------------------------------------------
    public void setPositionY(double positionY) { this.imageView.setTranslateY(positionY); }
    
    public void setLane(int currentLane) { this.currentLane = currentLane; }
    
    public void setPositionX(double positionX) { 
        this.positionX = positionX;
        this.imageView.setTranslateX(this.positionX); 
    }
    public void setBurgerPoints(int burger) {
    	this.burgerPoints = burger;
    }


}