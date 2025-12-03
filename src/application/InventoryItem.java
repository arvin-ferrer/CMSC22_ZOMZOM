package application;

import java.io.Serializable;

public class InventoryItem implements Serializable {
	public static final String WOOD = "WOOD";
	public static final String CLOTH = "CLOTH";
	public static final String BANDAGE = "BANDAGE";
	public static final String MEDKIT = "MEDKIT";
	public static final String STONE = "STONE";
	public static final String GRENADE = "GRENADE";
	public static final String WOOD_IMAGE = "/assets/wood.png";
	public static final String CLOTH_IMAGE = "/assets/whiteCloth.png";
	public static final String BANDAGE_IMAGE = "/assets/bandage.png";
	public static final String MEDKIT_IMAGE = "/assets/medkit.png";
	public static final String STONE_IMAGE = "/assets/stone.png";
	public static final String GRENADE_IMAGE = "/assets/grenade-sprite.png";
	


    private static final long serialVersionUID = 1L;
    private String name;
    private String imagePath;
    private String description;
    private int quantity;

    public InventoryItem(String name, String imagePath, String description) {
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
    }
    public void incrementQuantity() { this.quantity++; }
    public void addQuantity(int amount) {
        this.quantity += amount;
    }
    public int getQuantity() { return this.quantity; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }
}