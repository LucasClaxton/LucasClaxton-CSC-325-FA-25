package com.dungeonCrawler.rooms;

import com.dungeonCrawler.characters.GameCharacter;
import com.dungeonCrawler.items.Equipment;
import com.dungeonCrawler.items.EquipmentType;
import com.dungeonCrawler.items.Potion;
import java.util.Random;

/**
 * Chest room - Contains treasure and loot.
 */
public class ChestRoom implements Room {
    private Random random;
    private boolean opened;
    
    public ChestRoom() {
        this.random = new Random();
        this.opened = false;
    }
    
    @Override
    public void enter(GameCharacter character) {
        character.addLog(character.getCharacterName() + " enters a chamber with an ornate chest!");
        
        synchronized(this) {
            if (!opened) {
                opened = true;
                
                // Random loot
                int lootType = random.nextInt(3);
                switch(lootType) {
                    case 0: // Gold
                        int gold = random.nextInt(100) + 50;
                        character.addGold(gold);
                        character.addLog("Found " + gold + " gold!");
                        break;
                    case 1: // Equipment
                        EquipmentType[] types = EquipmentType.values();
                        EquipmentType eqType = types[random.nextInt(types.length)];
                        Equipment equipment = new Equipment(eqType, character.getLevel());
                        character.getInventory().addEquipment(equipment);
                        character.addLog("Found: " + equipment.toString());
                        character.collectItem();
                        break;
                    case 2: // Potion
                        Potion.PotionType[] potionTypes = Potion.PotionType.values();
                        Potion.PotionType pType = potionTypes[random.nextInt(potionTypes.length)];
                        Potion potion = new Potion(pType, random.nextInt(2) + 1);
                        character.getInventory().addPotion(potion);
                        character.addLog("Found: " + potion.toString());
                        character.collectItem();
                        break;
                }
            } else {
                character.addLog("The chest is already looted!");
            }
        }
    }
    
    @Override
    public String getRoomType() {
        return "Chest Room";
    }
    
    @Override
    public String getDescription() {
        return "A treasure-filled chamber. " + (opened ? "The chest is already open and empty." : "An ornate chest gleams in the torchlight.");
    }
}
