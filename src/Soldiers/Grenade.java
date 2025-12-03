package Soldiers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Grenade extends Soldier {

    private double fuseTimer = 1.0; // 1 second until boom
    private double explosionDuration = 0.5; // How long the explosion GIF stays on screen
    private boolean exploded = false;
    private boolean damageDealt = false; // Ensures we only damage zombies once

    public Grenade(int col, int lane) {
        // col, lane, width, height, imagePath
        super(col, lane, 64, 64, "/assets/grenade-sprite.png"); // Make sure you have a grenade image
        
        this.health = 10; 
        this.damage = 500; 
        this.type = "Grenade";
        this.SoldierCost = 0; 
        
        // Center it in the tile slightly
        this.imageView.setTranslateX(this.imageView.getTranslateX() + 16);
        this.imageView.setTranslateY(this.imageView.getTranslateY() + 16);
    }

    @Override
    public void update(double deltaTime) {
        // Phase 1: Counting down the fuse
        if (!exploded) {
            fuseTimer -= deltaTime;
            if (fuseTimer <= 0) {
                explode();
            }
        } 
        // Phase 2: Exploded, showing animation
        else {
            explosionDuration -= deltaTime;
            if (explosionDuration <= 0) {
                // Animation done, remove from map
                this.isAlive = false; 
            }
        }
    }

    private void explode() {
        this.exploded = true;
        
        // Change image to explosion
        try {
             // Make sure this file exists in your src/assets folder!
             this.imageView.setImage(new Image(getClass().getResourceAsStream("/assets/explosion-sprite.gif")));
             
             // Adjust size/position for the larger explosion image
             this.imageView.setFitWidth(600);
             this.imageView.setFitHeight(600);
             this.imageView.setTranslateX(this.imageView.getTranslateX() - 40);
             this.imageView.setTranslateY(this.imageView.getTranslateY() - 40);
        } catch (Exception e) {
            System.err.println("Explosion image not found!");
        }
    }

    public boolean hasExploded() {
        return exploded;
    }

    public boolean isDamageDealt() {
        return damageDealt;
    }

    public void setDamageDealt(boolean damageDealt) {
        this.damageDealt = damageDealt;
    }
}