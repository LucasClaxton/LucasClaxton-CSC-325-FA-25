# Concurrency Report: Dungeon Crawler Thread Synchronization

## Executive Summary

This report details the thread synchronization architecture of the Dungeon Crawler adventure game. The game employs multiple concurrent threads to simulate three heroes exploring a dungeon simultaneously while ensuring data integrity and preventing race conditions through synchronized access patterns.

**Thread Model**: 3 Character Threads + 1 Main Game Thread
**Primary Concern**: Shared resources (inventory, character stats, room state)
**Synchronization Approach**: Synchronized methods, blocks, and ReentrantReadWriteLock

## System Architecture

### Thread Hierarchy

```
Main Thread (DungeonCrawlerApp)
├── Character Thread 1 (Knight)
├── Character Thread 2 (Thief)
└── Character Thread 3 (Wizard)
```

### Execution Flow

1. Main thread creates three character objects (Knight, Thief, Wizard)
2. Main thread starts each character as a separate thread
3. Character threads run concurrently with run() method implementation
4. Main thread calls join() on all character threads to wait for completion
5. Upon thread completion, game ends and statistics are displayed

## Thread-Safe Components

### 1. GameCharacter Class (Abstract Base)

#### Synchronized Methods:
- `heal(int amount)` - Atomically updates health
- `takeDamage(int amount)` - Prevents corruption of health value
- `restoreMana(int amount)` - Atomic mana restoration
- `addGold(int amount)` - Prevents gold count race conditions
- `spendGold(int amount)` - Atomic gold transaction
- `addExperience(int amount)` - Level-up logic protected
- `addLog(String message)` - Battle log thread-safe append
- `winBattle()` - Increments battle counter atomically
- `collectItem()` - Item count incremented safely

**Rationale**: All character state modifications are synchronized to prevent:
- Multiple threads reading stale values
- Lost updates (last-write-wins problem)
- Partial state changes (e.g., healing beyond max HP)

### 2. Inventory Class

#### Synchronized Methods (per-character Inventory):
```java
public synchronized boolean addEquipment(Equipment item)
public synchronized boolean addPotion(Potion potion)
public synchronized Equipment removeEquipment(EquipmentType type)
public synchronized Potion usePotion(Potion.PotionType type)
public synchronized Equipment getEquipment(EquipmentType type)
public synchronized Map<EquipmentType, Equipment> getAllEquipment()
public synchronized List<Potion> getAllPotions()
public synchronized boolean hasSpace()
public synchronized void clear()
```

**Inventory Model**: Each hero has its own `Inventory` instance (capacity 50 by default). The game provides a 21-slot aggregated management UI that consolidates items from all heroes for the player's convenience — the UI is a view/renderer and does not replace the per-character inventories.

**Shared Resource**: The true shared party resource is the `playerGold` pool in `DungeonCrawlerApp` (single integer). Inventory operations are synchronized per Inventory instance; moving items between heroes or updating an individual hero's inventory is protected by that hero's Inventory locks.

**Example Race Condition Prevented** (per-Inventory):
```
Without synchronization:
Thread 1: Check if hero's inventory has space
Thread 2: Add item to same hero (takes last space)
Thread 1: Add item (buffer overflow!)

With synchronization:
Thread 1: locks hero's Inventory, checks space, adds item, unlocks
Thread 2: waits for lock, then checks space, adds item or fails gracefully
```

### 3. Room Classes (ChestRoom, CombatRoom, ShopRoom)

#### Synchronized Blocks:
```java
synchronized(this) {
    if (!opened) {
        opened = true;
        // Process loot/combat/shop
    }
}
```

**Purpose**: Ensure only one character can interact with a room at a time

**Race Condition Prevented**:
- Multiple characters finding duplicate loot from same chest
- Multiple characters fighting same enemy sequentially
- Shop purchased by multiple characters simultaneously

### 4. Boss Class

#### Synchronized Battle State:
```java
public void battle(GameCharacter[] characters) {
    // Thread-safe damage calculation
    // Prevents multiple threads modifying boss health simultaneously
}
```

**Protection**: Boss health modifications are atomic
**Guarantee**: Damage is accurately applied, no lost updates

### 5. GameEngine Class

#### ReentrantReadWriteLock:
```java
private ReentrantReadWriteLock lock;

public void generateFloorRooms() {
    lock.writeLock().lock();
    try {
        // Modify floor rooms
    } finally {
        lock.writeLock().unlock();
    }
}
```

**Purpose**: Multiple threads can read current floor safely, only one can write

**Benefit**: Improves concurrency - many readers don't block each other

## Potential Race Conditions & Solutions

### Race Condition 1: Character Health Update

**Scenario**:
```
Thread 1: health = 100
Thread 2: Reads health = 100
Thread 1: Applies 20 damage, health = 80, writes back
Thread 2: Applies 15 damage, health = 85, writes back
Result: Lost 20 damage! Health should be 65.
```

**Solution**: Synchronized `takeDamage()` method
```java
public synchronized void takeDamage(int amount) {
    currentHealth -= amount;
    // Atomic: read-modify-write is indivisible
}
```

### Race Condition 2: Inventory Corruption (per-hero)

**Scenario**:
```
Thread 1 (Thief): heroInventory.addEquipment(sword1)
Thread 2 (Thief): heroInventory.getAllEquipped()  // May read while write in progress
Thread 1: heroInventory.removeEquipmentInstance(dagger) // Modification during read
Result: The Thief may observe an inconsistent inventory snapshot
```

**Solution**: Use synchronized Inventory methods and return snapshots for read operations
```java
public synchronized Map<EquipmentType, Equipment> getAllEquipped() {
    return new HashMap<>(equipped);  // Returns a safe snapshot copy
}
```

