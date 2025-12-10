package com.dungeonCrawler.boss;

/**
 * Factory for creating bosses at different difficulty levels.
 */
public class BossFactory {
    
    public static Boss createFloorBoss(int floorNumber) {
        switch(floorNumber) {
            case 5:
                return new Boss(
                    "Garth the Stone Guardian",
                    "Guardian of the Depths",
                    300,
                    25,
                    "A massive creature of living stone blocks your path. Its eyes glow with ancient power!\n" +
                    "\"None shall pass beyond this point,\" it rumbles menacingly.",
                    5
                );
            case 10:
                return new Boss(
                    "Zephyr the Storm Elemental",
                    "Master of the Winds",
                    500,
                    35,
                    "Lightning crackles around a towering elemental form.\n" +
                    "\"I am the fury of nature! Your mortal forms are but dust before me!\"",
                    10
                );
            default:
                return null;
        }
    }
    
    public static Boss createFinalBoss() {
        return new Boss(
            "Malachar",
            "THE DARK LORD MALACHAR - FINAL ANTAGONIST",
            1000,
            50,
            "Before you stands Malachar, the ancient Dark Lord whose corruption has plagued these lands!\n\n" +
            "\"AT LAST! The pathetic heroes arrive to meet their doom!\"\n" +
            "\"For a thousand years I have waited, gathering power, spreading darkness through every corner.\"\n" +
            "\"Did you think your pitiful blades and magic could harm one such as I?\"\n" +
            "\"I AM ETERNAL! I AM INEVITABLE!\"\n\n" +
            "Malachar raises his staff, and shadows surge around the chamber. The final battle begins!\n",
            15
        );
    }
}
