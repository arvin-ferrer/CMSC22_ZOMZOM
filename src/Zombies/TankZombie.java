package Zombies;

public class TankZombie extends Zombie {

    private static final String IMAGE_FILE = "/assets/tankZombie.gif";
    private static final int ZOMBIE_WIDTH = 166;
    private static final int ZOMBIE_HEIGHT = 166; 

    public TankZombie(double startX, int lane) {
        super(startX, 0, lane, IMAGE_FILE); 
        
        setType(Zombie.TANK);
        setHealth(200);
        setSpeed(30); // move 40 px per sec
        setDamage(10);
        setRewardPoints(10);
        setSpecialAbility("None"); // might add later
        setAttackRange(5); // can attack if within 5 pixels
        setSpawnRate(0.5); // spawns every 0.5 seconds
        setBurgerPoints(20);
        setExpValue(50);

     
    }
    

}