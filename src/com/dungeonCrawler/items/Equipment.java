package com.dungeonCrawler.items;

/**
 * Equipment item class.
 */
public class Equipment {
    private EquipmentType type;
    private int level;
    private String lore;
    
    public Equipment(EquipmentType type, int level) {
        this.type = type;
        this.level = level;
        this.lore = generateLore();
    }
    
    private String generateLore() {
        switch(type) {
            case SWORD:
                return "A legendary blade passed down through generations of warriors.";
            case DAGGER:
                return "A swift and deadly dagger, perfect for precision strikes.";
            case STAFF:
                return "An ancient staff crackling with arcane energy.";
            case HELMET:
                return "A sturdy helmet that has seen many battles.";
            case CHESTPLATE:
                return "Heavy armor forged by master blacksmiths.";
            case SHIELD:
                return "A reliable shield that never fails to protect.";
            case GLOVES:
                return "Supple leather gloves for better grip and dexterity.";
            case BOOTS:
                return "Enchanted boots allowing swift movement.";
            default:
                return "An unidentified item of power.";
        }
    }
    
    public EquipmentType getType() { return type; }
    public int getLevel() { return level; }
    public String getLore() { return lore; }
    
    @Override
    public String toString() {
        return type.getDescription() + " (Level " + level + ") - " + lore;
    }
}
