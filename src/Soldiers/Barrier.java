package Soldiers;

public class Barrier extends Soldier {

    private Barrier mainPart; // Reference to the "Head" of the barrier

    // --- CONSTRUCTOR 1: The Main Barrier (Top Part) ---
    public Barrier(int col, int lane, int cost) {
        // Width 96, Height 288 (Spans 3 tiles)
        super(col, lane, 96, 288, "/assets/barrier-sprite.png");
        
        this.health = 2000; // Shared Health Pool
        this.damage = 0;    // Barriers don't attack
        this.type = Soldier.BARRIER;
        this.SoldierCost = cost;
        this.isAlive = true;
        this.mainPart = null; // I am the main part

        // Fix visual position: Shift down by 1 tile (96px) to align top-left correctly
        // because the image is very tall.
        this.imageView.setTranslateY((lane * 96) - 192 + 96);
    }

    // --- CONSTRUCTOR 2: The Dummy Barrier (Middle/Bottom Parts) ---
    public Barrier(int col, int lane, int cost, Barrier parentBarrier) {
        // Size doesn't matter here, it's invisible
        super(col, lane, 96, 96, "/assets/barrier-sprite.png");
        
        this.mainPart = parentBarrier; // Link to the Main Barrier
        this.damage = 0;
        this.type = Soldier.BARRIER;
        this.isAlive = true;
        
        // Dummies are invisible, they are just hitboxes for zombies
        this.imageView.setVisible(false);
    }
    
    @Override
    public void takeDamage(int amount) {
        // If I am a dummy, hurt my parent instead
        if (this.mainPart != null) {
            this.mainPart.takeDamage(amount);
        } else {
            // If I am the main barrier, take damage normally
            super.takeDamage(amount);
        }
    }

    @Override
    public boolean isAlive() {
        // If I am a dummy, I am only alive if my parent is alive
        if (this.mainPart != null) {
            return this.mainPart.isAlive();
        }
        return super.isAlive();
    }
    
    @Override
    public void update(double deltaTime) {
        // Barriers do not move
    }
}