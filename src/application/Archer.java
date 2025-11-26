package application;

public class Archer extends Soldier{
	
	private static final String IMAGE_FILE = "archer-sprite.gif";
	private static final String TYPE = Soldier.ARCHER;
	private static final int HEALTH = 100 ;
	private static final int DAMAGE = 50 ;

	
	public Archer(int col, int lane) {
		super(col, lane, IMAGE_FILE);
		setDamage(DAMAGE);
		setHealth(HEALTH);
		setType(TYPE);
	}
}
