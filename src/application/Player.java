package application;

import java.io.Serializable;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
    private String username;
    private String password;

    private int level;
    private int experiencePoints;
    private int experienceToNextLevel; 
    private int currency;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        
        this.level = 1;
        this.experiencePoints = 0;
        this.currency = 50; // Example starting currency
        this.experienceToNextLevel = 100; // Example XP needed for level 2

    }

    public Player(String username, String password, int level, int xp, int currency) {
        this.username = username;
        this.password = password;
        this.level = level;
        this.experiencePoints = xp;
        this.currency = currency;
        
        this.experienceToNextLevel = 100 * level; // sample lang pwede ibahin pano nagl-level up
        
        // this.inventory = ... // load this from a text file
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
    
    // public Inventory getInventory() {
    //     return inventory;
    // }

}