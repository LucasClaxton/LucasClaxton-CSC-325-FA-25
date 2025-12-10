# UML Class Diagram - Dungeon Crawler Architecture

## Full Class Hierarchy

```
┌─────────────────────────────────────────────────────────────────────┐
│                      GAME ENGINE PACKAGE                             │
├─────────────────────────────────────────────────────────────────────┤
│                         GameEngine                                   │
├─────────────────────────────────────────────────────────────────────┤
│ - characters: GameCharacter[]                                        │
│ - currentFloor: int                                                  │
│ - maxFloors: int                                                     │
│ - currentFloorRooms: List<Room>                                      │
│ - shopRoom: Room                                                     │
│ - lock: ReentrantReadWriteLock                                       │
├─────────────────────────────────────────────────────────────────────┤
│ + startAdventure(): void                                             │
│ + generateFloorRooms(): void                                         │
│ + getNextRoom(): Room                                                │
│ + processRoom(GameCharacter, Room): void                             │
│ + restAtRestPoint(GameCharacter, Scanner): void                      │
│ + bossBattle(Boss, Scanner): void                                    │
│ + nextFloor(): void                                                  │
└─────────────────────────────────────────────────────────────────────┘
         ▲
         │ uses
         │
    ┌────┴────────────────────────────────────┐
    │                                           │
    ▼                                           ▼
┌─────────────────────────────────┐    ┌──────────────────────────────┐
│    <<abstract>>                 │    │  DungeonCrawlerApp (main)    │
│    GameCharacter extends Thread │    ├──────────────────────────────┤
├─────────────────────────────────┤    │ - engine: GameEngine         │
│ # name: String                  │    │ - characters: GameCharacter[]│
│ # currentHealth: int            │    │ - scanner: Scanner           │
│ # maxHealth: int                │    ├──────────────────────────────┤
│ # mana: int                     │    │ + play(): void               │
│ # maxMana: int                  │    │ + playAdventure(): void      │
│ # level: int                    │    │ + playFloor(): void          │
│ # experience: int               │    │ + exploreRoom(): void        │
│ # gold: int                     │    │ + main(String[]): void       │
│ # inventory: Inventory          │    └──────────────────────────────┘
│ # battleLog: List<String>       │
│ # battlesWon: int               │
│ # itemsCollected: int           │
│ # isAlive: boolean              │
├─────────────────────────────────┤
│ + abstract act(Room): void      │
│ + abstract useAbility(): void   │
│ + abstract getCharacterClass(): │
│ + heal(int): void ✓             │
│ + takeDamage(int): void ✓       │
│ + restoreMana(int): void ✓      │
│ + addGold(int): void ✓          │
│ + spendGold(int): boolean ✓     │
│ + addExperience(int): void ✓    │
│ + addLog(String): void ✓        │
│ + winBattle(): void ✓           │
│ + collectItem(): void ✓         │
│ + run(): void                   │
└────────┬────────────────────────┘
         │
         │ extends
         ├──────┬──────┬──────────┐
         │      │      │          │
         ▼      ▼      ▼          ▼
    ┌────────┐┌──────┐┌───────┐┌──────────┐
    │ Knight ││Thief ││Wizard ││(Others)  │
    ├────────┤├──────┤├───────┤└──────────┘
    │ - armor        │ │      │ │- spellPower
    │        │      │ │      │ │
    │ + heavy│      │- crit- │ │
    │  Attack│      │  Chance│ │
    │ + defen│      │- agility  │
    │  sive  │      │        │ │
    │  Stance│      │        │ │
    │        │      │        │ │
    └────────┘└──────┘└───────┘
```

## Room Interface Hierarchy

```
┌──────────────────────────────────────────────┐
│         <<interface>>                         │
│         Room                                  │
├──────────────────────────────────────────────┤
│ + enter(GameCharacter): void                 │
│ + getRoomType(): String                      │
│ + getDescription(): String                   │
└────────────────┬─────────────────────────────┘
                 │ implements
    ┌────────────┼────────────┐
    │            │            │
    ▼            ▼            ▼
┌─────────┐ ┌──────────┐ ┌─────────┐
│ChestRoom│ │CombatRoom│ │ShopRoom │
├─────────┤ ├──────────┤ ├─────────┤
│-opened: │ │-enemyName│ │-visited:│
│ boolean │ │-enemy    │ │ boolean │
│         │ │ Health   │ │-shopkeep│
│+ enter()│ │          │ │ er      │
│+ getRmT │ │+ enter() │ │         │
│+ getDesc│ │+ getRmT()│ │+ enter()│
└─────────┘ └──────────┘ │+ getRmT │
                         │+ getDesc│
                         └─────────┘
```

