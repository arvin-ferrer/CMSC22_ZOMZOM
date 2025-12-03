package Items;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public abstract class Item {
    // Constants
    public static final String BARRIER = "Barrier";
    public static final String BOMB = "Bomb";
    public static final String POTION = "Health Potion";
    public static final String COIN = "Coin";
    public static final String BANDAGE = "Bandage";
    public static final String FENCE = "FENCE";

    
    // Game attributes
    protected int health;
    protected String type; 
    protected boolean isDestroyed; 
    
    // Positions (Crucial for collision detection)
    protected int lane;
    protected int col;
    
    // For rendering
    protected String imagePath;
    protected ImageView imageView;

    
    public Item(int col, int lane, int health, String type, String imageFileName) {
        this.col = col;
        this.lane = lane;
        this.health = health;
        this.type = type;
        this.imagePath = imageFileName;
        this.isDestroyed = false; // Initially, the item is NOT destroyed

     
        try {
         
            Image itemImage = new Image(getClass().getResourceAsStream(imagePath));
            this.imageView = new ImageView(itemImage);
            
            // Set rendering size (96x96 matches your map tiles)
            this.imageView.setFitWidth(96); 
            this.imageView.setFitHeight(96); 
            
         
            this.imageView.setTranslateX(this.col * 96 + 160); 
            this.imageView.setTranslateY(this.lane * 80 + 80); 
            
        } catch (Exception e) {
            System.err.println("ERROR: Could not load item image: " + imagePath);
            // e.printStackTrace(); // Optional: keep clean console
            this.imageView = new ImageView(); // Fallback to empty to prevent crash
        }
    }

    public void update(double deltaTime) {

        if (isDestroyed) return;
        
        
    }

    public void takeDamage(int amount) { 
        if (isDestroyed) return;

        this.health -= amount;
        
       
        if (this.health <= 0) {
            this.isDestroyed = true;
            this.imageView.setVisible(false);
            System.out.println(this.type + " was destroyed!");
        }
    }

   
    public boolean isAlive() { 
        return !this.isDestroyed; 
    }

    public void setHealth(int health) { this.health = health; }
    
    public ImageView getImageView() { return this.imageView; }
    public int getHealth() { return health; }
    public String getType() { return type; }
    public String getImagePath() { return imagePath; }
    
    // Position Getters are needed for Zombie collision logic
    public int getLane() { return this.lane; }
    public int getCol() { return this.col; }
    
    public int[] getPosition() {
        return new int[] {this.col, this.lane};
    }
}