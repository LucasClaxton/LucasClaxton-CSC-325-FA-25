package com.dungeonCrawler.rooms;

import com.dungeonCrawler.characters.GameCharacter;

/**
 * Interface for all room types in the dungeon.
 */
public interface Room {
    /**
     * Character enters and interacts with the room.
     */
    void enter(GameCharacter character);
    
    /**
     * Get the room type name.
     */
    String getRoomType();
    
    /**
     * Get room description.
     */
    String getDescription();
}
