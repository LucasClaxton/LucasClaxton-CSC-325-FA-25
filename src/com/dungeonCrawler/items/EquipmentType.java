package com.dungeonCrawler.items;

/**
 * Enum for equipment types.
 */
public enum EquipmentType {
    SWORD("Longsword", 15),
    DAGGER("Dagger", 8),
    STAFF("Mana Staff", 12),
    HELMET("Iron Helmet", 10),
    CHESTPLATE("Armored Chestplate", 25),
    SHIELD("Wooden Shield", 15),
    GLOVES("Leather Gloves", 5),
    BOOTS("Swift Boots", 3);
    
    private final String description;
    private final int value;
    
    EquipmentType(String description, int value) {
        this.description = description;
        this.value = value;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getValue() {
        return value;
    }
}
