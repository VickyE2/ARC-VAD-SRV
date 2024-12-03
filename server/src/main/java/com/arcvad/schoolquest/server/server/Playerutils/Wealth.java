package com.arcvad.schoolquest.server.server.Playerutils;

import java.util.Arrays;
import java.util.Random;

public enum Wealth {
    RELATIVELY_POOR(4),
    SLIGHTLY_BELOW_AVERAGE(12),
    AVERAGE(37),
    SLIGHTLY_ABOVE_AVERAGE(21),
    RELATIVELY_RICH(12),
    RICH(7),
    VERY_RICH(5),
    GOD_GIVEN_WEALTH(2);

    private final int chance;

    Wealth(int chance) {
        this.chance = chance;
    }

    public int getChance() {
        return chance;
    }

    public static Wealth getRandomWealthByWeight() {
        int totalWeight = Arrays.stream(Wealth.values())
            .mapToInt(Wealth::getChance)
            .sum();

        int randomValue = new Random().nextInt(totalWeight);
        int cumulativeWeight = 0;

        for (Wealth wealth : Wealth.values()) {
            cumulativeWeight += wealth.getChance();
            if (randomValue < cumulativeWeight) {
                System.out.println(STR."Resulting weight \{wealth}");
                return wealth;
            }
        }
        throw new IllegalStateException("Weighted random selection failed.");
    }
}
