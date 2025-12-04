package Soldiers;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Projectile {

    private double x;
    private double y;
    private double speed = 400; // Default speed
    private int lane;
    private int damage;
    private boolean isActive;
    
    // Changed to Node to support both ImageView and Shape
    protected Node view; 

    /**
     * @param imagePathOrType Pass a file path for images, or "BULLET" for a circle shape.
     */
    public Projectile(double startX, double startY, int lane, int damage, String imagePathOrType) {
        this.x = startX;
        this.y = startY;
        this.lane = lane;
        this.damage = damage;
        this.isActive = true;
        
        // Check if we want a geometric bullet or an image
        if (imagePathOrType.equals("BULLET")) {
            // Create a small Gold Circle
            Circle bullet = new Circle(5); 
            bullet.setFill(Color.GOLD);
            bullet.setStroke(Color.BLACK);
            bullet.setStrokeWidth(1);
            
            this.view = bullet;
            this.speed = 900; // Bullets fly faster than arrows
        } else {
            // Load Image (e.g., Arrow)
            try {
                ImageView imgView = new ImageView(new Image(getClass().getResourceAsStream(imagePathOrType)));
                imgView.setFitWidth(60); 
                imgView.setFitHeight(20);
                this.view = imgView;
                this.speed = 400; // Arrows are slower
            } catch (Exception e) {
                System.err.println("Error loading projectile image: " + imagePathOrType);
                this.view = new Circle(5, Color.RED); // Error fallback
            }
        }

        // Set initial position
        this.view.setTranslateX(this.x);
        this.view.setTranslateY(this.y);
    }

    public void update(double deltaTime) {
        x += speed * deltaTime;
        this.view.setTranslateX(x);
    }

    public Node getView() { return this.view; }
    public double getX() { return x; }
    public int getLane() { return lane; }
    public int getDamage() { return damage; }
    
    public void setInactive() {
        this.isActive = false;
        this.view.setVisible(false);
    }
}