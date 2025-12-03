package Zombies;

public class NormalZombie extends Zombie {

    private static final String IMAGE_FILE = "/assets/normalZombie.gif";
    private static final int ZOMBIE_WIDTH = 112;
    private static final int ZOMBIE_HEIGHT = 112; 

    public NormalZombie(double startX, int lane) {
        super(startX, 0, lane, IMAGE_FILE); 
        
        setType(Zombie.NORMAL);
        setHealth(100);
        setSpeed(40); // move 40 px per sec
        setDamage(10);
        setRewardPoints(10);
        setSpecialAbility("None");
        setAttackRange(5); // can attack if within 5 pixels
        setSpawnRate(0.5); // spawns every 0.5 seconds
        setBurgerPoints(5);
       setExpValue(10);
    }
    

}