package Soldiers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile {

    // 1. Load the image ONLY ONCE (static) to prevent lag
    private static final String IMAGE_PATH = "/assets/arrow-sprite.png";
    private static Image projectileImage; 

    private double x;
    private double y;
    private double speed = 400; // pixels per second
    private int lane;
    private int damage;
    private boolean isActive;
    protected ImageView imageView;

    public Projectile(double startX, double startY, int lane, int damage) {
        this.x = startX;
        this.y = startY;
        this.lane = lane;
        this.damage = damage;
        this.isActive = true;
        
        // Initialize image only if it hasn't been loaded yet
        if (projectileImage == null) {
            try {
                projectileImage = new Image(getClass().getResourceAsStream(IMAGE_PATH));
            } catch (Exception e) {
                System.err.println("Error loading projectile image: " + IMAGE_PATH);
            }
        }

        this.imageView = new ImageView(projectileImage);
        
        // Optional: Resize if the sprite is too big
//        this.imageView.setFitWidth(60); 
//        this.imageView.setFitHeight(20);
        
        // 2. CRITICAL FIX: You must set the initial visual position!
        this.imageView.setTranslateX(this.x);
        this.imageView.setTranslateY(this.y);
    }

    public void update(double deltaTime) {
        x += speed * deltaTime;
        
        // Update the visual X position every frame
        this.imageView.setTranslateX(x);
    }

    public ImageView getView() { return this.imageView; }
    public double getX() { return x; }
    public int getLane() { return lane; }
    public int getDamage() { return damage; }
    
    public void setInactive() {
        this.isActive = false;
        this.imageView.setVisible(false);
    }
}