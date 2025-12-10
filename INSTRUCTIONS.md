# Dungeon Crawler - Instructions & Controls

## How to Play

### Getting Started

1. **Launch the Game**
   ```
   java com.dungeonCrawler.engine.DungeonCrawlerApp
   ```

2. **Meet Your Heroes**
   - **Theron the Knight**: A formidable warrior with high HP and defense
   - **Shadowblade the Thief**: Quick and deadly, with critical strike potential
   - **Arcanis the Wizard**: Master of magic with powerful spells

3. **Understand Your Objective**
   - Descend through 15 floors of a dungeon
   - Survive encounters with monsters, collect treasure, and defeat bosses
   - Reach the final floor and defeat Malachar, the Dark Lord

### Game Structure

#### Floors (1-14)
Each floor contains:
- **5 Room Choices**: Players select rooms to explore
- **Rest Points**: Between room selections, rest to heal and manage inventory
- **Random Encounters**: Combat, treasure, or shops

#### Boss Floors
- **Floor 5**: Fight "Garth the Stone Guardian"
- **Floor 10**: Fight "Zephyr the Storm Elemental"
- **Floor 15 (Final)**: Epic showdown with Malachar, the Dark Lord

### Controls & Menu Options

#### Main Game Menu
When prompted during a floor, enter:
1. **Explore Room** - Enter the next dungeon chamber
2. **Inventory and Equipment** - Manage shared party inventory
3. **Check Status** - View character statistics
4. **Rest and recover** - Heal party for 25 HP, refill all mana, and skip next combat

#### Room Types

**Chest Rooms**
- Contain random loot:
  - Gold (50-150)
  - Equipment (varies by level)
  - Potions (Health, Mana, or Vigor)

**Combat Rooms**
- Fight monsters scaled to current floor
- **If Rest was selected**: Combat system is skipped, all living heroes take equal damage from the enemy, but mana stays refilled
- **If Rest was NOT selected**: Full combat execution with simultaneous hero attacks and enemy counterattacks
- Victory rewards: Experience, gold, and battle records
- Loss: Character takes damage

**Shop Rooms** (once per floor)
- Purchase healing (50 gold)
- Buy new equipment
- Only available once per floor

### Character Abilities

#### Knight (Theron)
- **Shield Bash** (30 Mana)
  - Blocks damage and increases armor temporarily
  - Perfect for tanking damage
  
- **Heavy Attack**
  - Strong melee strike
  - Scales with armor

- **Defensive Stance**
  - Temporarily increases armor
  - Good for strategic positioning

#### Thief (Shadowblade)
- **Shadow Clone** (25 Mana)
  - Evade next attack
  - Increase agility temporarily
  
- **Backstab**
  - Critical strike ability
  - 25% base critical chance

- **Pickpocket**
  - Steal gold from enemies
  - Chance to gain 25-75 gold

- **Evasion**
  - Enter evasive stance
  - Increase dodge chance

#### Wizard (Arcanis)
- **Fireball** (40 Mana)
  - Area of effect damage spell
  - Increase spell power temporarily

- **Arcane Missile** (15 Mana)
  - Quick single-target damage
  - Spammable spell

- **Mana Shield** (50 Mana)
  - Convert mana to temporary armor
  - Emergency defense

- **Teleport** (35 Mana)
  - Escape dangerous situations
  - Avoid damage

- **Mana Drain**
  - Restore 30 mana from environment
  - Low cost ability

### Character Stats

**Health**: Current and maximum hit points
**Mana**: Energy used for abilities and spells
**Level**: Character progression (increases with experience)
**Gold**: Currency for purchases and upgrades
**Battles Won**: Count of defeated enemies
**Items Collected**: Number of items found

### Resource Management

#### Gold
- **Earn**: From defeating enemies and finding treasure
- **Spend**: At shops for healing and equipment
- **Manage**: Budget carefully for major purchases

#### Experience
- **Gain**: From defeating monsters and bosses
- **Level Up**: Every 100 experience points
- **Bonus**: Health and mana increase on level up

#### Inventory
- **Capacity**: 50 items maximum (shared among all characters)
- **Equipment**: Armor, weapons, accessories
- **Potions**: Health, Mana, and Vigor potions
- **Management**: Use rest points to organize equipment

### Boss Battles

#### How Boss Battles Work
1. Boss enters with dramatic narration
2. Each round, heroes and boss exchange blows
3. Multiple rounds until boss is defeated (or heroes fall)
4. Victory grants massive experience and gold
5. Defeat on boss floor: Game Over

#### Tips for Boss Battles
- Use rest points before boss floors to prepare
- Stock up on healing potions
- Use defensive abilities to reduce damage
- Coordinate party attacks for maximum damage
- Manage mana carefully for sustained offense

### Game Progression

**Early Game (Floors 1-4)**
- Learn mechanics
- Build up gold and equipment
- Scout room types

**Mid Game (Floors 5-9)**
- First boss encounter
- Stronger equipment available
- Characters level up significantly

**Late Game (Floors 10-14)**
- Second boss encounter
- Prepare for final battle
- Maximize character power

**End Game (Floor 15)**
- Epic final boss battle
- Ultimate test of skill and strategy
- Post-game statistics and rankings

### Tips & Strategies

1. **Balance Party**: Use each character's strengths
2. **Rest Often**: Don't run out of resources
3. **Collect Equipment**: Upgrade as you find better items
4. **Use Abilities**: Don't waste special powers - use them strategically
5. **Manage Inventory**: Keep the shared inventory organized
6. **Track Resources**: Monitor gold and potion stocks
7. **Read Narratives**: Story elements provide context and hints

### Victory Conditions

- **Standard Victory**: Defeat Malachar on Floor 15
- **Heroic Victory**: Complete with all characters alive and high level
- **Perfect Victory**: Minimal character damage and maximum loot

### Game Over Conditions

- **All Characters Defeated**: If all three heroes fall during a boss battle
- **Retreat**: Choose to quit the game at any point

### Post-Game Statistics

After completing the game, you'll see:
- **Individual Stats**: Each character's final performance
- **Rankings**: Characters ranked by battles won and gold collected
- **Highest Level**: Most experienced character
- **Total Achievements**: Cumulative party statistics

### Troubleshooting

**Game Won't Compile**
- Ensure all source files are in `src/com/dungeonCrawler/`
- Check for missing semicolons and braces

**Game Crashes**
- Make sure Java is properly installed
- Use Java 8 or higher

**Unexpected Behavior**
- Thread synchronization ensures no data corruption
- If issues persist, restart the game

### Advanced Notes

This game demonstrates:
- **Thread Concurrency**: Characters operate in parallel
- **Synchronization**: Shared resources protected from race conditions
- **OOP Design Patterns**: Inheritance, polymorphism, abstraction
- **Lambda Expressions**: Stream processing for statistics
- **Event-Driven Systems**: Boss encounters and room interactions

Enjoy your descent into the Dungeon Crawler!
