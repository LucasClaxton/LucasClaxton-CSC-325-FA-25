package com.dungeonCrawler.characters;

import com.dungeonCrawler.rooms.Room;
import java.util.Random;

/**
 * Thief class - Fast and agile, focuses on evasion and critical strikes.
 */
public class Thief extends GameCharacter {
    private Random random;
    private int agility;
    private int movement;  // Dodge chance stat (0-100)
    private int criticalChance;
    
    public Thief(String name) {
        super(name, 120, 80);
        this.random = new Random();
        this.agility = 35;
        this.movement = 45;  // 45% base dodge chance
        this.criticalChance = 25; // 25% base critical chance
        this.setName(name);
    }
    
    @Override
    public String getCharacterClass() {
        return "Thief";
    }
    
    @Override
    public void act(Room room) {
        // Thief explores the room with stealth and precision
        Random rand = new Random();
        int action = rand.nextInt(3);
        
        switch(action) {
            case 0:
                addLog(name + " moves silently through the shadows...");
                break;
            case 1:
                addLog(name + " searches for hidden treasures with keen eyes!");
                break;
            case 2:
                addLog(name + " readies daggers in both hands.");
                break;
        }
    }
    
    @Override
    public void useAbility() {
        if (mana >= 25) {
            mana -= 25;
            addLog(name + " uses SHADOW CLONE! Evades next attack and moves behind enemy!");
            addLog(name + "'s agility increased by 20.");
            agility += 20;
            
            // Reduce agility after effect wears off
            new Thread(() -> {
                try {
                    Thread.sleep(2500);
                    synchronized(this) {
                        agility -= 20;
                        addLog(name + "'s shadow clone fades away.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            addLog(name + " doesn't have enough mana for Shadow Clone! (Requires 25 mana)");
        }
    }
    
    /**
     * Thief's unique ability: Backstab
     */
    public void backstab() {
        int baseDamage = 20;
        int critMultiplier = random.nextInt(100) < criticalChance ? 2 : 1;
        int totalDamage = baseDamage * critMultiplier;
        
        if (critMultiplier == 2) {
            addLog(name + " lands a CRITICAL BACKSTAB for " + totalDamage + " damage!");
        } else {
            addLog(name + " performs a backstab for " + totalDamage + " damage!");
        }
    }
    
    /**
     * Thief's unique ability: Pickpocket
     */
    public void pickpocket() {
        int gold = random.nextInt(50) + 25;
        this.gold += gold;
        addLog(name + " uses pickpocket and gains " + gold + " gold!");
    }
    
    /**
     * Thief's unique ability: Evasion
     */
    public void evasion() {
        addLog(name + " takes an evasive stance! Dodge chance increased!");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        addLog(name + " the Thief enters the dungeon, moving silently...");
        try {
            Thread.sleep(500 + random.nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public int getAgility() { return agility; }
    public int getMovement() { return movement; }
    public int getCriticalChance() { return criticalChance; }
}
