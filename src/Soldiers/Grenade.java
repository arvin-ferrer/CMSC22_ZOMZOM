package Soldiers;

import javafx.scene.image.Image;

public class Grenade extends Soldier {

    // 1. Set to 0.1 for instant explosion (almost 0, but allows 1 frame for setup)
    private double fuseTimer = 0.1; 
    private double explosionDuration = 0.5; // Keeps explosion on screen for 0.5s
    private boolean exploded = false;
    private boolean damageDealt = false; 

    public Grenade(int col, int lane) {
        super(col, lane, 96, 96, "/assets/grenade-sprite.png");
        
        this.health = 999999; 
        // 2. Set massive damage to ensure zombies are "gone"
        this.damage = 10000; 
        this.type = "Grenade";
        this.SoldierCost = 0; 
        
        // Center the small grenade visually in the tile
        this.imageView.setTranslateX(this.imageView.getTranslateX() + 16);
        this.imageView.setTranslateY(this.imageView.getTranslateY() + 16);
    }

    @Override
    public void update(double deltaTime) {
        if (!exploded) {
            fuseTimer -= deltaTime;
            if (fuseTimer <= 0) {
                explode();
            }
        } else {
            // Wait for explosion animation to finish before removing from map
            explosionDuration -= deltaTime;
            if (explosionDuration <= 0) {
                this.isAlive = false; 
            }
        }
    }

    private void explode() {
        this.exploded = true;
        
        try {
             Image boomImg = new Image(getClass().getResourceAsStream("/assets/explosion-sprite.gif"));
             this.imageView.setImage(boomImg);
             
             // 3. Make the image cover the 3x3 grid (96px * 3 = 288px)
             double explosionSize = 290; 
             this.imageView.setFitWidth(explosionSize);
             this.imageView.setFitHeight(explosionSize);
             
             // --- VISUAL MATH FIX ---
             // We need to shift the image LEFT and UP to center it on the tile.
             // Currently, it is shifted +16 (from constructor).
             // To center a 290px image on a 96px tile, we need to shift roughly -97px.
             // Combined calculation: We subtract roughly 112px from current position.
             
             this.imageView.setTranslateX(this.imageView.getTranslateX());
             this.imageView.setTranslateY(this.imageView.getTranslateY() );
             
             this.imageView.toFront(); // Ensure explosion is on top of zombies
             
        } catch (Exception e) {
            System.err.println("Explosion image not found! Check /assets/explosion-sprite.gif");
        }
    }

    public boolean hasExploded() { return exploded; }
    public boolean isDamageDealt() { return damageDealt; }
    public void setDamageDealt(boolean damageDealt) { this.damageDealt = damageDealt; }
}