## Item System

```
┌──────────────────────────┐
│   EquipmentType (enum)   │
├──────────────────────────┤
│ SWORD, DAGGER, STAFF,    │
│ HELMET, CHESTPLATE,      │
│ SHIELD, GLOVES, BOOTS    │
└──────────────────────────┘

┌──────────────────────────────────┐
│        Equipment                 │
├──────────────────────────────────┤
│ - type: EquipmentType            │
│ - level: int                     │
│ - lore: String                   │
├──────────────────────────────────┤
│ + getType(): EquipmentType       │
│ + getLevel(): int                │
│ + getLore(): String              │
│ + toString(): String             │
└──────────────────────────────────┘

┌──────────────────────────────────┐
│        Potion                    │
├──────────────────────────────────┤
│ - type: PotionType (enum)        │
│ - quantity: int                  │
├──────────────────────────────────┤
│ + getType(): PotionType          │
│ + getQuantity(): int             │
│ + use(): void ✓                  │
│ + toString(): String             │
└──────────────────────────────────┘

┌──────────────────────────────────────┐
│        Inventory ✓                   │
├──────────────────────────────────────┤
│ - equipment: Map<EquipmentType,      │
│    Equipment>                        │
│ - potions: List<Potion>              │
│ - capacity: int                      │
│ - currentSize: int                   │
├──────────────────────────────────────┤
│ + addEquipment(Equip): boolean ✓     │
│ + addPotion(Potion): boolean ✓       │
│ + removeEquipment(Type): Equipment ✓ │
│ + usePotion(Type): Potion ✓          │
│ + getEquipment(Type): Equipment ✓    │
│ + getAllEquipment(): Map ✓           │
│ + getAllPotions(): List ✓            │
│ + hasSpace(): boolean ✓              │
│ + clear(): void ✓                    │
└──────────────────────────────────────┘
```

## Boss System

```
┌────────────────────────────────────┐
│          Boss                      │
├────────────────────────────────────┤
│ - name: String                     │
│ - title: String                    │
│ - health: int                      │
│ - maxHealth: int                   │
│ - attackPower: int                 │
│ - story: String                    │
│ - defeated: boolean                │
│ - floorNumber: int                 │
├────────────────────────────────────┤
│ + entrance(): void                 │
│ + battle(GameCharacter[]): void    │
│ + getHealth(): int                 │
│ + isDefeated(): boolean            │
└────────────────────────────────────┘
         ▲
         │ created by
         │
┌────────────────────────────────────┐
│      BossFactory                   │
├────────────────────────────────────┤
│ + createFloorBoss(int): Boss       │
│ + createFinalBoss(): Boss          │
└────────────────────────────────────┘
```

## Utility Package

```
┌────────────────────────────────────────┐
│      GameLogger                        │
├────────────────────────────────────────┤
│ + printGameStatistics(...): void       │
│ + printEventLogs(GameCharacter[]): ... │
│ + rankCharactersByBattles(...): void   │
│ + rankCharactersByGold(...): void      │
│ + getHighestLevelCharacter(...): ...   │
└────────────────────────────────────────┘
  (Uses Lambda expressions and Streams)
```

## Thread Relationships

```
┌─────────────────────────────────────────────────────────────────┐
│ Main Thread (DungeonCrawlerApp.main)                            │
├─────────────────────────────────────────────────────────────────┤
│ 1. Creates GameCharacter[] array                                │
│ 2. Creates GameEngine(characters)                               │
│ 3. Starts each character thread                                 │
│    - Character.start() calls run()                              │
│ 4. Calls join() on all character threads                        │
│ 5. Waits for all threads to complete (synchronization point)   │
│ 6. Prints statistics                                            │
└─────────────────────────────────────────────────────────────────┘
        │ forks
        ├──────────┬──────────┬──────────────┐
        │          │          │              │
        ▼          ▼          ▼              ▼
    Knight Thread Thief Thread Wizard Thread ...
    (run())       (run())       (run())
        │          │          │              │
        └──────────┼──────────┼──────────────┘
                   │ joins
                   ▼
         Main Thread Resumes
```

