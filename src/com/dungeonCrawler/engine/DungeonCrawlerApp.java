package com.dungeonCrawler.engine;

import com.dungeonCrawler.characters.Knight;
import com.dungeonCrawler.characters.Thief;
import com.dungeonCrawler.characters.Wizard;
import com.dungeonCrawler.characters.GameCharacter;
import com.dungeonCrawler.boss.BossFactory;
import com.dungeonCrawler.utils.GameLogger;
import java.util.Scanner;
import java.util.Random;

/**
 * Main application entry point for the Dungeon Crawler adventure game.
 * Manages the game loop, player interactions, and floor progression.
 */
public class DungeonCrawlerApp {
    private GameEngine engine;
    private GameCharacter[] characters;
    private Scanner scanner;
    private Random random;
    private int playerGold;  // Shared gold pool for all heroes
    
    public DungeonCrawlerApp() {
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.playerGold = 0;
    }
    
    /**
     * Main entry for starting the game from programmatic call.
     */
    public void play() {
        // Initialize
        initializeCharacters();
            this.playerGold = 300;  // Starting gold pool for all heroes
        
        // Show introduction
        showIntroduction();

        // Create engine and start adventure
        engine = new GameEngine(characters, 15);
        playAdventure();

        // Game end summary
        if (engine.isGameActive()) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("*** ADVENTURE COMPLETE ***");
            System.out.println("=".repeat(80) + "\n");
        }

        GameLogger.printGameStatistics(characters);
        GameLogger.rankCharactersByBattles(characters);
        GameLogger.rankCharactersByGold(characters);
        GameCharacter highest = GameLogger.getHighestLevelCharacter(characters);
        if (highest != null) {
            System.out.println("\n*** HIGHEST LEVEL: " + highest.getCharacterName() + " (Level " + highest.getLevel() + ") ***\n");
        }

