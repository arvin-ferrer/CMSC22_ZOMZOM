package Soldiers;


import application.InventoryItem;
import javafx.scene.image.Image;

public class MainCharacter extends Soldier {
    
    // REMOVED private col/row variables to prevent shadowing logic errors
    // REMOVED health/damage/imageView variables because Soldier already has them

    public MainCharacter(int startCol, int startRow) {
        // Initialize Parent
        super(startCol, startRow, 140, 140, "/assets/Zom-base.gif");

        this.health = 200;
        this.damage = 20;
        this.setSoldierCost(0); 
    }
    
    public void moveTo(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol < 10 && targetRow >= 0 && targetRow < 6) {
            // Use PARENT setters. This updates the logic AND the visual position
            this.setCol(targetCol);
            this.setLane(targetRow);
        }
    }
    // NEW: Method to change sprite based on weapon
    public void setWeaponSprite(String weaponName) {
        String imagePath = "/assets/Zom-base.gif"; // Default / Hand

        switch (weaponName) {
            case InventoryItem.KATANA:
                imagePath = "/assets/zom-katana.gif"; 
               
                break;
            case InventoryItem.MALLET:
                imagePath = "/assets/zom-mallet.gif";
                break;
            case InventoryItem.MACHINE_GUN:
                imagePath = "/assets/Zom-gun.gif";
                break;
            case InventoryItem.HAND:
            default:
                imagePath = "/assets/Zom-base.gif";
                break;
        }

        try {
            this.imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Could not load weapon sprite: " + imagePath);
        }
    }

}