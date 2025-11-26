package Soldiers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile {

    // load the image
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
        
        // initialize image only if its not loaded yet
        if (projectileImage == null) {
            try {
                projectileImage = new Image(getClass().getResourceAsStream(IMAGE_PATH));
            } catch (Exception e) {
                System.err.println("Error loading projectile image: " + IMAGE_PATH);
            }
        }

        this.imageView = new ImageView(projectileImage);
       
//        this.imageView.setFitWidth(60); 
//        this.imageView.setFitHeight(20);
        
        // set the initial visual position!
        this.imageView.setTranslateX(this.x);
        this.imageView.setTranslateY(this.y);
    }

    public void update(double deltaTime) {
        x += speed * deltaTime;
        
        //update the visual x position every frame
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