package application;

public class nurseZombie extends Zombie {

    private static final String IMAGE_FILE = "nurseZombie.gif";
    private static final int ZOMBIE_WIDTH = 112;
    private static final int ZOMBIE_HEIGHT = 112; 

    public nurseZombie(double startX, int lane) {
        super(startX, 0, lane, IMAGE_FILE); 
        
        setType("fast");
        setHealth(50);
        setSpeed(100); // move 40 px per sec
        setDamage(10);
        setRewardPoints(10);
        setSize(1.0); 
        setSpecialAbility("None"); // might add later
        setAttackRange(5); // can attack if within 5 pixels
        setSpawnRate(0.5); // spawns every 0.5 seconds
        
       
    }
    

}