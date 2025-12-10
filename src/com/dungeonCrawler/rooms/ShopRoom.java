package com.dungeonCrawler.rooms;

import com.dungeonCrawler.characters.GameCharacter;
import com.dungeonCrawler.items.Equipment;
import com.dungeonCrawler.items.EquipmentType;
import com.dungeonCrawler.items.Potion;
import java.util.Random;

/**
 * Shop room - Purchase items and potions.
 * Can only be used once per floor.
 */
public class ShopRoom implements Room {
    private Random random;
    private boolean visited;
    private String shopkeeper;
    
    public ShopRoom() {
        this.random = new Random();
        this.visited = false;
        this.shopkeeper = "Eldric";
    }
    
    @Override
    public void enter(GameCharacter character) {
        character.addLog(character.getCharacterName() + " enters the mysterious shop of " + shopkeeper + "!");
        
        synchronized(this) {
            if (!visited) {
                visited = true;
                
                // Random shop event
                int shopEvent = random.nextInt(2);
                switch(shopEvent) {
                    case 0: // Healing service
                        character.addLog(shopkeeper + " offers healing! Full restoration for 50 gold.");
                        if (character.spendGold(50)) {
                            character.heal(character.getMaxHealth());
                            character.restoreMana(character.getMaxMana());
                            character.addLog(character.getCharacterName() + " is fully healed!");
                        } else {
                            character.addLog("Not enough gold for healing!");
                        }
                        break;
                    case 1: // Equipment sale
                        EquipmentType[] types = EquipmentType.values();
                        EquipmentType eqType = types[random.nextInt(types.length)];
                        Equipment equipment = new Equipment(eqType, character.getLevel() + 1);
                        int price = eqType.getValue() * 20;
                        
                        character.addLog(shopkeeper + " offers " + equipment.toString() + " for " + price + " gold.");
                        if (character.spendGold(price)) {
                            character.getInventory().addEquipment(equipment);
                            character.addLog(character.getCharacterName() + " purchased the item!");
                        } else {
                            character.addLog("Not enough gold for purchase!");
                        }
                        break;
                }
            } else {
                character.addLog("The shop is already closed!");
            }
        }
    }
    
    @Override
    public String getRoomType() {
        return "Shop Room";
    }
    
    @Override
    public String getDescription() {
        return (visited ? "The shop is closed." : "A mysterious shop run by " + shopkeeper + ".");
    }
}
