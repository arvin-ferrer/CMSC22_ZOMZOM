package application;

public class Archer extends Soldier{
	
	private static final String IMAGE_FILE = "/assets/archer-sprite.gif";
	private static final String TYPE = Soldier.ARCHER;
	private static final int HEALTH = 100 ;
	private static final int DAMAGE = 10 ;
	private static final int WIDTH = 112 ;
	private static final int HEIGHT = 112 ;

	
	public Archer(int col, int lane) {
		super(col, lane, Archer.WIDTH, Archer.HEIGHT, IMAGE_FILE);
		setDamage(DAMAGE);
		setHealth(HEALTH);
		setType(TYPE);
	}
}