### Race Condition 3: Room Loot Duplication

**Scenario**:
```
Thread 1 (Knight): Enters chest room
Thread 2 (Wizard): Enters same chest room
Both find the same loot!
```

**Solution**: Synchronized room state with boolean flag
```java
synchronized(this) {
    if (!opened) {
        opened = true;  // Mark as used
        // Distribute loot
    }
}
```

### Race Condition 4: Experience & Level Up

**Scenario**:
```
Thread 1: experience = 95
Thread 2: Reads experience = 95
Thread 1: Adds 10 exp, experience = 105, levels up
Thread 2: Adds 8 exp, experience = 103 (lost the level-up!)
Result: Character doesn't level up at correct point
```

**Solution**: Synchronized `addExperience()` with level-up logic
```java
public synchronized void addExperience(int amount) {
    experience += amount;
    while (experience >= 100) {
        experience -= 100;
        level++;
        // All level-up logic protected
    }
}
```

## Concurrency Mechanisms Used

### 1. Synchronized Methods
**When**: Single method that modifies/reads shared state
**Example**: `heal()`, `takeDamage()`, `addGold()`
**Overhead**: Minimal, lock per method call

### 2. Synchronized Blocks
**When**: Only part of a method needs synchronization
**Example**: Room entry with state check
**Overhead**: More efficient than method-wide locks

### 3. ReentrantReadWriteLock
**When**: Many readers, few writers
**Example**: Floor room list access
**Advantage**: Multiple threads can read simultaneously

### 4. Thread.join()
**When**: Main thread waits for worker threads
**Example**: Waiting for all character threads to complete
**Guarantee**: Sequential consistency after join()

### 5. ConcurrentHashMap / Collections
**Where**: Currently using HashMap with synchronization
**Alternative**: Could use ConcurrentHashMap for better performance
**Choice**: Synchronized for teaching clarity

## Thread Safety Guarantees

### Memory Visibility
- Synchronized methods act as memory barriers
- Changes visible to all threads after lock release
- Volatile fields ensure visibility across threads

### Atomicity
- Read-modify-write operations are indivisible
- No partial state corruption
- All-or-nothing semantics

### Ordering
- Locks enforce happens-before relationships
- Lock acquisition-release creates order
- No instruction reordering violations

### Deadlock Prevention
- Single lock per resource (no circular dependencies)
- Locks always released (try-finally blocks)
- ReentrantReadWriteLock handles recursion

## Performance Considerations

### Lock Contention Points

1. **Character Health Updates** (Moderate)
   - Called frequently during combat
   - Brief critical section
   - Multiple characters, single resource

2. **Inventory Operations** (Low to Moderate)
   - Called less frequently
   - Characters rarely access simultaneously
   - Shared across all characters

3. **Room Entry** (Low)
   - Sequential room progression
   - No simultaneous room access expected
   - Short critical section

### Optimization Opportunities

1. **Use ConcurrentHashMap** for inventory
   - Segment-level locking instead of global lock
   - Better multi-threaded performance

2. **Character-Specific Locks**
   - Each character has its own health lock
   - Reduces contention vs. global lock

3. **Compare-and-Swap (CAS)**
   - For simple integer updates
   - Lock-free alternatives

## Testing Thread Safety

### Stress Test Scenarios

1. **Multiple Characters Taking Damage Simultaneously**
   ```
   Verify: Health decrements correctly, no missed updates
   ```

2. **Simultaneous Inventory Access**
   ```
   Verify: Items added/removed consistently
   Verify: No inventory overflow
   ```

3. **Concurrent Room Entry**
   ```
   Verify: Only one character gets the loot
   Verify: Others see room as already processed
   ```

4. **Boss Battle Thread Coordination**
   ```
   Verify: Health updates don't corrupt
   Verify: Damage calculations accurate
   Verify: Boss defeat properly detected
   ```

### Verification Methods

- **Console Logging**: Track all state changes
- **Battle Logs**: Each character records events
- **Final Statistics**: Verify totals are consistent
- **Health Assertions**: Verify never exceeds max

## Thread Lifecycle

### Character Thread Lifecycle

1. **Creation**: Thread object created in DungeonCrawlerApp
2. **Start**: `thread.start()` calls `run()` method
3. **Initialization**: Character logs entry message
4. **Active**: Thread waits for game events
5. **Termination**: `run()` method completes
6. **Join**: Main thread waits for completion

### Synchronization Points

```
Main Thread                 Character Thread
-----------                 ----------------
Start character threads
                            run() starts
                            Enter dungeon
Wait with join()
                            Adventure completes
                            run() exits
Threads complete
```

## Exception Handling & Thread Safety

### InterruptedException Handling
```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

### Lock Release Guarantees
```java
lock.writeLock().lock();
try {
    // Modify shared state
} finally {
    lock.writeLock().unlock();  // Always released
}
```

## Conclusion

The Dungeon Crawler implements a robust multi-threaded architecture with careful attention to thread safety:

1. **Synchronized Access**: All shared resources protected
2. **No Race Conditions**: Identified and prevented through design
3. **Data Consistency**: Inventory and character stats always valid
4. **Deadlock-Free**: Single lock per resource prevents deadlocks
5. **Memory Safe**: Proper visibility and atomicity guarantees

The implementation balances correctness with performance, using appropriate synchronization mechanisms for each shared resource. The game demonstrates fundamental multi-threading concepts suitable for an advanced OOP course.

### Key Takeaways

- Thread synchronization is essential for shared resource access
- Multiple synchronization mechanisms serve different purposes
- Careful design prevents race conditions and deadlocks
- Testing concurrent systems requires special attention
- Comments and documentation clarify synchronization strategy
