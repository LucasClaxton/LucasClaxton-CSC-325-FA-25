# Dungeon Crawler Adventure Game - CSC325-FA25 Final Assignment

## Student Name
Lucas Claxton

## Course
CSC325-FA25 - Advanced Object-Oriented Programming

## Game Title & Story Summary

### "The Descent: Chronicles of Three Heroes"

In the dark realm of Aethermoor, a malevolent Dark Lord named **Malachar** has emerged from the depths of an ancient dungeon. For a thousand years, he has gathered power and spread corruption throughout the land. Three legendary heroes — **Theron the Knight**, **Shadowblade the Thief**, and **Arcanis the Wizard** — have united to descend into the dreaded dungeon, facing increasingly powerful foes, collecting treasures, and ultimately confronting Malachar himself to save the realm.

The game features:
- **15 Floors of Progression**: Each floor contains 5 room choices plus special boss encounters
- **Three Playable Characters**: Each with unique abilities, strengths, and story elements
- **Dynamic Dungeons**: Randomly generated rooms including combat encounters, treasure chambers, and shops
- **Boss Battles**: Floor bosses at levels 5 and 10, leading to the epic final showdown with Malachar
- **Shared Inventory System**: A unified equipment and potion management system accessible by all characters
- **Strategic Rest System**: Party healing and mana restoration; skips combat if a combat room is encountered
- **Real-time Multithreading**: Characters operate concurrently, simulating parallel adventures
- **Enhanced UI**: Real-time hero health/mana display in combat and boss battles

## Quick Start

See **INSTRUCTIONS.md** for detailed gameplay instructions and controls.

## Project Structure

\\\
src/com/dungeonCrawler/
 characters/          # Character classes (Knight, Thief, Wizard, GameCharacter)
 items/              # Equipment, potions, and inventory management
 rooms/              # Room implementations (Chest, Combat, Shop)
 boss/               # Boss encounters and boss factory
 engine/             # Game engine and main application
 utils/              # Logging and statistics utilities
\\\

## Key Features Implemented

### 1. Advanced OOP Concepts
- **Abstraction**: \GameCharacter\ abstract class defines core functionality
- **Inheritance**: Knight, Thief, Wizard extend GameCharacter with unique implementations
- **Polymorphism**: Room interface implemented by different room types
- **Encapsulation**: Private fields with synchronized getters/setters

### 2. Multithreading & Concurrency
- Each character runs in a separate thread
- Synchronized access to shared resources (inventory, character stats)
- ReentrantReadWriteLock for thread-safe floor management
- Proper thread joining for graceful shutdown

### 3. Collections & Arrays
- Character arrays for managing multiple heroes
- ArrayList for room lists, potion inventory, and battle logs
- HashMap for equipment management

### 4. Lambdas & Stream Operations
- Character statistics aggregation using streams
- Ranking and sorting with lambda comparators
- Event log filtering and formatting
- Total calculations using mapToInt and sum operations

## Thread Synchronization Approach

### Shared Resources
1. **Character Health/Mana**: Synchronized methods prevent race conditions
2. **Inventory System**: Synchronized methods in Inventory class
3. **Room State**: Synchronized blocks prevent simultaneous room access
4. **Boss Battles**: Thread-safe boss state management

### Synchronization Mechanisms Used
- **Synchronized Methods**: heal(), takeDamage(), addGold() etc.
- **Synchronized Blocks**: Room entry and item handling
- **ReentrantReadWriteLock**: Floor room list access
- **Thread.join()**: Main thread waits for all character threads

### Thread Safety Guarantees
- Only one thread can modify a room's state at a time
- Inventory changes are atomic and consistent
- Character stats cannot be corrupted by concurrent access
- Boss battles prevent race conditions in damage calculation

## Documentation Files

- **README.md** (this file) - Game overview and project structure
- **INSTRUCTIONS.md** - Detailed gameplay guide and controls
- **CONCURRENCY_REPORT.md** - In-depth concurrency analysis
- **UML_DIAGRAM.md** - Class relationships and architecture diagram

## Compilation & Execution

### Compile (Windows PowerShell)
Use PowerShell to expand Java source file paths before invoking `javac`:
```powershell
cd c:\Users\lclax\lucas_claxton_csc_325\LucasClaxton-CSC-325-FA-25
$files = Get-ChildItem -Recurse -Path 'src' -Filter '*.java' | Select-Object -ExpandProperty FullName
javac -d bin $files
```

### Run (Windows PowerShell)
```powershell
cd bin
java com.dungeonCrawler.engine.DungeonCrawlerApp
```

## Class Overview

### Characters
- **Knight**: High HP, Strong Defense, Heavy Hitter
- **Thief**: High Agility, Critical Strikes, Evasion Focus
- **Wizard**: High Mana, Spellcasting, Area Damage

### Rooms
- **ChestRoom**: Random treasure, equipment, or potions
- **CombatRoom**: Battle encounters with scaling difficulty
- **ShopRoom**: Healing and equipment purchasing (once per floor)

### Boss Encounters
- **Floor Bosses** (Level 5, 10): "Garth the Stone Guardian", "Zephyr the Storm Elemental"
- **Final Boss** (Level 15): "Malachar - The Dark Lord"

## Game Statistics & Achievements

The game tracks:
- Battles won by each character
- Items collected per hero
- Gold accumulated
- Character levels and experience
- Health/Mana management
- Boss defeats

Final statistics include rankings by battles won and gold collected.
I would have made the battles won be whoever landed the last blow per monster/boss, but I ran out of time.

## AI Reflection

I did use AI for whenever I got stuck, like the rest mechanic went through multiple variations and the final version is what was suggested and idk why I didn't think about it. The other thing I used the AI for was the general lore and lore on items plus the speeches. I was having a lot of difficulty comming up with something and the AI gave me something.
