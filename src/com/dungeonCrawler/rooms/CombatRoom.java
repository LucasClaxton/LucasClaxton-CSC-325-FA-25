package com.dungeonCrawler.rooms;

import com.dungeonCrawler.characters.GameCharacter;
import com.dungeonCrawler.characters.Knight;
import com.dungeonCrawler.characters.Thief;
import com.dungeonCrawler.characters.Wizard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Combat room - Multithreaded turn-based combat where all heroes attack simultaneously,
 * then enemy responds.
 */
public class CombatRoom implements Room {
    private Random random;
    private String enemyName;
    private int enemyMaxHealth;
    private int enemyHealth;
    private int enemyAttackPower;
    private boolean defeated;
    private List<GameCharacter> allHeroes = new ArrayList<>();
    private boolean combatStarted = false;
    // Track last total gold earned from the most recent combat
    private int lastGoldEarned = 0;
    private java.util.Set<GameCharacter> restingHeroes = new java.util.HashSet<>();
    
    public CombatRoom(int floorNumber) {
        this.random = new Random();
        this.defeated = false;
        generateEnemy(floorNumber);
    }
    
        public CombatRoom(int floorNumber, java.util.Set<GameCharacter> restingHeroes) {
            this.random = new Random();
            this.defeated = false;
            this.restingHeroes = restingHeroes != null ? new java.util.HashSet<>(restingHeroes) : new java.util.HashSet<>();
            generateEnemy(floorNumber);
        }
    
    private void generateEnemy(int floorNumber) {
        String[] enemies = {"Goblin", "Orc", "Troll", "Skeleton", "Wraith", "Drake"};
        this.enemyName = enemies[random.nextInt(enemies.length)];
        this.enemyMaxHealth = 50 + (floorNumber * 20) + random.nextInt(30);
        this.enemyHealth = enemyMaxHealth;
        // Reduce enemy attack scaling so bosses deal less damage per floor
        this.enemyAttackPower = 5 + (floorNumber * 1); // previously 8 + floor*2
    }
    
    @Override
    public void enter(GameCharacter character) {
        // Skip entry if hero is resting
        if (restingHeroes.contains(character)) {
            return;
        }
        
        boolean shouldStartCombat = false;
        
        synchronized(this) {
            // Add hero if not already present
            if (!allHeroes.contains(character) && character.isCharacterAlive()) {
                allHeroes.add(character);
                
                broadcastToAll("\n" + "=".repeat(60));
                broadcastToAll("*** COMBAT STARTED ***");
                broadcastToAll("Party encounters a " + enemyName + "!");
                broadcastToAll("=".repeat(60) + "\n");
            }
            
            // Start combat when we have all 3 heroes OR after giving time for them to join
            if (!combatStarted && allHeroes.size() == 3) {
                combatStarted = true;
                shouldStartCombat = true;
            }
        }
        
        // Start combat OUTSIDE the synchronized block so other heroes can still enter
        if (shouldStartCombat) {
            conductMultithreadedCombat();
        }
    }
    
    private void conductMultithreadedCombat() {
        broadcastToAll("\n" + "=".repeat(60));
        broadcastToAll("ENEMIES: " + enemyName + " - HP: " + enemyHealth + "/" + enemyMaxHealth);
        broadcastToAll("=".repeat(60) + "\n");
        
        int round = 1;
        
        while (!defeated && enemyHealth > 0) {
            // Check if all heroes are defeated
            int aliveHeroes = 0;
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    aliveHeroes++;
                }
            }
            
            if (aliveHeroes == 0) {
                broadcastToAll("\n" + "=".repeat(60));
                broadcastToAll("*** ALL HEROES DEFEATED! ***");
                broadcastToAll("=".repeat(60) + "\n");
                break;
            }
            
            broadcastToAll("\n--- ROUND " + round + " ---");
            broadcastToAll(enemyName + " HP: " + enemyHealth + "/" + enemyMaxHealth + "\n");
            
