package Soldiers;

public class Barrier extends Soldier {

    public Barrier(int col, int lane, int cost) {
        // pass the specific dimensions: 96 width, 288 height
        super(col, lane, 96, 288, "/assets/barrier-sprite.png");
        
        this.health = 500; 
        this.damage = 0;   
        this.type = Soldier.BARRIER;
        this.SoldierCost = cost;
        this.isAlive = true;

        this.imageView.setTranslateY((lane * 96) - 192 + 96);
    }
    
    @Override
    public void update(double deltaTime) {
        // barriers do not move or attack
    }
}