        scanner.close();
    }
    private void showIntroduction() {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("WELCOME TO THE DESCENT: CHRONICLES OF THREE HEROES");
        System.out.println("Aethermoor has fallen under the shadow of Malachar, the Dark Lord.");
        System.out.println("Three heroes must venture deep into the dungeon to stop the spreading corruption.");
        System.out.println("You will choose rooms each turn — some hold treasure, others foes, and a rare shop may appear.");
        System.out.println("Survive 15 floors, defeat the champions of the dark, and face Malachar himself on the final floor.");
        System.out.println("" + "-".repeat(80));
        System.out.print("Press Enter to begin the adventure...");
        scanner.nextLine();
    }
    
    /**
     * Initialize characters.
     */
    private void initializeCharacters() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** CHARACTER CREATION ***");
        System.out.println("=".repeat(80) + "\n");
        
        Knight knight = new Knight("Theron");
        Thief thief = new Thief("Shadowblade");
        Wizard wizard = new Wizard("Arcanis");
        
        characters = new GameCharacter[] { knight, thief, wizard };
        
        System.out.println("Three legendary heroes have assembled:\n");
        for (GameCharacter character : characters) {
            System.out.println("- " + character.getCharacterName() + " the " + character.getCharacterClass());
            System.out.println("  Health: " + character.getMaxHealth() + " | Mana: " + character.getMaxMana() + "\n");
        }
        
    }
    
    /**
     * Main adventure loop.
     */
    private void playAdventure() {
        while (engine.isGameActive() && engine.getCurrentFloor() <= engine.getMaxFloors()) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FLOOR " + engine.getCurrentFloor() + "/" + engine.getMaxFloors());
            System.out.println("=".repeat(80));
            
            // Check for boss floor
            if (engine.getCurrentFloor() % 5 == 0 && engine.getCurrentFloor() < engine.getMaxFloors()) {
                // Floor boss battle
                playFloorBoss();
                if (!engine.isGameActive()) break;
            } else if (engine.getCurrentFloor() == engine.getMaxFloors()) {
                // Final boss battle
                playFinalBoss();
                break;
            }
            
            // Generate floor rooms
            engine.generateFloorRooms();
            
            // Play through floor (5 room choices + rest points)
            playFloor();
            
            if (engine.isGameActive()) {
                engine.nextFloor();
            }
        }
    }
    
    /**
     * Play through a single floor with room choices and rest points.
     */
    private void playFloor() {
        int roomChoices = 5;
        java.util.Set<GameCharacter> restingHeroes = new java.util.HashSet<>(); // Track which heroes chose to rest
        for (int round = 1; round <= roomChoices; round++) {
            if (!engine.isGameActive()) break;

            // Reset rest availability for each new room
            boolean restUsedThisRoom = false;
            
            System.out.println("\n[FLOOR " + engine.getCurrentFloor() + "] [ROOM " + round + " OF 5]");

            // Generate three room options with constraints ONCE per round
            java.util.List<com.dungeonCrawler.rooms.Room> options = generateRoomOptions(restingHeroes);

            // Inner loop: prompt until a consuming action (explore a room) occurs
            boolean roundComplete = false;
            while (!roundComplete) {
                // Display main menu; 'Explore' opens the generated room choices
                System.out.println("\nChoices:");
                System.out.println("1. Explore");
                System.out.println("2. Inventory and Equipment");
                System.out.println("3. Check status");
                if (!restUsedThisRoom) {
                    System.out.println("4. Rest and recover");
                }

                System.out.print("\nChoice: ");
                int mainChoice = -1;
                try {
                    mainChoice = scanner.nextInt();
                } catch (java.util.InputMismatchException e) {
                    // consume bad input
                }
                scanner.nextLine();

                if (mainChoice == 1) {
                    // Show the three generated room options and let the player pick one
                    System.out.println("\nSelect a room to explore:");
                    for (int i = 0; i < options.size(); i++) {
                        System.out.println((i + 1) + ". " + options.get(i).getRoomType() + " - " + options.get(i).getDescription());
                    }
                    System.out.print("\nRoom choice (1-3): ");
                    int roomChoice = -1;
                    try {
                        roomChoice = scanner.nextInt();
                    } catch (java.util.InputMismatchException e) {
                        // consume bad input
                    }
                    scanner.nextLine();

                    if (roomChoice >= 1 && roomChoice <= options.size()) {
                        com.dungeonCrawler.rooms.Room chosen = options.get(roomChoice - 1);

                        if (chosen instanceof com.dungeonCrawler.rooms.ShopRoom) {
                            engine.markShopVisited();
                            handleShopRoom((com.dungeonCrawler.rooms.ShopRoom) chosen);
                        } else if (chosen instanceof com.dungeonCrawler.rooms.ChestRoom) {
                            handleChestRoom((com.dungeonCrawler.rooms.ChestRoom) chosen);
                        } else if (chosen instanceof com.dungeonCrawler.rooms.CombatRoom) {
                            handleCombatRoom((com.dungeonCrawler.rooms.CombatRoom) chosen, restingHeroes, restUsedThisRoom);
                        }

                        // After resolving the chosen room, finish the round and clear rests
                        restingHeroes.clear();
                        roundComplete = true;
                    } else {
                        System.out.println("Invalid room selection.");
                    }

                } else if (mainChoice == 2) {
                    manageInventory();
                    // do not change roundComplete; options persist
                } else if (mainChoice == 3) {
                    displayCharacterStatus();
                    // do not change roundComplete; options persist
                } else if (mainChoice == 4 && !restUsedThisRoom) {
                    // Party Rest: heal party and refill mana. Mark alive heroes as resting.
                    for (GameCharacter hc : characters) {
                        if (hc.isCharacterAlive()) {
                            hc.heal(25); // heal 25 HP
                            hc.restoreMana(hc.getMaxMana()); // refill mana fully
                            restingHeroes.add(hc);
                        }
                    }
                    System.out.println("The party rests: healed 25 HP and refilled mana for all living heroes.");
                    restUsedThisRoom = true;
                    // rest does not complete round; options persist
                } else {
                    System.out.println("Invalid choice, please select again.");
                }
            }
        }
    }

    /**
     * Generate three room options for the player to choose from.
     * Rules:
     *  - For each round, chest and shop may be included at most once in the three options
     *  - Shop will only be included if the engine reports a shop is available this floor
     */
    private java.util.List<com.dungeonCrawler.rooms.Room> generateRoomOptions(java.util.Set<GameCharacter> restingHeroes) {
        java.util.List<com.dungeonCrawler.rooms.Room> options = new java.util.ArrayList<>();
        java.util.Random rnd = new java.util.Random();

        boolean chestIncluded = false;
        boolean shopIncluded = false;

        while (options.size() < 3) {
            int pick = rnd.nextInt(3); // 0=Chest,1=Combat,2=Shop

            if (pick == 2) { // Shop
                if (!shopIncluded && engine.isShopAvailable()) {
                    // Use the shared shopRoom from engine so it remembers visited state
                    com.dungeonCrawler.rooms.Room shop = engine.getShopRoom();
                    if (shop != null) {
                        options.add(shop);
                        shopIncluded = true;
                    } else {
                        // fallback to combat when shop is not created
                            options.add(new com.dungeonCrawler.rooms.CombatRoom(engine.getCurrentFloor(), restingHeroes));
                    }
                } else {
                    // shop not allowed, choose another
                    continue;
                }
            } else if (pick == 0) { // Chest
                if (!chestIncluded) {
                    options.add(new com.dungeonCrawler.rooms.ChestRoom());
                    chestIncluded = true;
                } else {
                    continue; // skip duplicate chest in same round
                }
            } else { // Combat
                    options.add(new com.dungeonCrawler.rooms.CombatRoom(engine.getCurrentFloor(), restingHeroes));
            }
        }

        return options;
    }

    /**
     * Handle chest room entry - display rewards and which hero received them.
     */
    private void handleChestRoom(com.dungeonCrawler.rooms.ChestRoom chestRoom) {
        System.out.println("\n*** TREASURE CHEST ***\n");
        
        // Pick a random hero to receive the treasure
        java.util.List<GameCharacter> aliveHeroes = new java.util.ArrayList<>();
        for (GameCharacter c : characters) {
            if (c.isCharacterAlive()) {
                aliveHeroes.add(c);
            }
        }
        
        if (aliveHeroes.isEmpty()) return;
        
        GameCharacter luckyHero = aliveHeroes.get(random.nextInt(aliveHeroes.size()));
        
        // Get initial state
        int initialGold = playerGold;
        int heroInitialGold = luckyHero.getGold();
        java.util.List<com.dungeonCrawler.items.Equipment> initialBag = 
            new java.util.ArrayList<>(luckyHero.getInventory().getBagEquipment());
        java.util.List<com.dungeonCrawler.items.Potion> initialPotions = 
            new java.util.ArrayList<>(luckyHero.getInventory().getAllPotions());
        
        // Enter the chest
        chestRoom.enter(luckyHero);
        
        // Determine and display what was received
        int heroNewGold = luckyHero.getGold();
        int goldGained = heroNewGold - heroInitialGold;
        if (goldGained > 0) {
            // Add to party pool
            playerGold += goldGained;
            System.out.println("💰 " + luckyHero.getCharacterName() + " found " + goldGained + " gold!");
        }
        
        // Check for new equipment
        java.util.List<com.dungeonCrawler.items.Equipment> newBag = luckyHero.getInventory().getBagEquipment();
        for (com.dungeonCrawler.items.Equipment eq : newBag) {
            if (!initialBag.contains(eq)) {
                System.out.println("⚔️  " + luckyHero.getCharacterName() + " found: " + eq.toString());
            }
        }
        
        // Check for new potions
        java.util.List<com.dungeonCrawler.items.Potion> newPotions = luckyHero.getInventory().getAllPotions();
        if (newPotions.size() > initialPotions.size()) {
            for (com.dungeonCrawler.items.Potion p : newPotions) {
                if (!initialPotions.contains(p)) {
                    System.out.println("🧪 " + luckyHero.getCharacterName() + " found: " + p.toString());
                }
            }
        }
        
        System.out.println();
    }

    /**
     * Handle shop room entry - display 5 items for purchase.
     */
    private void handleShopRoom(com.dungeonCrawler.rooms.ShopRoom shopRoom) {
        System.out.println("\n*** MYSTERIOUS SHOP ***\n");
        
        boolean shopping = true;
        while (shopping) {
            // Generate 5 items to display
            java.util.List<com.dungeonCrawler.items.Equipment> shopItems = new java.util.ArrayList<>();
            java.util.List<Integer> shopPrices = new java.util.ArrayList<>();
            
            for (int i = 0; i < 5; i++) {
                com.dungeonCrawler.items.EquipmentType[] types = com.dungeonCrawler.items.EquipmentType.values();
                com.dungeonCrawler.items.EquipmentType type = types[random.nextInt(types.length)];
                int heroLevel = characters[random.nextInt(characters.length)].getLevel();
                com.dungeonCrawler.items.Equipment item = new com.dungeonCrawler.items.Equipment(type, heroLevel + 1);
                shopItems.add(item);
                shopPrices.add(type.getValue() * 20);
            }
            
            // Display shop inventory with current gold
            System.out.println("Party Gold: " + playerGold + " gold\n");
            System.out.println("Available Items:\n");
            for (int i = 0; i < shopItems.size(); i++) {
                System.out.println((i + 1) + ". " + shopItems.get(i).toString() + " - " + shopPrices.get(i) + " gold");
            }
            System.out.println("6. Leave the shop");
            
            System.out.print("\nSelect item (1-6): ");
            int itemChoice = -1;
            try {
                itemChoice = scanner.nextInt();
            } catch (java.util.InputMismatchException e) {
                // consume bad input
            }
            scanner.nextLine();
            
            if (itemChoice >= 1 && itemChoice <= 5) {
                // Purchase item
                com.dungeonCrawler.items.Equipment selectedItem = shopItems.get(itemChoice - 1);
                int price = shopPrices.get(itemChoice - 1);
                
                if (playerGold >= price) {
                    playerGold -= price;
                    // Add item to the first alive hero
                    for (GameCharacter c : characters) {
                        if (c.isCharacterAlive()) {
                            c.getInventory().addEquipment(selectedItem);
                            System.out.println("\n✓ " + selectedItem.getType() + " purchased for " + price + " gold!");
                            System.out.println("Remaining gold: " + playerGold + "\n");
                            break;
                        }
                    }
                } else {
                    System.out.println("\nNot enough gold! This item costs " + price + " gold. Party has " + playerGold + ".\n");
                }
            } else if (itemChoice == 6) {
                shopping = false;
                System.out.println("\nYou leave the shop.\n");
            } else {
                System.out.println("Invalid choice.\n");
            }
        }
    }

    /**
     * Handle combat room entry - show combat sequence.
     */
    private void handleCombatRoom(com.dungeonCrawler.rooms.CombatRoom combatRoom, java.util.Set<GameCharacter> restingHeroes, boolean restUsedThisRoom) {
        System.out.println("\n*** COMBAT ENGAGED ***\n");

        // Only skip combat if Rest was selected for this room and there are resting heroes
        if (restUsedThisRoom && restingHeroes != null && !restingHeroes.isEmpty()) {
            System.out.println("Combat system skipped due to rest — enemy lashes out at the whole party!");
            int damage = combatRoom.applyEqualDamageToAll(characters);
            System.out.println("Each living hero took " + damage + " damage.");
            // Display per-hero health after the strike
            for (GameCharacter c : characters) {
                System.out.println(c.getCharacterName() + ": " + c.getHealth() + "/" + c.getMaxHealth() + " HP" + (c.isCharacterAlive() ? "" : " (DEFEATED)"));
            }
            return;
        }
        
        // Normal combat: all alive characters enter the combat room together
        for (GameCharacter c : characters) {
            if (c.isCharacterAlive() && !restingHeroes.contains(c)) {
                combatRoom.enter(c);
            }
        }
        
        // Display the complete combat log (all heroes share the same log via broadcastToAll)
        if (characters.length > 0) {
            GameCharacter firstHero = characters[0];
            
            if (!firstHero.getBattleLog().isEmpty()) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("*** COMBAT LOG ***");
                System.out.println("=".repeat(80));
                for (String logEntry : firstHero.getBattleLog()) {
                    System.out.println(logEntry);
                }
                System.out.println("=".repeat(80) + "\n");
            }
            // Add gold earned in this combat to the party pool
            int goldFromCombat = combatRoom.getLastGoldEarned();
            if (goldFromCombat > 0) {
                playerGold += goldFromCombat;
            }
            
            // Clear all hero logs after displaying
            for (GameCharacter c : characters) {
                c.getBattleLog().clear();
            }
        }
    }

    

        /**
     * Manage inventory and equipment for heroes with 21-slot system.
     * Commands: i#-h# (equip to hero), i#-u (use potion), i#-d (drop)
     */
    private void manageInventory() {
        // Collect all items into a single 21-slot inventory
        java.util.List<Object> inventorySlots = new java.util.ArrayList<>();
        // Track which hero originally owned each slot entry (null = none)
        java.util.List<GameCharacter> slotOwners = new java.util.ArrayList<>();
        for (GameCharacter hero : characters) {
            for (com.dungeonCrawler.items.Equipment eq : hero.getInventory().getBagEquipment()) {
                inventorySlots.add(eq);
                slotOwners.add(hero);
            }
            for (com.dungeonCrawler.items.Potion potion : hero.getInventory().getAllPotions()) {
                inventorySlots.add(potion);
                slotOwners.add(hero);
            }
        }
        
        // Fill remaining slots to 21 with nulls (empty)
        while (inventorySlots.size() < 21) {
            inventorySlots.add(null);
            slotOwners.add(null);
        }
        
        boolean managing = true;
        while (managing) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("*** INVENTORY MANAGEMENT (21 SLOTS) ***");
            System.out.println("=".repeat(80));
            
            // Display hero status with equipped items
            System.out.println("\n[HEROES & EQUIPPED ITEMS]");
            System.out.println("Party Gold Pool: " + playerGold + " gold\n");
            for (int h = 0; h < characters.length; h++) {
                GameCharacter hero = characters[h];
                System.out.println("\nH" + (h + 1) + " - " + hero.getCharacterName() + " (Level " + hero.getLevel() + ")");
                java.util.Map<com.dungeonCrawler.items.EquipmentType, com.dungeonCrawler.items.Equipment> equipped = 
                    hero.getInventory().getAllEquipped();
                if (equipped.isEmpty()) {
                    System.out.println("     [No equipment equipped]");
                } else {
                    for (com.dungeonCrawler.items.Equipment eq : equipped.values()) {
                        System.out.println("     ⚔️  " + eq.getType());
                    }
                }
            }
            
            // Display inventory slots
            System.out.println("\n[INVENTORY SLOTS (0-20)]");
            for (int i = 0; i < inventorySlots.size(); i++) {
                System.out.print("I" + i + ": ");
                Object item = inventorySlots.get(i);
                if (item == null) {
                    System.out.println("[Empty]");
                } else if (item instanceof com.dungeonCrawler.items.Equipment) {
                    com.dungeonCrawler.items.Equipment eq = (com.dungeonCrawler.items.Equipment) item;
                    System.out.println("[EQ] " + eq.getType() + " (Level " + eq.getLevel() + ")");
                } else if (item instanceof com.dungeonCrawler.items.Potion) {
                    com.dungeonCrawler.items.Potion p = (com.dungeonCrawler.items.Potion) item;
                    System.out.println("[POTION] " + p.getType().getName() + " x" + p.getQuantity());
                }
            }
            
            // Command input
            System.out.println("\n[COMMANDS]");
            System.out.println("  i#-h# : Equip/replace item at slot # to hero # OR use potion on hero (e.g., i3-h1)");
            System.out.println("  i#-d  : Drop item at slot # (e.g., i7-d)");
            System.out.println("  exit  : Exit inventory\n");
            
            System.out.print("Command: ");
            String command = scanner.nextLine().trim();
            
            if (command.equalsIgnoreCase("exit")) {
                managing = false;
            } else if (command.contains("-")) {
                String[] parts = command.split("-");
                if (parts.length != 2) {
                    System.out.println("Invalid command format.\n");
                    continue;
                }
                
                String slotPart = parts[0].trim();
                String actionPart = parts[1].trim();
                
                if (!slotPart.startsWith("i")) {
                    System.out.println("Invalid slot format. Use i# (e.g., i3).\n");
                    continue;
                }
                
                int slotNum = -1;
                try {
                    slotNum = Integer.parseInt(slotPart.substring(1));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid slot number.\n");
                    continue;
                }
                
                if (slotNum < 0 || slotNum >= inventorySlots.size()) {
                    System.out.println("Slot out of range (0-20).\n");
                    continue;
                }
                
                Object slotItem = inventorySlots.get(slotNum);
                GameCharacter slotOwner = slotOwners.get(slotNum);
                
                if (actionPart.equalsIgnoreCase("d")) {
                    // Drop action
                    if (slotItem == null) {
                        System.out.println("That slot is empty.\n");
                    } else {
                        // remove from owner's inventory if present
                        if (slotOwner != null && slotItem instanceof com.dungeonCrawler.items.Equipment) {
                            slotOwner.getInventory().removeEquipmentInstance((com.dungeonCrawler.items.Equipment)slotItem);
                        }
                        if (slotOwner != null && slotItem instanceof com.dungeonCrawler.items.Potion) {
                            // attempt to remove one potion from owner's inventory
                            com.dungeonCrawler.items.Potion p = (com.dungeonCrawler.items.Potion) slotItem;
                            slotOwner.getInventory().usePotion(p.getType());
                        }
                        inventorySlots.set(slotNum, null);
                        slotOwners.set(slotNum, null);
                        System.out.println("✓ Item dropped from slot " + slotNum + ".\n");
                    }
                } else if (actionPart.startsWith("h")) {
                    // Equip or use on hero
                    int heroNum = -1;
                    try {
                        heroNum = Integer.parseInt(actionPart.substring(1));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid hero number.\n");
                        continue;
                    }
                    
                    if (heroNum < 1 || heroNum > characters.length) {
                        System.out.println("Hero out of range (1-" + characters.length + ").\n");
                        continue;
                    }
                    
                    GameCharacter targetHero = characters[heroNum - 1];
                    if (!targetHero.isCharacterAlive()) {
                        System.out.println(targetHero.getCharacterName() + " is defeated.\n");
                        continue;
                    }
                    
                    if (slotItem == null) {
                        System.out.println("That slot is empty.\n");
                    } else if (slotItem instanceof com.dungeonCrawler.items.Equipment) {
                        // Equip equipment
                        com.dungeonCrawler.items.Equipment eq = (com.dungeonCrawler.items.Equipment) slotItem;

                        // If hero already has equipment of this type, check replace rules
                        com.dungeonCrawler.items.Equipment oldEq = targetHero.getInventory().getEquipped(eq.getType());
                        if (oldEq != null) {
                            // Only allow replacement if new equipment has higher level
                            if (eq.getLevel() <= oldEq.getLevel()) {
                                System.out.println("Cannot replace: new " + eq.getType() + " is not higher level than equipped one.\n");
                                continue;
                            }

                            // Remove new eq from its owner inventory (if any)
                            if (slotOwner != null) {
                                slotOwner.getInventory().removeEquipmentInstance(eq);
                            }

                            // Move new eq into hero's bag then equip it (equipEquipment returns the previous equipped item)
                            targetHero.getInventory().addEquipment(eq);
                            com.dungeonCrawler.items.Equipment returned = targetHero.getInventory().equipEquipment(eq);

                            // Place returned (previously equipped) item into this slot
                            if (returned != null) {
                                inventorySlots.set(slotNum, returned);
                                slotOwners.set(slotNum, targetHero);
                            } else {
                                inventorySlots.set(slotNum, null);
                                slotOwners.set(slotNum, null);
                            }

                            System.out.println("✓ " + targetHero.getCharacterName() + " equipped " + eq.getType() + " (replaced old one).\n");
                        } else {
                            // No old equipment: remove new eq from its owner inventory and equip
                            if (slotOwner != null) {
                                slotOwner.getInventory().removeEquipmentInstance(eq);
                            }
                            targetHero.getInventory().addEquipment(eq);
                            com.dungeonCrawler.items.Equipment prev = targetHero.getInventory().equipEquipment(eq);
                            // Clear the inventory slot
                            inventorySlots.set(slotNum, null);
                            slotOwners.set(slotNum, null);
                            System.out.println("✓ " + targetHero.getCharacterName() + " equipped " + eq.getType() + ".\n");
                        }
                    } else if (slotItem instanceof com.dungeonCrawler.items.Potion) {
                        // Use potion on the selected hero
                        com.dungeonCrawler.items.Potion p = (com.dungeonCrawler.items.Potion) slotItem;
                        int restore = p.getType().getRestoreAmount();
                        switch (p.getType()) {
                            case HEALTH:
                            case VIGOR:
                                targetHero.heal(restore);
                                break;
                            case MANA:
                                targetHero.restoreMana(restore);
                                break;
                        }
                        System.out.println("✓ " + targetHero.getCharacterName() + " used " + p.getType().getName() + ".\n");

                        // Remove one potion from owner's inventory if present
                        if (slotOwner != null) {
                            com.dungeonCrawler.items.Potion updated = slotOwner.getInventory().usePotion(p.getType());
                            if (updated != null) {
                                // still has quantity left
                                inventorySlots.set(slotNum, updated);
                                slotOwners.set(slotNum, slotOwner);
                            } else {
                                inventorySlots.set(slotNum, null);
                                slotOwners.set(slotNum, null);
                            }
                        } else {
                            // No owner recorded: decrement local quantity
                            if (p.getQuantity() > 1) {
                                p.setQuantity(p.getQuantity() - 1);
                                inventorySlots.set(slotNum, p);
                            } else {
                                inventorySlots.set(slotNum, null);
                                slotOwners.set(slotNum, null);
                            }
                        }
                    }
                } else {
                    System.out.println("Unknown action. Use 'h' for equip/use or 'd' for drop.\n");
                }
            } else {
                System.out.println("Invalid command. Use format: i#-h# or i#-d\n");
            }
        }
    }
    
    /**
     * Process room exploration for all characters.
     */
    private void exploreRoom() {
        System.out.println("\nThe party explores...\n");
        
        for (GameCharacter character : characters) {
            if (character.isCharacterAlive()) {
                character.act(null);
                
                // Random room type
                int roomType = random.nextInt(3);
                switch(roomType) {
                    case 0:
                        // Chest room
                        new com.dungeonCrawler.rooms.ChestRoom().enter(character);
                        break;
                    case 1:
                        // Combat room
                        new com.dungeonCrawler.rooms.CombatRoom(engine.getCurrentFloor()).enter(character);
                        break;
                    case 2:
                        // Shop room
                        new com.dungeonCrawler.rooms.ShopRoom().enter(character);
                        break;
                }
                
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Allow characters to use abilities.
     */
    private void useAbilities() {
        System.out.println("\nCharacter Abilities:\n");
        for (int i = 0; i < characters.length; i++) {
            System.out.println((i + 1) + ". " + characters[i].getCharacterName() + " (" + characters[i].getCharacterClass() + ")");
        }
        System.out.println("0. Cancel");
        
        System.out.print("\nSelect character: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice > 0 && choice <= characters.length) {
            characters[choice - 1].useAbility();
        }
    }
    
    /**
     * Display character status.
     */
    private void displayCharacterStatus() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("*** CHARACTER STATUS ***");
        System.out.println("=".repeat(80) + "\n");
        // Show party gold pool
        System.out.println("Party Gold: " + playerGold + " gold\n");
        for (GameCharacter c : characters) {
            System.out.println(c.getCharacterName() + " (" + c.getCharacterClass() + ")");
            System.out.println("  Status: " + (c.isCharacterAlive() ? "ALIVE" : "DEFEATED"));
            System.out.println("  Level: " + c.getLevel() + " | Experience: " + c.getExperience());
            System.out.println("  Health: " + c.getHealth() + "/" + c.getMaxHealth());
            System.out.println("  Mana: " + c.getMana() + "/" + c.getMaxMana());
            System.out.println("  Battles Won: " + c.getBattlesWon() + " | Items Collected: " + c.getItemsCollected());
            System.out.println();
        }
        System.out.println("=".repeat(80) + "\n");
    }
    
    /**
     * Floor boss battle (every 5 floors).
     */
    private void playFloorBoss() {
        engine.bossBattle(BossFactory.createFloorBoss(engine.getCurrentFloor()), scanner);
    }
    
    /**
     * Final boss battle (floor 15).
     */
    private void playFinalBoss() {
        engine.bossBattle(BossFactory.createFinalBoss(), scanner);
    }
    
    /**
     * Main entry point.
     */
    public static void main(String[] args) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║               WELCOME TO THE DUNGEON CRAWLER ADVENTURE GAME!                  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("=".repeat(80) + "\n");
        
        DungeonCrawlerApp game = new DungeonCrawlerApp();
        game.play();
        
        System.out.println("\nThank you for playing! The adventure concludes...\n");
    }
}
