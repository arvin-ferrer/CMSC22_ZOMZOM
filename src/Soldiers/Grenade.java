package Soldiers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Grenade extends Soldier {

    private double fuseTimer = 1.0; // 1 second until boom
    private boolean exploded = false;

    public Grenade(int col, int lane) {
        // col, lane, width, height, imagePath
        super(col, lane, 64, 64, "/assets/grenade-sprite.png");
        
        this.health = 10; // Low health (can be destroyed before exploding!)
        this.damage = 500; // Massive damage
        this.type = "Grenade";
        this.SoldierCost = 0; // Cost handled by item consumption
        
        // Center it in the tile
        this.imageView.setTranslateX(this.imageView.getTranslateX() + 16);
        this.imageView.setTranslateY(this.imageView.getTranslateY() + 16);
    }

    @Override
    public void update(double deltaTime) {
        if (exploded) return;

        fuseTimer -= deltaTime;
        if (fuseTimer <= 0) {
            explode();
        }
    }

    private void explode() {
        this.exploded = true;
        this.isAlive = false; 
        
        
        try {
             this.imageView.setImage(new Image(getClass().getResourceAsStream("/assets/explosion-sprite.gif")));
             // s.imageView.setFitWidth(150);
             this.imageView.setFitHeight(150);
             this.imageView.setTranslateX(this.imageView.getTranslateX() - 40);
             this.imageView.setTranslateY(this.imageView.getTranslateY() - 40);
        } catch (Exception e) {}
    }

    public boolean hasExploded() {
        return exploded;
    }
}