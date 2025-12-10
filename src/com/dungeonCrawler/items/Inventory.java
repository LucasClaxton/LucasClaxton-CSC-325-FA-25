package com.dungeonCrawler.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Inventory now separates equipped items from bag (backpack) items.
 * Bag can hold multiple equipment of the same type.
 * Thread-safe for concurrent access.
 */
public class Inventory {
    // Equipped items: one per EquipmentType
    private Map<EquipmentType, Equipment> equipped;
    // Bag/backpack: can contain multiple equipments (including duplicates by type)
    private List<Equipment> bag;
    private List<Potion> potions;
    private int capacity;
    private int currentSize;
    
    public Inventory() {
        this.equipped = new HashMap<>();
        this.bag = new ArrayList<>();
        this.potions = new ArrayList<>();
        this.capacity = 50;
        this.currentSize = 0;
    }
    
    /**
     * Add equipment to the bag (thread-safe). Does not equip.
     */
    public synchronized boolean addEquipment(Equipment item) {
        if (currentSize >= capacity) {
            return false;
        }
        bag.add(item);
        currentSize++;
        return true;
    }
    
    /**
     * Add potion to inventory (thread-safe).
     */
    public synchronized boolean addPotion(Potion potion) {
        for (Potion p : potions) {
            if (p.getType() == potion.getType()) {
                p.setQuantity(p.getQuantity() + potion.getQuantity());
                return true;
            }
        }
        if (currentSize >= capacity) {
            return false;
        }
        potions.add(potion);
        currentSize++;
        return true;
    }
    
    /**
     * Unequip equipment of the given type (remove from equipped slots).
     */
    public synchronized Equipment removeEquipped(EquipmentType type) {
        if (equipped.containsKey(type)) {
            Equipment removed = equipped.remove(type);
            // returned equipment goes back to bag manually by caller if desired
            currentSize--;
            return removed;
        }
        return null;
    }

    /**
     * Remove a specific equipment instance from the bag.
     */
    public synchronized Equipment removeEquipmentInstance(Equipment item) {
        if (bag.remove(item)) {
            currentSize--;
            return item;
        }
        return null;
    }
    
    /**
     * Use a potion from inventory.
     */
    public synchronized Potion usePotion(Potion.PotionType type) {
        for (Potion p : new ArrayList<>(potions)) {
            if (p.getType() == type) {
                if (p.getQuantity() > 0) {
                    p.use();
                    if (p.getQuantity() == 0) {
                        potions.remove(p);
                        currentSize--;
                    }
                    return p;
                }
            }
        }
        return null;
    }
    
    /**
     * Get currently equipped item by type.
     */
    public synchronized Equipment getEquipped(EquipmentType type) {
        return equipped.get(type);
    }

    /**
     * Get all equipped items.
     */
    public synchronized Map<EquipmentType, Equipment> getAllEquipped() {
        return new HashMap<>(equipped);
    }

    /**
     * Get all bag (unequipped) equipment.
     */
    public synchronized List<Equipment> getBagEquipment() {
        return new ArrayList<>(bag);
    }

    /**
     * Equip an equipment from the bag. Returns the previously equipped item (if any).
     * Caller is responsible for placing the returned item back into a bag (addEquipment).
     */
    public synchronized Equipment equipEquipment(Equipment item) {
        // remove from bag if present
        boolean removed = bag.remove(item);
        if (!removed) return null;
        currentSize--;
        Equipment prev = equipped.put(item.getType(), item);
        return prev;
    }

    /**
     * Get all potions.
     */
    public synchronized List<Potion> getAllPotions() {
        return new ArrayList<>(potions);
    }
    
    /**
     * Check if inventory has space.
     */
    public synchronized boolean hasSpace() {
        return currentSize < capacity;
    }
    
    /**
     * Clear inventory.
     */
    public synchronized void clear() {
        equipped.clear();
        bag.clear();
        potions.clear();
        currentSize = 0;
    }
    
    public synchronized int getCurrentSize() { return currentSize; }
    public int getCapacity() { return capacity; }
}
