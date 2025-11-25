package application;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Soldier{

    protected int health;
    protected int damage;
    protected String type; 
    protected boolean isAlive; 
    protected String imagePath; 
    protected ImageView imageView;
    private int lane;
    private int col;

    public Soldier(int lane, int col, String imageFileName) {
        this.lane = lane;
        this.isAlive = true;
        this.imagePath = imageFileName;

        // load the GIF
        try {
            Image zombieImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(zombieImage);
            
            // set initial position
            this.imageView.setTranslateX(this.col * 96 - 400);
            
            this.imageView.setFitWidth(112); 
            this.imageView.setFitHeight(112); 
            this.imageView.setTranslateY(this.currentLane * 96 - 192); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            e.printStackTrace();
            this.imageView = new ImageView(); 
        }
    }

    protected int currentLane;

    public void update(double deltaTime) {
        if (!isAlive) return;
        
        // add other update logic here like attack(), checkCollision() etc.
    }



    public void takeDamage(int amount) { 
        this.health -= amount;
        if (this.health <= 0) {
            this.isAlive = false;
            this.imageView.setVisible(false);
            // add a death animation or remove from parent here later
        }
    }

    
    

    public boolean isAlive() { return this.isAlive; }

    //setters
    public void setHealth(int health) { this.health = health; }
    public void setDamage(int damage) { this.damage = damage; }
    protected void setType(String type) {this.type = type;} 
    public void setLane(int currentLane) { this.currentLane = currentLane; }
    
    //getters
    public ImageView getImageView() {return this.imageView;}
    public int getHealth() { return health; }
    public String getType() { return type; }
    public int getLane() { return currentLane; }
    public int getDamage() { return damage; }
    public String getImagePath() {  return imagePath; }
    public int[] getCoordinates() {
    	int[] coordinates = {this.col, this.lane};
    	return coordinates;
    	}

}