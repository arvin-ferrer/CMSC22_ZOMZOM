package application;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Zombie {

    protected int health;
    protected int speed; 
    protected int damage;
    protected String type; // "Normal", "Speed", "Flying", "Tank"
    protected double positionX; 
    protected double positionY; 
    protected boolean isAlive;
    protected int rewardPoints;
    protected double size; 
    protected String specialAbility; 
    protected String imagePath; 
    protected int attackRange; 
    protected double spawnRate; 
    protected ImageView imageView;

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
            
            this.imageView.setFitWidth(112); 
            this.imageView.setFitHeight(112);
            this.size = 1.0; 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            e.printStackTrace();
            this.imageView = new ImageView(); 
        }
    }

    protected int currentLane;

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

    
    public ImageView getImageView() {
        return this.imageView;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public int getHealth() { 
    	return health; 
    }
    public void setHealth(int health) { 
    	
    	this.health = health; 
    }

    public int getSpeed() { 
    	return speed; 
    }
    public void setSpeed(int speed) { 
    	
    	this.speed = speed; 
 
    }

    public int getDamage() { 
    	
    	return damage; 
    }
    public void setDamage(int damage) { 
    	
    	this.damage = damage; 
    }

    public String getType() { 
    	return type; 
    }
    protected void setType(String type) { 
    	this.type = type; 
    
    } 

    public double getPositionX() { 
    	return positionX; 
    }
    // setter for positionX if needed (pwede mag add ng push back effect)
    public void setPositionX(double positionX) { 
        this.positionX = positionX;
        this.imageView.setTranslateX(this.positionX); 
    }

    public double getPositionY() { return positionY; }
    // setter for positionY 
    public void setPositionY(double positionY) { 
        this.positionY = positionY;
        this.imageView.setTranslateY(this.positionY); 
    }

    public int getLane() { return currentLane; }
    public void setLane(int currentLane) { this.currentLane = currentLane; }

    
    public int getRewardPoints() { 
    	return rewardPoints; 
    }
    public void setRewardPoints(int rewardPoints) { 
    	this.rewardPoints = rewardPoints; 
    
    }

    public double getSize() { return size; }
    public void setSize(double size) { 
        this.size = size;
        this.imageView.setFitWidth(112 * size); 
        this.imageView.setFitHeight(112 * size);
    }

    public String getSpecialAbility() { return specialAbility; }
    public void setSpecialAbility(String specialAbility) { this.specialAbility = specialAbility; }

    public String getImagePath() {     	
    	return imagePath; 
    }


    public int getAttackRange() { 
    	return attackRange; 
    	
    }
    public void setAttackRange(int attackRange) { 
    	this.attackRange = attackRange; 
    }

    public double getSpawnRate() { 
    	return spawnRate; 
    }
    public void setSpawnRate(double spawnRate) { 
    	this.spawnRate = spawnRate; 
    }
}