package application;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Existing Items
    public static final String WOOD = "Wood";
    public static final String CLOTH = "Cloth";
    public static final String BANDAGE = "Bandage";
    public static final String MEDKIT = "Medkit";
    public static final String STONE = "Rock";
    public static final String GUNPOWDER = "Gunpowder";
    public static final String GRENADE = "Grenade";
    public static final String BARRIER = "Barrier";
    
    // NEW WEAPONS
    public static final String HAND = "Hand";
    public static final String MALLET = "Mallet";
    public static final String KATANA = "Katana";
    public static final String MACHINE_GUN = "Machine Gun";
    
    // Images
    public static final String WOOD_IMAGE = "/assets/wood.png";
    public static final String CLOTH_IMAGE = "/assets/whiteCloth.png";
    public static final String BANDAGE_IMAGE = "/assets/bandage.png";
    public static final String MEDKIT_IMAGE = "/assets/medkit.png";
    public static final String STONE_IMAGE = "/assets/stone.png";
    public static final String GRENADE_IMAGE = "/assets/grenade-sprite.png";
    public static final String BARRIER_IMAGE = "/assets/barrier-card.png";
    public static final String GUNPOWDER_IMAGE = "/assets/gunpowder.png";
    
    // Weapon Images (Ensure you have these files or use placeholders)
    public static final String MALLET_IMAGE = "/assets/mallet-card.png";
    public static final String KATANA_IMAGE = "/assets/katana-card.png";
    public static final String MACHINE_GUN_IMAGE = "/assets/gun-card.png";
    public static final String HAND_IMAGE = "/assets/hand-card.png"; // Optional icon

    private String name;
    private String imagePath;
    private String description;
    private int quantity;

    public InventoryItem(String name, String imagePath, String description) {
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
        this.quantity = 1; 
    }

    public InventoryItem(String name, String imagePath, String description, int quantity) {
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
        this.quantity = quantity;
    }

    public void addQuantity(int amount) { this.quantity += amount; }
    public int getQuantity() { return this.quantity; }
    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }
}