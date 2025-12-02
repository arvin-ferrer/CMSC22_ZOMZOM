package Soldiers;
import Soldiers.Soldier;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;

public class MainCharacter extends Soldier {
	
    private int col;
    private int row;
    private int health;
    private int damage;
//    private ImageView imageView;
    private static final int TILE_WIDTH = 96;
    private static final int TILE_HEIGHT = 96; 

    public MainCharacter(int startCol, int startRow) {
        super(startCol, startRow, 140, 140, "/assets/Zom-base.gif");

        this.health = 2000;
        this.damage = 20;
        this.setSoldierCost(0); 
        updateVisualPosition();
    }
    
    public void moveTo(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol < 10 && targetRow >= 0 && targetRow < 6) {
            this.col = targetCol;
            this.row = targetRow;
            updateVisualPosition();
        }
    }
    private void updateVisualPosition() {
        this.getImageView().setTranslateX((this.col * 96 ) - 360);
        this.getImageView().setTranslateY(this.row * 96 - 192); 

    }

}