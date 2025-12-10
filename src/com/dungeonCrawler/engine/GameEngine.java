package com.dungeonCrawler.engine;

import com.dungeonCrawler.characters.GameCharacter;
import com.dungeonCrawler.rooms.*;
import com.dungeonCrawler.boss.Boss;
import com.dungeonCrawler.boss.BossFactory;
import com.dungeonCrawler.items.Potion;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Main game engine that manages the dungeon crawl experience.
 * Handles multithreading, floor progression, room selection, and synchronization.
 */
public class GameEngine {
    private GameCharacter[] characters;
    private int currentFloor;
    private int maxFloors;
    private List<Room> currentFloorRooms;
    private Room shopRoom;
    private boolean shopVisited;
    private Random random;
    private boolean gameActive;
    private ReentrantReadWriteLock lock;
    
    public GameEngine(GameCharacter[] characters, int maxFloors) {
        this.characters = characters;
        this.currentFloor = 1;
        this.maxFloors = maxFloors;
        this.currentFloorRooms = new ArrayList<>();
        this.shopRoom = null;
        this.random = new Random();
        this.gameActive = true;
        this.lock = new ReentrantReadWriteLock();
        this.shopVisited = false;
    }
    
    /**
     * Start the adventure for all characters using threads.
     */
    public void startAdventure() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** WELCOME TO THE DUNGEON CRAWLER ***");
        System.out.println("Three heroes venture into a dark dungeon filled with mysteries...");
        System.out.println("=".repeat(80) + "\n");
        
        // Start each character thread
        Thread[] characterThreads = new Thread[characters.length];
        for (int i = 0; i < characters.length; i++) {
            characterThreads[i] = characters[i];
            characterThreads[i].start();
        }
        
