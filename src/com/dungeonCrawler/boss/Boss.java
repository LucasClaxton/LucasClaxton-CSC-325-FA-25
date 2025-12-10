package com.dungeonCrawler.boss;

import com.dungeonCrawler.characters.GameCharacter;
import java.util.Random;

/**
 * Boss class for floor bosses and final antagonist.
 */
public class Boss {
    private String name;
    private String title;
    private int health;
    private int maxHealth;
    private int attackPower;
    private String story;
    private boolean defeated;
    private int floorNumber;
    
    public Boss(String name, String title, int health, int attackPower, String story, int floorNumber) {
        this.name = name;
        this.title = title;
        this.health = health;
        this.maxHealth = health;
        this.attackPower = attackPower;
        this.story = story;
        this.defeated = false;
        this.floorNumber = floorNumber;
    }
    
    /**
     * Boss entrance with dramatic narration.
     */
    public void entrance() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** " + title.toUpperCase() + " ***");
        System.out.println("=".repeat(80));
        System.out.println(story);
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * Battle with all characters.
     */
    public void battle(GameCharacter[] characters) {
        if (defeated) return;
        
        Random random = new Random();
        boolean allDefeated = true;
        
        // Check if all characters are alive
        for (GameCharacter c : characters) {
            if (c.isCharacterAlive()) {
                allDefeated = false;
                break;
            }
        }
        
        if (allDefeated) {
            System.out.println("\n*** THE HEROES HAVE FALLEN ***\n");
            defeated = true;
            return;
        }
        
        // Combat phase
        System.out.println("\n[BOSS BATTLE] " + name + " attacks!");
        
        for (GameCharacter character : characters) {
            if (character.isCharacterAlive()) {
                int heroDamage = random.nextInt(25) + 15;
                health -= heroDamage;
                
                System.out.println("[HERO] " + character.getCharacterName() + " strikes for " + heroDamage + " damage!");
                
                if (health <= 0) {
                    health = 0;
                    defeated = true;
                    System.out.println("\n*** " + name + " HAS BEEN DEFEATED! ***\n");
                    
                    // Reward heroes
                    for (GameCharacter c : characters) {
                        if (c.isCharacterAlive()) {
                            c.addExperience(200);
                            c.addGold(500);
                            c.winBattle();
                        }
                    }
                    return;
                }
                
                // Boss counterattack
                int bossDamage = random.nextInt(20) + 10;
                character.takeDamage(bossDamage);
                System.out.println("[BOSS] " + name + " counterattacks " + character.getCharacterName() + " for " + bossDamage + " damage!");
            }
        }
        
        // Display hero health and mana after round
        System.out.println("\n--- Hero Status ---");
        for (GameCharacter character : characters) {
            if (character.isCharacterAlive()) {
                System.out.println(character.getCharacterName() + ": " + character.getHealth() + "/" + character.getMaxHealth() + 
                                 " HP | " + character.getMana() + "/" + character.getMaxMana() + " Mana");
            } else {
                System.out.println(character.getCharacterName() + ": DEFEATED");
            }
        }
        System.out.println(name + ": " + health + "/" + maxHealth + " HP\n");
    }
    
    public String getName() { return name; }
    public String getTitle() { return title; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDefeated() { return defeated; }
    public int getFloorNumber() { return floorNumber; }
}
