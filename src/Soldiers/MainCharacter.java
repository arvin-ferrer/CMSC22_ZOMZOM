package Soldiers;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class MainCharacter extends Soldier {
    
    private Label healthLabel;
    private int maxHealth = 200;

    public MainCharacter(int startCol, int startRow) {
        super(startCol, startRow, 140, 140, "/assets/Zom-base.gif");

        this.health = maxHealth;
        this.damage = 20;
        this.setSoldierCost(0); 
        
        this.healthLabel = new Label(health + "/" + maxHealth);
        this.healthLabel.setTextFill(Color.LIMEGREEN);
        this.healthLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 14px; -fx-effect: dropshadow(gaussian, black, 2, 1, 0, 0);");
        
        this.healthLabel.translateXProperty().bind(this.getImageView().translateXProperty().add(50));
        this.healthLabel.translateYProperty().bind(this.getImageView().translateYProperty().subtract(20));
    }
    
    public Label getHealthLabel() {
        return healthLabel;
    }
    
    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount); 
        
        this.healthLabel.setText(Math.max(0, this.health) + "/" + maxHealth);
        
        if (this.health < (maxHealth * 0.3)) {
            this.healthLabel.setTextFill(Color.RED);
        }
    }
 
    public void heal(int amount) {
        this.health += amount;
        
        // Cap health at max (200)
        if (this.health > maxHealth) {
            this.health = maxHealth;
        }
        
        // Update the label
        this.getHealthLabel().setText(this.health + "/" + maxHealth);
        
        // Restore green color if we healed up from low health
        if (this.health > (maxHealth * 0.3)) {
            this.getHealthLabel().setTextFill(Color.LIMEGREEN);
        }
        
        System.out.println("Zom healed! Current Health: " + this.health);
    }
    public void moveTo(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol < 10 && targetRow >= 0 && targetRow < 6) {
            
            this.setCol(targetCol);
            this.setLane(targetRow);
        }
    }
}