        // Wait for all threads to complete
        try {
            for (Thread t : characterThreads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Generate rooms for the current floor.
     */
    public void generateFloorRooms() {
        lock.writeLock().lock();
        try {
            currentFloorRooms.clear();
            // reset per-floor shop visited flag
            shopVisited = false;
            
            // Always have 5 base rooms + 1 boss room on floors 5, 10
            boolean isBossFloor = (currentFloor % 5 == 0 && currentFloor < maxFloors) || currentFloor == maxFloors;
            
            // Generate 5 random rooms (with shop only once per floor)
            shopRoom = new ShopRoom();
            List<RoomType> roomTypes = new ArrayList<>();
            
            // Add random room types
            for (int i = 0; i < 5; i++) {
                if (i == random.nextInt(5)) {
                    roomTypes.add(RoomType.SHOP);
                } else {
                    roomTypes.add(random.nextBoolean() ? RoomType.CHEST : RoomType.COMBAT);
                }
            }
            
            // Create room objects
            for (RoomType type : roomTypes) {
                switch(type) {
                    case CHEST:
                        currentFloorRooms.add(new ChestRoom());
                        break;
                    case COMBAT:
                        currentFloorRooms.add(new CombatRoom(currentFloor));
                        break;
                    case SHOP:
                        currentFloorRooms.add(shopRoom);
                        break;
                }
            }
            
            // Rooms generated (debug output removed)
            
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Return shared shop room for the current floor (created in generateFloorRooms).
     */
    public Room getShopRoom() {
        return shopRoom;
    }

    /**
     * Check whether shop is still available this floor.
     */
    public boolean isShopAvailable() {
        return !shopVisited && shopRoom != null;
    }

    /**
     * Mark the shared shop as visited for this floor.
     */
    public void markShopVisited() {
        this.shopVisited = true;
    }
    
    /**
     * Get next available room for a character.
     */
    public Room getNextRoom() {
        lock.readLock().lock();
        try {
            if (!currentFloorRooms.isEmpty()) {
                Room room = currentFloorRooms.get(0);
                return room;
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Character uses a room and moves to next floor if needed.
     */
    public synchronized void processRoom(GameCharacter character, Room room) {
        if (room != null) {
            room.enter(character);
        }
    }
    
    /**
     * Rest point between rooms - heal, restore mana, manage inventory.
     */
    public synchronized void restAtRestPoint(GameCharacter character, Scanner scanner) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println(character.getCharacterName() + " reached a rest point!");
        System.out.println("-".repeat(80));
        System.out.println("\nOptions:");
        System.out.println("1. Heal (costs 25 gold per 50 HP)");
        System.out.println("2. Restore Mana (costs 25 gold per 50 Mana)");
        System.out.println("3. View/Use Inventory");
        System.out.println("4. Continue Adventure");
        System.out.println("5. Rest (skip turn)");
        
        System.out.print("\nChoice: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        switch(choice) {
            case 1:
                if (character.getHealth() < character.getMaxHealth()) {
                    if (character.spendGold(25)) {
                        character.heal(50);
                        System.out.println(character.getCharacterName() + " healed!");
                    } else {
                        System.out.println("Not enough gold!");
                    }
                }
                break;
            case 2:
                if (character.getMana() < character.getMaxMana()) {
                    if (character.spendGold(25)) {
                        character.restoreMana(50);
                        System.out.println(character.getCharacterName() + "'s mana restored!");
                    } else {
                        System.out.println("Not enough gold!");
                    }
                }
                break;
            case 3:
                displayInventory(character);
                break;
            case 4:
                System.out.println(character.getCharacterName() + " continues the adventure!");
                break;
            case 5:
                System.out.println(character.getCharacterName() + " takes a short rest...");
                break;
        }
        System.out.println("-".repeat(80) + "\n");
    }
    
    /**
     * Display and manage character inventory.
     */
    private void displayInventory(GameCharacter character) {
        System.out.println("\n" + character.getCharacterName() + "'s Inventory:");
        System.out.println("  Equipment: " + character.getInventory().getAllEquipped().size());
        System.out.println("  Potions: " + character.getInventory().getAllPotions().size());
        System.out.println("  Gold: " + character.getGold());
        
        // Show potions available to use
        List<Potion> potions = character.getInventory().getAllPotions();
        if (!potions.isEmpty()) {
            System.out.println("\nAvailable Potions:");
            for (int i = 0; i < potions.size(); i++) {
                System.out.println((i + 1) + ". " + potions.get(i).toString());
            }
        }
    }
    
    /**
     * Handle boss encounter.
     */
    public void bossBattle(Boss boss, Scanner scanner) {
        System.out.println("\n" + "=".repeat(80));
        boss.entrance();
        System.out.println("Press Enter to begin the battle...");
        scanner.nextLine();
        
        // Battle simulation
        while (!boss.isDefeated() && gameActive) {
            boolean anyAlive = false;
            for (GameCharacter c : characters) {
                if (c.isCharacterAlive()) {
                    anyAlive = true;
                    break;
                }
            }
            
            if (!anyAlive) {
                System.out.println("\n*** ALL HEROES HAVE FALLEN ***");
                gameActive = false;
                break;
            }
            
            boss.battle(characters);
            
            if (!boss.isDefeated()) {
                System.out.print("Press Enter to continue...");
                scanner.nextLine();
            }
        }
    }
    
    /**
     * Advance to next floor.
     */
    public void nextFloor() {
        lock.writeLock().lock();
        try {
            currentFloor++;
            if (currentFloor <= maxFloors) {
                System.out.println("\n[ADVANCING TO FLOOR " + currentFloor + "]");
                generateFloorRooms();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // Getters
    public int getCurrentFloor() { return currentFloor; }
    public int getMaxFloors() { return maxFloors; }
    public boolean isGameActive() { return gameActive; }
    public void setGameActive(boolean active) { this.gameActive = active; }
    public GameCharacter[] getCharacters() { return characters; }
    
    private enum RoomType {
        CHEST, COMBAT, SHOP
    }
}
