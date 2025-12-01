package Items;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Item{
	//constants
	public static final String BARRIER = "Barrier";
	public static final String BOMB = "Bomb";
	public static final String POTION = "Health Potion";
	public static final String COIN = "Coin";
	
	
	
	//game attributes
    protected int health;
    protected String type; 
    protected boolean isDestroyed; 
    
    //positions
//    private int lane;
//    private int col;
    
    //for rendering
    protected String imagePath;
    protected ImageView imageView;

    public Item(int health, String type, String imageFileName) {
//        this.lane = lane;
//        this.isAlive = true;
        this.imagePath = imageFileName;
//        this.col = col;
        this.health = health;
        this.type = type;

//		 int lane = 0; //default lane
//		 int col = 0; //default col
//		 int width = 96; //default width
//		 int height = 96; //default height

        // load the GIF
        try {
            Image soldierImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(soldierImage);
            
            // set x position
//            this.imageView.setTranslateX((this.col * 96 ) - 360);
//            this.imageView.setTranslateY(this.lane * 96 - 192); 
            
            // set image size
//            this.imageView.setFitWidth(width); 
//            this.imageView.setFitHeight(height); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load zombie image: " + imagePath);
            e.printStackTrace();
            this.imageView = new ImageView(); 
        }
    }

    

    public void update(double deltaTime) {
        if (!isDestroyed) return;
        
        // add other update logic here like attack(), checkCollision() etc.
    }



    public void takeDamage(int amount) { 
        this.health -= amount;
        if (this.health <= 0) {
            this.isDestroyed = false;
            this.imageView.setVisible(false);
            // add a death animation or remove from parent here later
        }
    }

    
    

    public boolean isAlive() { return this.isDestroyed; }

    //setters
    public void setHealth(int health) { this.health = health; }
//    public void setDamage(int damage) { this.damage = damage; }
    protected void setType(String type) {this.type = type;} 
//    public void setLane(int currentLane) { this.lane = currentLane; }
    
    //getters
    public ImageView getImageView() {return this.imageView;}
    public int getHealth() { return health; }
    public String getType() { return type; }
//    public int getLane() { return this.lane; }
//    public int getDamage() { return damage; }
    public String getImagePath() {  return imagePath; }
//    public int[] getPosition() {
//    	int[] coordinates = {this.col, this.lane};
//    	return coordinates;
//    	}

}