package application;

public class NormalZombie extends Zombie {

    private static final String IMAGE_FILE = "normalZombie.gif";
    private static final int ZOMBIE_WIDTH = 112;
    private static final int ZOMBIE_HEIGHT = 112; 

    public NormalZombie(double startX, int lane) {
        super(startX, 0, lane, IMAGE_FILE); 
        
        setType("Normal");
        setHealth(100);
        setSpeed(40); // move 40 px per sec
        setDamage(10);
        setRewardPoints(10);
        setSize(1.0); 
        setSpecialAbility("None");
        setAttackRange(5); // can attack if within 5 pixels
        setSpawnRate(0.5); // spawns every 0.5 seconds
        
        double laneTopY = lane * Map.TILE_HEIGHT; 
        
        double centeredY = laneTopY + (Map.TILE_HEIGHT / 2.0) - (ZOMBIE_HEIGHT / 2.0);
        
        setPositionY(centeredY); 
    }
    

}