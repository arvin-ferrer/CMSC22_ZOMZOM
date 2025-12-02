package Soldiers;

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
}