            // Display all hero statuses before actions
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    broadcastToAll(hero.getCharacterName() + ": " + hero.getHealth() + "/" + 
                                 hero.getMaxHealth() + " HP | " + hero.getMana() + "/" + 
                                 hero.getMaxMana() + " Mana");
                }
            }
            broadcastToAll("");
            
            // ===== SIMULTANEOUS HERO ATTACKS =====
            broadcastToAll(">>> HEROES ATTACK! <<<\n");
            
            // Use CountDownLatch to synchronize all hero attacks
            CountDownLatch attackLatch = new CountDownLatch(0);
            int livingHeroes = 0;
            
            // Count living heroes for latch
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    livingHeroes++;
                }
            }
            
            if (livingHeroes > 0) {
                attackLatch = new CountDownLatch(livingHeroes);
            }
            
            int totalDamage = 0;
            List<Integer> heroDamages = new ArrayList<>();
            
            // Launch attack threads for each living hero
            final CountDownLatch finalLatch = attackLatch;
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    Thread attackThread = new Thread(() -> {
                        int damage = executeHeroAttack(hero);
                        synchronized(heroDamages) {
                            heroDamages.add(damage);
                        }
                        finalLatch.countDown();
                    });
                    attackThread.start();
                }
            }
            
            // Wait for all attacks to complete
            try {
                finalLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Calculate total damage
            for (int damage : heroDamages) {
                totalDamage += damage;
            }
            
            enemyHealth -= totalDamage;
            broadcastToAll(">>> Total Damage Dealt: " + totalDamage + " <<<\n");
            
            // Check if enemy is defeated
            if (enemyHealth <= 0) {
                broadcastToAll("\n" + "=".repeat(60));
                broadcastToAll("*** " + enemyName.toUpperCase() + " DEFEATED! ***");
                broadcastToAll("=".repeat(60) + "\n");
                
                // Reward all living heroes and calculate total gold
                int totalGoldEarned = 0;
                for (GameCharacter hero : allHeroes) {
                    if (hero.isCharacterAlive()) {
                        int goldReward = random.nextInt(20) + 30;
                        hero.addExperienceSilent(50);
                        hero.addGoldSilent(goldReward);
                        hero.winBattle();
                        totalGoldEarned += goldReward;
                        // Show individual hero experience gain without name prefix
                        broadcastToAll(hero.getCharacterName() + " gained 50 experience!");
                    }
                }
                
                // Record and display total gold collected
                this.lastGoldEarned = totalGoldEarned;
                broadcastToAll("Gained " + totalGoldEarned + " gold total!");
                
                broadcastToAll("=".repeat(60) + "\n");
                defeated = true;
                break;
            }
            
            // ===== ENEMY CHOOSES RANDOM TARGET AND ATTACKS =====
            broadcastToAll("\n>>> " + enemyName.toUpperCase() + " ATTACKS! <<<\n");
            
            // Choose random living hero to attack
            List<GameCharacter> livingTargets = new ArrayList<>();
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    livingTargets.add(hero);
                }
            }
            
            if (!livingTargets.isEmpty()) {
                GameCharacter target = livingTargets.get(random.nextInt(livingTargets.size()));
                executeEnemyAttack(target);
            }
            
            broadcastToAll("");
            // Display hero health/mana after round segment
            broadcastToAll("--- End of Round " + round + " ---");
            for (GameCharacter hero : allHeroes) {
                if (hero.isCharacterAlive()) {
                    broadcastToAll(hero.getCharacterName() + ": " + hero.getHealth() + "/" + 
                                 hero.getMaxHealth() + " HP | " + hero.getMana() + "/" + 
                                 hero.getMaxMana() + " Mana");
                }
            }
            broadcastToAll("");
            round++;
        }
    }
    
    private int executeHeroAttack(GameCharacter hero) {
        int baseDamage = random.nextInt(12) + 8;
        int totalDamage = baseDamage;
        
        broadcastToAll(hero.getCharacterName() + " (" + hero.getCharacterClass() + ") attacks!");
        
        if (hero instanceof Wizard) {
            totalDamage = executeWizardAttack((Wizard) hero, baseDamage);
        } else if (hero instanceof Thief) {
            totalDamage = executeThiefAttack((Thief) hero, baseDamage);
        } else if (hero instanceof Knight) {
            totalDamage = executeKnightAttack((Knight) hero, baseDamage);
        }
        
        broadcastToAll(hero.getCharacterName() + " deals " + totalDamage + " damage!\n");
        
        return totalDamage;
    }
    
    private int executeWizardAttack(Wizard wizard, int baseDamage) {
        if (wizard.getMana() >= 20) {
            wizard.deductMana(20);
            int spellDamage = baseDamage + 15;
            broadcastToAll("  🔮 Wizard casts FIREBALL (-20 mana) for " + spellDamage + " total damage!");
            return spellDamage;
        } else {
            broadcastToAll("  ⚠️  Not enough mana for spell! Basic attack only (-5 mana)");
            wizard.deductMana(5);
            return baseDamage;
        }
    }
    
    private int executeThiefAttack(Thief thief, int baseDamage) {
        int dodgeChance = thief.getMovement();
        
        if (random.nextInt(100) < 20) {
            if (thief.getMana() >= 15) {
                thief.deductMana(15);
                int goldStolen = random.nextInt(20) + 10;
                broadcastToAll("  💰 Thief uses STEAL GOLD (-15 mana) and steals " + goldStolen + " gold!");
                broadcastToAll("  (Base attack reduced this turn)");
                return baseDamage / 2;
            }
        }
        
        if (random.nextInt(100) < thief.getCriticalChance()) {
            int critDamage = baseDamage * 2;
            broadcastToAll("  ⚡ CRITICAL STRIKE for " + critDamage + " damage!");
            return critDamage;
        }
        
        broadcastToAll("  🗡️  Thief strikes with " + baseDamage + " damage (Dodge: " + dodgeChance + "%)");
        return baseDamage;
    }
    
    private int executeKnightAttack(Knight knight, int baseDamage) {
        if (random.nextInt(100) < 30) {
            if (knight.getMana() >= 25) {
                knight.deductMana(25);
                broadcastToAll("  🛡️  Knight casts SHIELD SPELL (-25 mana)");
                broadcastToAll("  (Defense increased this round - reduced incoming damage)");
                broadcastToAll("  (Reduced attack power this turn)");
                return baseDamage / 2;
            }
        }
        
        broadcastToAll("  🗡️  Knight performs a strong attack for " + baseDamage + " damage!");
        return baseDamage;
    }
    
    private void executeEnemyAttack(GameCharacter target) {
        boolean dodged = false;
        int incomingDamage = enemyAttackPower + random.nextInt(8);
        
        broadcastToAll(enemyName + " targets " + target.getCharacterName() + "!");
        
        if (target instanceof Thief) {
            Thief thief = (Thief) target;
            int dodgeChance = thief.getMovement();
            if (random.nextInt(100) < dodgeChance) {
                broadcastToAll("  ✨ " + target.getCharacterName() + " DODGES the attack! (Dodge: " + dodgeChance + "%)");
                dodged = true;
            }
        }
        
        if (!dodged) {
            broadcastToAll("  💥 " + enemyName + " hits " + target.getCharacterName() + " for " + incomingDamage + " damage!");
            target.takeDamage(incomingDamage);
        }
    }
    
    private void broadcastToAll(String message) {
        for (GameCharacter hero : allHeroes) {
            hero.addLogDirect(message);
        }
    }

    /**
     * Apply an equal enemy attack to all provided heroes and log the results.
     * This is used when combat is skipped (e.g., due to a rest action).
     */
    public int applyEqualDamageToAll(GameCharacter[] party) {
        int damage = enemyAttackPower + random.nextInt(8);
        String header = "\n" + "=".repeat(60) + "\n";
        String title = "*** COMBAT SKIPPED: " + enemyName + " STRIKES ALL HEROES ***";

        for (GameCharacter hero : party) {
            if (hero.isCharacterAlive()) {
                hero.takeDamage(damage);
                String msg = header + title + "\n" + enemyName + " hits " + hero.getCharacterName() + " for " + damage + " damage!\n" + header;
                hero.addLogDirect(msg);
            }
        }
        return damage;
    }

    /**
     * Return the total gold earned by the last completed combat.
     */
    public synchronized int getLastGoldEarned() {
        return lastGoldEarned;
    }

    /**
     * Set the heroes that are resting and should not participate in this combat.
     */
    public synchronized void setRestingHeroes(java.util.Set<GameCharacter> resting) {
        this.restingHeroes = resting != null ? new java.util.HashSet<>(resting) : new java.util.HashSet<>();
    }
    
    @Override
    public String getRoomType() {
        return "Combat Room";
    }
    
    @Override
    public String getDescription() {
        return (defeated ? "The defeated " : "A hostile ") + enemyName + (defeated ? " lies on the ground." : " awaits in combat!");
    }
}
