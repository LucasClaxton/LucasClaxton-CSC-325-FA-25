package com.dungeonCrawler.characters;

import com.dungeonCrawler.items.Inventory;
import com.dungeonCrawler.rooms.Room;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all game characters.
 * Defines common attributes and abstract methods that all character subclasses must implement.
 */
public abstract class GameCharacter extends Thread {
    protected String name;
    protected int maxHealth;
    protected int currentHealth;
    protected int mana;
    protected int maxMana;
    protected int level;
    protected int experience;
    protected int gold;
    protected Inventory inventory;
    protected List<String> battleLog;
    protected int battlesWon;
    protected int itemsCollected;
    protected boolean isAlive;
    
    public GameCharacter(String name, int maxHealth, int maxMana) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.mana = maxMana;
        this.maxMana = maxMana;
        this.level = 1;
        this.experience = 0;
        this.gold = 100;
        this.inventory = new Inventory();
        this.battleLog = new ArrayList<>();
        this.battlesWon = 0;
        this.itemsCollected = 0;
        this.isAlive = true;
    }
    
    /**
     * Abstract method that defines the character's primary action.
     * Each character type implements this uniquely.
     */
    public abstract void act(Room room);
    
    /**
     * Abstract method for unique ability usage.
     */
    public abstract void useAbility();
    
    /**
     * Returns the character's class name for logging.
     */
    public abstract String getCharacterClass();
    
    /**
     * Heal the character, up to max health.
     */
    public synchronized void heal(int amount) {
        currentHealth = Math.min(currentHealth + amount, maxHealth);
        addLog(name + " healed for " + amount + " HP. Current health: " + currentHealth);
    }
    
    /**
     * Take damage and check if character is still alive.
     */
    public synchronized void takeDamage(int amount) {
        currentHealth -= amount;
        addLog(name + " took " + amount + " damage. Current health: " + currentHealth);
        if (currentHealth <= 0) {
            currentHealth = 0;
            isAlive = false;
            addLog(name + " has been defeated!");
        }
    }
    
    /**
     * Restore mana.
     */
    public synchronized void restoreMana(int amount) {
        mana = Math.min(mana + amount, maxMana);
        addLog(name + " restored " + amount + " mana. Current mana: " + mana);
    }
    
    /**
     * Deduct mana (used for abilities).
     */
    public synchronized boolean deductMana(int amount) {
        if (mana >= amount) {
            mana -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Add to gold count.
     */
    public synchronized void addGold(int amount) {
        gold += amount;
        addLog(name + " gained " + amount + " gold. Total: " + gold);
    }
    
    /**
     * Add gold without logging (for combat rewards).
     */
    public synchronized void addGoldSilent(int amount) {
        gold += amount;
    }
    
    /**
     * Spend gold.
     */
    public synchronized boolean spendGold(int amount) {
        if (gold >= amount) {
            gold -= amount;
            return true;
        }
        return false;
    }
    
    /**
     * Add experience and handle leveling up.
     */
    public synchronized void addExperience(int amount) {
        experience += amount;
        addLog(name + " gained " + amount + " experience.");
        
        // Level up every 100 experience
        while (experience >= 100) {
            experience -= 100;
            level++;
            // Increase stats more per level: larger HP and Mana gains
            maxHealth += 25; // previously 10
            currentHealth = maxHealth;
            maxMana += 10; // previously 5
            mana = maxMana;
            addLog(name + " leveled up to level " + level + "!");
        }
    }
    
    /**
     * Add experience without logging (for combat rewards).
     */
    public synchronized void addExperienceSilent(int amount) {
        experience += amount;
        
        // Level up every 100 experience
        while (experience >= 100) {
            experience -= 100;
            level++;
            // Silent level-up with larger gains
            maxHealth += 25;
            currentHealth = maxHealth;
            maxMana += 10;
            mana = maxMana;
        }
    }
    
    /**
     * Log events to the character's battle log.
     */
    public synchronized void addLog(String message) {
        battleLog.add("[" + name + "] " + message);
    }
    
    /**
     * Log events without adding the character name prefix (for unified combat logs).
     */
    public synchronized void addLogDirect(String message) {
        battleLog.add(message);
    }
    
    /**
     * Record a battle victory.
     */
    public synchronized void winBattle() {
        battlesWon++;
    }
    
    /**
     * Record item collection.
     */
    public synchronized void collectItem() {
        itemsCollected++;
    }
    
    // Getters
    public String getCharacterName() { return name; }
    public int getHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public int getMana() { return mana; }
    public int getMaxMana() { return maxMana; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public int getGold() { return gold; }
    public Inventory getInventory() { return inventory; }
    public List<String> getBattleLog() { return battleLog; }
    public int getBattlesWon() { return battlesWon; }
    public int getItemsCollected() { return itemsCollected; }
    public boolean isCharacterAlive() { return isAlive; }
}
