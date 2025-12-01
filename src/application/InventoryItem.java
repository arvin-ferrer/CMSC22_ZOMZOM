package application;

import java.io.Serializable;

public class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String imagePath;
    private String description;

    public InventoryItem(String name, String imagePath, String description) {
        this.name = name;
        this.imagePath = imagePath;
        this.description = description;
    }

    public String getName() { return name; }
    public String getImagePath() { return imagePath; }
    public String getDescription() { return description; }
}