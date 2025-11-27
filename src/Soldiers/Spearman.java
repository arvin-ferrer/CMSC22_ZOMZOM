package Soldiers;

public class Spearman extends Soldier{
	private static final String IMAGE_FILE = "/assets/spearman-sprite.gif";
	private static final String TYPE = Soldier.ARCHER;
	private static final int HEALTH = 100 ;
	private static final int DAMAGE = 50 ;
	private static final int WIDTH = 132 ;
	private static final int HEIGHT = 132 ;
	private static final int COST = 75;
	
	public Spearman(int col, int lane, int cost) {
		super(col, lane, Spearman.WIDTH, Spearman.HEIGHT, IMAGE_FILE);
		setDamage(DAMAGE);
		setHealth(HEALTH);
		setType(TYPE);
		setSoldierCost(COST);
	}
}
