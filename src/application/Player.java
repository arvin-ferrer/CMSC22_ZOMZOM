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
//    private int 
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

    // Constructor for loading existing player
    public Player(String username, String password, int level, int xp, int currency, int burger) {
        this.username = username;
        this.password = password;
        this.level = level;
        this.burger = burger;
        this.experiencePoints = xp;
        this.currency = currency;
        this.experienceToNextLevel = 100 * level; 
        this.inventory = new ArrayList<>();
    }
 
    /**
     * Adds XP and handles Level Up logic automatically.
     * Preserves overflow XP (e.g., if you have 90/100 and gain 20, you become level 2 with 10/150).
     */
    public void addExperience(int amount) {
        this.experiencePoints += amount;
        
        // Loop in case they gain enough XP to level up multiple times
        while (this.experiencePoints >= this.experienceToNextLevel) {
            this.experiencePoints -= this.experienceToNextLevel; // Keep the overflow!
            this.level++;
            
            // Reward
            this.currency += 200; 
            
            // Increase difficulty for next level (e.g., * 1.2 or +100)
            this.experienceToNextLevel = (int)(this.experienceToNextLevel * 1.2); 
            
            System.out.println("LEVEL UP! " + this.username + " is now level " + this.level);
        }
    }

    public void addItem(InventoryItem newItem) {
        if (inventory == null) inventory = new ArrayList<>();
        
        for (InventoryItem existingItem : inventory) {
            if (existingItem.getName().equalsIgnoreCase(newItem.getName())) {
                existingItem.addQuantity(newItem.getQuantity()); 
                System.out.println("Stacked " + newItem.getName() + ". Total: " + existingItem.getQuantity());
                return; 
            }
        }
        inventory.add(newItem);
        System.out.println("Added new item: " + newItem.getName());
    }
    
    public void setInventory(List<InventoryItem> inventory) {
        this.inventory = inventory;
        if (this.inventory == null) this.inventory = new ArrayList<>();
    }

    public List<InventoryItem> getInventory() {
        if (this.inventory == null) this.inventory = new ArrayList<>();
        return this.inventory;
    }

    // --- GETTERS AND SETTERS ---
    public String getUsername() { return username; }
    public boolean checkPassword(String attempt) { return this.password.equals(attempt); }

    public int getLevel() { return level; }
    public int getExperiencePoints() { return experiencePoints; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }

    public int getCurrency() { return currency; }
    public void setCurrency(int currency) { this.currency = currency; }
    public void addCurrency(int amount) { this.currency += amount; }
    public void deductCurrency(int amount) { this.currency -= amount; }

    public int getBurger() { return burger; }
    public void setBurger(int burger) { this.burger = burger; }
    public void addBurger(int amount) { this.burger += amount; }
    public void deductBurger(int amount) { this.burger -= amount; }
}