## Synchronization Points

```
┌────────────────────────────────────────┐
│  Synchronized Methods (✓)              │
├────────────────────────────────────────┤
│ GameCharacter:                         │
│  ✓ heal()                              │
│  ✓ takeDamage()                        │
│  ✓ restoreMana()                       │
│  ✓ addGold()                           │
│  ✓ spendGold()                         │
│  ✓ addExperience()                     │
│  ✓ addLog()                            │
│  ✓ winBattle()                         │
│  ✓ collectItem()                       │
│                                        │
│ Inventory:                             │
│  ✓ addEquipment()                      │
│  ✓ addPotion()                         │
│  ✓ removeEquipment()                   │
│  ✓ usePotion()                         │
│  ✓ getEquipment()                      │
│  ✓ getAllEquipment()                   │
│  ✓ getAllPotions()                     │
│  ✓ hasSpace()                          │
│  ✓ clear()                             │
│                                        │
│ Room Classes:                          │
│  ✓ synchronized(this) blocks           │
│                                        │
│ GameEngine:                            │
│  • ReentrantReadWriteLock for rooms    │
│  ✓ synchronized methods for state      │
└────────────────────────────────────────┘
```

## Data Flow Diagram

```
                    User Input
                        │
                        ▼
                ┌─────────────────┐
                │ DungeonCrawlerApp
                │   Game Loop     │
                └────────┬────────┘
                         │
        ┌────────────────┼────────────────┐
        │                │                │
        ▼                ▼                ▼
    Knight          Thief            Wizard
    Thread          Thread           Thread
        │                │                │
        │                ▼                │
        │            ┌─────────┐          │
        ├──────────→ │ Rooms   │ ←────────┤
        │            └────┬────┘          │
        │                 │               │
        ├────────────────→├──────────────┤
        │                 │              │
        │            ┌────▼─────┐       │
        └──────────→ │Inventory │ ◄────┘
                     │(Shared)  │
                     └──────────┘
```

## Key Design Patterns

### 1. Abstract Factory Pattern
- `BossFactory`: Creates boss instances
- Decouples boss creation from usage

### 2. Strategy Pattern
- `Room` interface: Different room types with shared interface
- Different exploration strategies per room

### 3. Template Method Pattern
- `GameCharacter.run()`: Common structure, unique implementations
- Knight, Thief, Wizard override specific methods

### 4. Observer Pattern
- Character logging system observes game events
- Battle logs capture all significant occurrences

### 5. Thread Pool Simulation
- GameEngine manages character threads
- Fixed pool of 3 worker threads (characters)

## Dependency Graph

```
DungeonCrawlerApp
    ├── GameEngine
    │   ├── GameCharacter[]
    │   │   ├── Knight
    │   │   ├── Thief
    │   │   ├── Wizard
    │   │   └── Inventory (shared)
    │   │       ├── Equipment
    │   │       └── Potion
    │   ├── Room (interface)
    │   │   ├── ChestRoom
    │   │   ├── CombatRoom
    │   │   └── ShopRoom
    │   └── Boss
    │       └── BossFactory
    │
    ├── GameLogger (utilities)
    └── Scanner (user input)
```

## Package Organization

```
com.dungeonCrawler
├── characters/
│   ├── GameCharacter (abstract base)
│   ├── Knight
│   ├── Thief
│   └── Wizard
│
├── items/
│   ├── Equipment
│   ├── EquipmentType (enum)
│   ├── Inventory (thread-safe)
│   ├── Potion
│   └── Potion.PotionType (enum)
│
├── rooms/
│   ├── Room (interface)
│   ├── ChestRoom
│   ├── CombatRoom
│   └── ShopRoom
│
├── boss/
│   ├── Boss
│   └── BossFactory
│
├── engine/
│   ├── GameEngine
│   └── DungeonCrawlerApp (main)
│
└── utils/
    └── GameLogger
```

## Legend

- `+` : Public access
- `-` : Private access
- `#` : Protected access
- `~` : Package-private access
- `✓` : Synchronized (thread-safe)
- `<<abstract>>` : Abstract class
- `<<interface>>` : Interface
- `extends` : Inheritance
- `implements` : Interface implementation
- `uses` : Dependency relationship

This architecture demonstrates solid OOP principles with clear separation of concerns, proper encapsulation, and thread-safe concurrent design patterns.
