package com.dungeonCrawler.items;

/**
 * Potion item class.
 */
public class Potion {
    public enum PotionType {
        HEALTH("Health Potion", 50, "Restores 50 HP"),
        MANA("Mana Potion", 50, "Restores 50 Mana"),
        VIGOR("Vigor Potion", 75, "Restores 75 HP");
        
        private final String name;
        private final int restoreAmount;
        private final String description;
        
        PotionType(String name, int restoreAmount, String description) {
            this.name = name;
            this.restoreAmount = restoreAmount;
            this.description = description;
        }
        
        public String getName() { return name; }
        public int getRestoreAmount() { return restoreAmount; }
        public String getDescription() { return description; }
    }
    
    private PotionType type;
    private int quantity;
    
    public Potion(PotionType type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }
    
    public PotionType getType() { return type; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void use() { quantity--; }
    
    @Override
    public String toString() {
        return type.getName() + " x" + quantity + " - " + type.getDescription();
    }
}
