package Soldiers;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Soldier{
	//constants
	public static final String ARCHER =	"Archer";
	public static final String SPEARMAN = "Spearman";
//	public static final String SWORDSMAN = "Swordsman";
	
	
	//game attributes
    protected int health;
    protected int damage;
    protected String type; 
    protected boolean isAlive; 
    protected int SoldierCost;
    
    //positions
    private int lane;
    private int col;
    
    //for rendering
    protected String imagePath;
    protected ImageView imageView;

    public Soldier(int col, int lane, int width, int  height, String imageFileName) {
        this.lane = lane;
        this.isAlive = true;
        this.imagePath = imageFileName;
        this.col = col;

        // load the GIF
        try {
            Image soldierImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(soldierImage);
            
            // set x position
            this.imageView.setTranslateX((this.col * 96 ) - 360);
            this.imageView.setTranslateY(this.lane * 96 - 192); 
            
            // set image size
            this.imageView.setFitWidth(width); 
            this.imageView.setFitHeight(height); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            e.printStackTrace();
            this.imageView = new ImageView(); 
        }
    }

    

    public void update(double deltaTime) {
        if (!isAlive) return;
        
        // add other update logic here like attack(), checkCollision() etc.
    }
    public int getSoldierCost() {
		return this.SoldierCost;
	}

    public void setSoldierCost(int cost) {
    	this.SoldierCost = cost;
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
    public void setLane(int currentLane) { this.lane = currentLane; }
    
    //getters
    public ImageView getImageView() {return this.imageView;}
    public int getHealth() { return health; }
    public String getType() { return type; }
    public int getLane() { return this.lane; }
    public int getDamage() { return damage; }
    public String getImagePath() {  return imagePath; }
    public int[] getPosition() {
    	int[] coordinates = {this.col, this.lane};
    	return coordinates;
    	}

}