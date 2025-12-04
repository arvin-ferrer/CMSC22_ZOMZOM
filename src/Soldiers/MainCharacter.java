package Soldiers;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import application.InventoryItem;
import javafx.scene.image.Image;

public class MainCharacter extends Soldier {
    
    private Label healthLabel;
    private int maxHealth = 200;
    
    // NEW: Base Stats
    private int baseMaxHealth = 200;
    private int baseMCDamage = 20;

    public MainCharacter(int startCol, int startRow) {
        super(startCol, startRow, 140, 140, "/assets/Zom-base.gif");

        this.health = maxHealth;
        this.damage = 20;
        this.setSoldierCost(0); 
        this.setType(Soldier.MAIN_CHARACTER);
        
        // Set Bases
        this.baseMaxHealth = 200;
        this.baseMCDamage = 20;
        
        this.healthLabel = new Label(health + "/" + maxHealth);
        this.healthLabel.setTextFill(Color.LIMEGREEN);
        this.healthLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
        
        this.healthLabel.translateXProperty().bind(this.getImageView().translateXProperty().add(50));
        this.healthLabel.translateYProperty().bind(this.getImageView().translateYProperty().subtract(20));
    }
    
    // --- NEW: Override Scaling ---
    @Override
    public void applyLevelScaling(int level) {
        if (level <= 1) return;

        double multiplier = 1.0 + (level * 0.1); // 10% per level
        
        int oldMaxHealth = this.maxHealth;
        
        // Recalculate Max Stats
        this.maxHealth = (int) (this.baseMaxHealth * multiplier);
        this.damage = (int) (this.baseMCDamage * multiplier);
        
        // Heal the difference so the health bar doesn't look empty
        int healthDiff = this.maxHealth - oldMaxHealth;
        if (healthDiff > 0) {
            this.health += healthDiff;
        }

        updateLabel();
    }

    public Label getHealthLabel() {
        return healthLabel;
    }
    
    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount); 
        updateLabel();
    }
 
    public void heal(int amount) {
        this.health += amount;
        
        // Cap health at NEW max
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
        updateLabel();
        System.out.println("Zom healed! Current Health: " + this.health);
    }
    
    private void updateLabel() {
        this.healthLabel.setText(Math.max(0, this.health) + "/" + maxHealth);
        
        if (this.health < (maxHealth * 0.3)) {
            this.healthLabel.setTextFill(Color.RED);
        } else {
            this.healthLabel.setTextFill(Color.LIMEGREEN);
        }
    }

    public void moveTo(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol < 10 && targetRow >= 0 && targetRow < 6) {
            this.setCol(targetCol);
            this.setLane(targetRow);
        }
    }

    public void setWeaponSprite(String weaponName) {
        String imagePath = "/assets/Zom-base.gif"; 

        switch (weaponName) {
            case InventoryItem.KATANA: imagePath = "/assets/zom-katana.gif"; break;
            case InventoryItem.MALLET: imagePath = "/assets/zom-mallet.gif"; break;
            case InventoryItem.MACHINE_GUN: imagePath = "/assets/Zom-gun.gif"; break;
            case InventoryItem.HAND:
            default: imagePath = "/assets/Zom-base.gif"; break;
        }

        try {
            this.imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Could not load weapon sprite: " + imagePath);
        }
    }
    public void reset() {
        this.health = this.maxHealth; // Restore to full health
        this.isAlive = true;
        moveTo(0, 2); // Move back to starting position
        updateLabel();

    }

    public int getMaxHealth() {

        return this.maxHealth;
    }
}