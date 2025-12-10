package com.dungeonCrawler.characters;

import com.dungeonCrawler.rooms.Room;
import com.dungeonCrawler.items.Equipment;
import com.dungeonCrawler.items.EquipmentType;
import java.util.Random;

/**
 * Knight class - Strong melee fighter with focus on defense and heavy hits.
 */
public class Knight extends GameCharacter {
    private Random random;
    private int armor;
    private int weaponDamage;
    
    public Knight(String name) {
        super(name, 200, 50);
        this.random = new Random();
        this.armor = 20;
        this.weaponDamage = 25;
        this.setName(name);
    }
    
    @Override
    public String getCharacterClass() {
        return "Knight";
    }
    
    @Override
    public void act(Room room) {
        // Knight explores the room with combat prowess
        Random rand = new Random();
        int action = rand.nextInt(3);
        
        switch(action) {
            case 0:
                addLog(name + " charges forward with shield raised!");
                break;
            case 1:
                addLog(name + " swings their longsword with tremendous force!");
                break;
            case 2:
                addLog(name + " prepares a defensive stance.");
                break;
        }
    }
    
    @Override
    public void useAbility() {
        if (mana >= 30) {
            mana -= 30;
            addLog(name + " uses SHIELD BASH! Blocks incoming damage and stuns enemies!");
            addLog(name + " increased armor by 15 temporarily.");
            armor += 15;
            
            // Reduce armor after time
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    synchronized(this) {
                        armor -= 15;
                        addLog(name + "'s shield bash effect wears off.");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            addLog(name + " doesn't have enough mana for Shield Bash! (Requires 30 mana)");
        }
    }
    
    /**
     * Knight's unique ability: Heavy Attack
     */
    public void heavyAttack() {
        if (weaponDamage > 0) {
            int damage = weaponDamage + random.nextInt(15);
            addLog(name + " performs a HEAVY ATTACK dealing " + damage + " damage!");
        }
    }
    
    /**
     * Knight's unique ability: Defensive Stance
     */
    public void defensiveStance() {
        armor += 10;
        addLog(name + " takes a DEFENSIVE STANCE! Armor increased by 10.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        armor -= 10;
        addLog(name + "'s defensive stance ends.");
    }
    
    @Override
    public void run() {
        addLog(name + " the Knight enters the dungeon, ready for battle!");
        try {
            Thread.sleep(500 + random.nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public int getArmor() { return armor; }
    public void setArmor(int amount) { this.armor = amount; }
}
