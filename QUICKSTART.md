# Quick Start Guide - Dungeon Crawler

## Installation & Running (Windows PowerShell)

### Step 1: Navigate to Project Directory
```powershell
cd c:\Users\lclax\lucas_claxton_csc_325\LucasClaxton-CSC-325-FA-25
```

### Step 2: Compile (if needed)
```powershell
$files = Get-ChildItem -Recurse -Path 'src' -Filter '*.java' | Select-Object -ExpandProperty FullName
javac -d bin $files
```

### Step 3: Run the Game
```powershell
cd bin
java com.dungeonCrawler.engine.DungeonCrawlerApp
```

## In-Game Menu

When playing, you'll see menu prompts like:
```
FLOOR 1

[FLOOR 1] [ROOM 1 OF 5]
Choices:
1. Explore
2. Inventory and Equipment
3. Check status
4. Rest and recover

Choice: _
```

### Common Actions

**Explore**: Enter 1 to explore a room (combat, treasure, or shop)
**Inventory**: Enter 2 to manage equipment and potions
**Status**: Enter 3 to check character health and stats
**Rest**: Enter 4 to heal party 25 HP and refill mana (skips next combat if room is combat)

## Game Overview

### Your Party
- **Theron** (Knight) - Tank with heavy armor
- **Shadowblade** (Thief) - Fast with critical strikes
- **Arcanis** (Wizard) - Spellcaster with area damage

### Floors
- **Floors 1-4**: Introduction and loot gathering
- **Floor 5**: First boss battle (Garth)
- **Floors 6-9**: Mid-game challenges
- **Floor 10**: Second boss battle (Zephyr)
- **Floors 11-14**: Final preparations
- **Floor 15**: Final boss (Malachar)

### Tips for Success
1. Use Rest strategically to skip combat when HP is low
2. Collect equipment from chests and shops to strengthen your party
3. Save gold for the shop to buy better gear
4. Rest refills all mana, so use spells more freely after resting
5. Manage your rest uses—you only get one per room!

## File Overview

| File | Purpose |
|------|---------|
| README.md | Project overview |
| INSTRUCTIONS.md | Full gameplay guide |
| UML_DIAGRAM.md | Architecture and design |
| CONCURRENCY_REPORT.md | Thread synchronization details |
| PROJECT_SUMMARY.md | Complete project documentation |

## Troubleshooting

**Game won't compile?**
- Make sure Java Development Kit (JDK) is installed
- Check that all source files are in `src/com/dungeonCrawler/`

**Game won't run?**
- Ensure you're in the `bin` directory
- Check Java is installed: `java -version`

**Unexpected behavior?**
- The game uses multithreading; some timing may vary
- This is normal and demonstrates concurrent execution

## Game Statistics

After completing the game, you'll see:
- Individual character stats (level, gold, battles won)
- Rankings by battles won and gold collected
- Highest level character
- Total party achievements

## For More Information

-- See **INSTRUCTIONS.md** for detailed controls
- See **CONCURRENCY_REPORT.md** for threading details
- See **UML_DIAGRAM.md** for architecture overview

Enjoy your descent into the Dungeon Crawler!
