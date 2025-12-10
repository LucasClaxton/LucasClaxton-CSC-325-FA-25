package com.dungeonCrawler.utils;

import com.dungeonCrawler.characters.GameCharacter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Utility class for logging and aggregating game statistics using lambdas and streams.
 */
public class GameLogger {
    
    /**
     * Print game statistics using stream operations and lambdas.
     */
    public static void printGameStatistics(GameCharacter[] characters) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** FINAL GAME STATISTICS ***");
        System.out.println("=".repeat(80));
        
        // Using stream operations to aggregate and display stats
        Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .forEach(character -> {
                System.out.println("\n" + character.getCharacterClass() + ": " + character.getCharacterName());
                System.out.println("  Level: " + character.getLevel());
                System.out.println("  Experience: " + character.getExperience());
                System.out.println("  Health: " + character.getHealth() + "/" + character.getMaxHealth());
                System.out.println("  Gold: " + character.getGold());
                System.out.println("  Battles Won: " + character.getBattlesWon());
                System.out.println("  Items Collected: " + character.getItemsCollected());
            });
        
        // Calculate totals using streams and lambdas
        int totalBattles = Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .mapToInt(GameCharacter::getBattlesWon)
            .sum();
        
        int totalItems = Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .mapToInt(GameCharacter::getItemsCollected)
            .sum();
        
        int totalGold = Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .mapToInt(GameCharacter::getGold)
            .sum();
        
        System.out.println("\n" + "-".repeat(80));
        System.out.println("TOTALS:");
        System.out.println("  Total Battles Won: " + totalBattles);
        System.out.println("  Total Items Collected: " + totalItems);
        System.out.println("  Total Gold Collected: " + totalGold);
        System.out.println("-".repeat(80) + "\n");
    }
    
    /**
     * Print event logs with filtering and formatting.
     */
    public static void printEventLogs(GameCharacter[] characters) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** EVENT LOG ***");
        System.out.println("=".repeat(80) + "\n");
        
        // Use streams to flatten and filter all logs
        Arrays.stream(characters)
            .flatMap(character -> character.getBattleLog().stream())
            .forEach(System.out::println);
        
        System.out.println("\n" + "=".repeat(80) + "\n");
    }
    
    /**
     * Rank characters by a specific metric using streams.
     */
    public static void rankCharactersByBattles(GameCharacter[] characters) {
        System.out.println("\n*** RANKING BY BATTLES WON ***\n");
        
        Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .sorted(Comparator.comparingInt(GameCharacter::getBattlesWon).reversed())
            .forEach(character -> 
                System.out.println(character.getCharacterName() + " (" + character.getCharacterClass() + "): " + 
                    character.getBattlesWon() + " battles")
            );
    }
    
    /**
     * Rank characters by gold collected.
     */
    public static void rankCharactersByGold(GameCharacter[] characters) {
        System.out.println("\n*** RANKING BY GOLD COLLECTED ***\n");
        
        Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .sorted(Comparator.comparingInt(GameCharacter::getGold).reversed())
            .forEach(character -> 
                System.out.println(character.getCharacterName() + " (" + character.getCharacterClass() + "): " + 
                    character.getGold() + " gold")
            );
    }
    
    /**
     * Find highest level character.
     */
    public static GameCharacter getHighestLevelCharacter(GameCharacter[] characters) {
        return Arrays.stream(characters)
            .filter(GameCharacter::isCharacterAlive)
            .max(Comparator.comparingInt(GameCharacter::getLevel))
            .orElse(null);
    }
}
