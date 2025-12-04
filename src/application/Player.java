package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    private static final long serialVersionUID = 2L; // Updated version ID

    // --- LOGIN FIELDS ---
    private String username;
    private String password;

    // --- GAME STATS ---
    private int currency;
    private int burger;
    private int level;
    private int experiencePoints;
    private int experienceToNextLevel;
    
    // --- INVENTORY & WEAPONS ---
    private List<InventoryItem> inventory;
    private List<String> equippedWeapons; // The new List system

    // --- CONSTRUCTOR REQUIRED BY LOGIN SCREEN ---
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        
        // Default Stats
        this.currency = 5000;
        this.burger = 0;
        this.level = 1; 
        this.experiencePoints = 0;
        this.experienceToNextLevel = 100;
        
        this.inventory = new ArrayList<>();
        this.equippedWeapons = new ArrayList<>();
    }

    // --- LOGIN METHODS REQUIRED BY LOGIN SCREEN ---
    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String inputPassword) {
        if (this.password == null) return false;
        return this.password.equals(inputPassword);
    }

    // --- NEW WEAPON MANAGEMENT METHODS ---

    /**
     * Returns the list of currently equipped weapons.
     */
    public List<String> getEquippedWeapons() {
        if (equippedWeapons == null) equippedWeapons = new ArrayList<>();
        return equippedWeapons;
    }
    
    // Helper for backward compatibility if logic calls getEquippedWeapon (singular)
    public String getEquippedWeapon() {
        if (equippedWeapons != null && !equippedWeapons.isEmpty()) {
            return equippedWeapons.get(0); // Return first weapon
        }
        return InventoryItem.HAND;
    }
    
    // Helper to set singular (clears list and adds one), good for simple resets
    public void setEquippedWeapon(String weapon) {
        if (weapon.equals(InventoryItem.HAND)) {
            // If setting to hand, we essentially clear the equipped list
            // But usually, we just don't store "Hand" in the list.
            equippedWeapons.clear();
        } else {
            // If legacy code calls this, we treat it as the primary weapon
            if (!equippedWeapons.contains(weapon)) {
                if (equippedWeapons.size() >= 2) {
                    equippedWeapons.remove(0); // Remove oldest
                }
                equippedWeapons.add(weapon);
            }
        }
    }

    /**
     * Tries to equip a weapon. Only allows if not already equipped and count < 2.
     */
    public void equipWeapon(String weaponName) {
        if (equippedWeapons == null) equippedWeapons = new ArrayList<>();
        
        if (!equippedWeapons.contains(weaponName)) {
            if (equippedWeapons.size() < 2) {
                equippedWeapons.add(weaponName);
                System.out.println("Equipped: " + weaponName);
            } else {
                System.out.println("Cannot equip " + weaponName + ". Max limit (2) reached.");
            }
        }
    }

    /**
     * Removes a weapon from the equipped list.
     */
    public void unequipWeapon(String weaponName) {
        if (equippedWeapons != null && equippedWeapons.contains(weaponName)) {
            equippedWeapons.remove(weaponName);
            System.out.println("Unequipped: " + weaponName);
        }
    }

    /**
     * Checks if a specific weapon is currently in the equipped list.
     */
    public boolean isWeaponEquipped(String weaponName) {
        if (equippedWeapons == null) return false;
        return equippedWeapons.contains(weaponName);
    }

    // --- EXISTING GETTERS/SETTERS ---

    public int getCurrency() { return currency; }
    public void setCurrency(int currency) { this.currency = currency; }
    public void addCurrency(int amount) { this.currency += amount; }
    public void deductCurrency(int amount) { this.currency -= amount; }
    
    public int getBurger() { return burger; }
    public void setBurger(int burger) { this.burger = burger; }
    public void addBurger(int amount) { this.burger += amount; }
    public void deductBurger(int amount) { this.burger -= amount; }

    public int getLevel() { return level; }
    
    public int getExperiencePoints() { return experiencePoints; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }

    public void addExperience(int amount) {
    	
        this.experiencePoints += amount;
        if (this.experiencePoints >= this.experienceToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.experiencePoints -= this.experienceToNextLevel;
        this.experienceToNextLevel = (int) (this.experienceToNextLevel * 1.5);
        System.out.println("Level Up! New Level: " + this.level);
    }

    public List<InventoryItem> getInventory() { 
        if (inventory == null) inventory = new ArrayList<>();
        return inventory; 
    }
    
    public void addItem(InventoryItem item) {
        if (inventory == null) inventory = new ArrayList<>();
        boolean found = false;
        for (InventoryItem i : inventory) {
            if (i.getName().equals(item.getName())) {
                i.addQuantity(item.getQuantity());
                found = true;
                break;
            }
        }
        if (!found) {
            inventory.add(item);
        }
    }
}