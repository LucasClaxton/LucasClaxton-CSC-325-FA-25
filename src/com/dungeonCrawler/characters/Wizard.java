package com.dungeonCrawler.characters;

import com.dungeonCrawler.rooms.Room;
import java.util.Random;

/**
 * Wizard class - Masters of magic with high mana pool and spellcasting.
 */
public class Wizard extends GameCharacter {
    private Random random;
    private int intelligence;
    private int spellPower;
    
    public Wizard(String name) {
        super(name, 100, 200);
        this.random = new Random();
        this.intelligence = 40;
        this.spellPower = 30;
        this.setName(name);
    }
    
    @Override
    public String getCharacterClass() {
        return "Wizard";
    }
    
    @Override
    public void act(Room room) {
        // Wizard explores the room with magical prowess
        Random rand = new Random();
        int action = rand.nextInt(3);
        
        switch(action) {
            case 0:
                addLog(name + " channels mystical energy around them...");
                break;
            case 1:
                addLog(name + " traces glowing runes in the air!");
                break;
            case 2:
                addLog(name + " mutters arcane incantations.");
                break;
        }
    }
    
    @Override
    public void useAbility() {
        if (mana >= 40) {
            mana -= 40;
            addLog(name + " casts FIREBALL! Dealing massive area damage!");
            addLog(name + " increased spell power by 15.");
            spellPower += 15;
            
            // Reduce spell power after cooldown
            new Thread(() -> {
                try {
                    Thread.sleep(3500);
                    synchronized(this) {
                        spellPower -= 15;
                        addLog(name + "'s fireball effect cools down.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            addLog(name + " doesn't have enough mana for Fireball! (Requires 40 mana)");
        }
    }
    
    /**
     * Wizard's unique ability: Arcane Missile
     */
    public void arcaneMissile() {
        if (mana >= 15) {
            mana -= 15;
            int damage = spellPower + random.nextInt(10);
            addLog(name + " casts ARCANE MISSILE for " + damage + " damage!");
        } else {
            addLog(name + " doesn't have enough mana for Arcane Missile!");
        }
    }
    
    /**
     * Wizard's unique ability: Mana Shield
     */
    public void manaShield() {
        if (mana >= 50) {
            mana -= 50;
            addLog(name + " creates a MANA SHIELD, converting mana to temporary armor!");
            int temporaryArmor = 30;
            addLog(name + " gained " + temporaryArmor + " temporary armor.");
            
            try {
                Thread.sleep(2000);
                addLog(name + "'s mana shield dissipates.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            addLog(name + " doesn't have enough mana for Mana Shield!");
        }
    }
    
    /**
     * Wizard's unique ability: Teleport
     */
    public void teleport() {
        if (mana >= 35) {
            mana -= 35;
            addLog(name + " casts TELEPORT and vanishes in a flash of arcane light!");
            addLog(name + " reappears at a safe distance!");
            
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            addLog(name + " doesn't have enough mana for Teleport!");
        }
    }
    
    /**
     * Wizard's unique ability: Mana Drain
     */
    public void manaDrain() {
        addLog(name + " drains mana from the environment!");
        restoreMana(30);
    }
    
    @Override
    public void run() {
        addLog(name + " the Wizard enters the dungeon, eyes glowing with arcane power...");
        try {
            Thread.sleep(500 + random.nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public int getIntelligence() { return intelligence; }
    public int getSpellPower() { return spellPower; }
}
