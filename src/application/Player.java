package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Items.Item;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    private int level;
    private int experiencePoints;
    private int experienceToNextLevel; 
    private int currency;
    private int burger;
    private List<InventoryItem> inventory; 

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.level = 1;
        this.experiencePoints = 0;
        this.currency = 500; 
        this.burger = 300;
        this.experienceToNextLevel = 100;
        
        this.inventory = new ArrayList<>();
    }

    public Player(String username, String password, int level, int xp, int currency, int burger) {
        this.username = username;
        this.password = password;
        this.level = level;
        this.burger = burger;
        this.experiencePoints = xp;
        this.currency = currency;
        this.experienceToNextLevel = 100 * level; 
        
        this.inventory = new ArrayList<>();
        
        addItem(new InventoryItem("Medkit", "/assets/medkit.png", "Heals 50 HP"));
        addItem(new InventoryItem("Burger", "/assets/burger-sprite.png", "Food for Soldiers"));
        addItem(new InventoryItem("Bandage", "/assets/bandage.png", "Heals 50 HP"));
    }
    private void addItem(InventoryItem item) {
            if (this.inventory == null) {
                this.inventory = new ArrayList<>();
            }
            inventory.add(item);
        }
    
    
	public void setInventory(List<InventoryItem> inventory) {
    this.inventory = inventory;
    if (this.inventory == null) {
        this.inventory = new ArrayList<>();
    	}
	}
    public List<InventoryItem> getInventory() {
        if (this.inventory == null) {
            this.inventory = new ArrayList<>();
        }
        for(int i = 0; i < inventory.size(); i++) {
			System.out.println("Item " + i + ": " + inventory.get(i).getName());
		}
        return this.inventory;

    }
    
	public void addExperience(int amount) {
        this.experiencePoints += amount;
        while (this.experiencePoints >= this.experienceToNextLevel) {
            this.level++;
            // subtract the XP required for the level-up
            this.experiencePoints -= this.experienceToNextLevel;
            this.currency += 100; // example reward
            this.experienceToNextLevel = 100 * this.level; 
            System.out.println("Congratulations, " + this.username + "! You are now level " + this.level);
        }
    }


    public String getUsername() {
        return username;
    }

    public void addBurger(int amount) {
		this.burger += amount;
	}
    public int getBurger() { 
    	return burger; 
    }
    public void deductBurger(int amount) {
		this.burger -= amount;
	}
    public void setBurger(int burger) {
    	this.burger = burger;
    }
    public boolean checkPassword(String attempt) {
        return this.password.equals(attempt);
    }

    public int getLevel() {
        return level;
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    public int getExperienceToNextLevel() {
        return experienceToNextLevel;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
    public void addCurrency(int amount) {
		this.currency += amount;
	}
    public void deductCurrency(int amount) {
    	this.currency -= amount;
    }
    
    public void setNextLevel() {
    	this.experienceToNextLevel = this.experienceToNextLevel + (this.experienceToNextLevel / 2);
    }
    
    public void resetLevel() {
    	this.experienceToNextLevel = 50;
    }
    
    public void resetExp() {
    	this.experiencePoints = 0;
    }
 
    
//     public Inventory getInventory() {
//         return inventory;
//     }

}