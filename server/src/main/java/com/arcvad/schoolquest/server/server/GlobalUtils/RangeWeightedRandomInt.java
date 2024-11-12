package com.arcvad.schoolquest.server.server.GlobalUtils;

import java.util.List;
import java.util.Random;

public class RangeWeightedRandomInt {
    private static final Random random = new Random();

    // Method to get a weighted random integer
    public static int getWeightedRandomInt(List<Range> ranges) {
        // Calculate total weight
        double totalWeight = ranges.stream().mapToDouble(r -> r.weight).sum();
        double randomValue = random.nextDouble() * totalWeight;

        // Select range based on weight
        for (Range range : ranges) {
            randomValue -= range.weight;

            if (randomValue <= 0) {
                // Once a range is selected, pick a random number from possible values in the range
                List<Integer> possibleValues = range.getPossibleValues();
                return possibleValues.get(random.nextInt(possibleValues.size()));
            }
        }

        // Default return in case of rounding errors (should rarely, if ever, happen)
        Range lastRange = ranges.get(ranges.size() - 1);
        List<Integer> possibleValues = lastRange.getPossibleValues();
        return possibleValues.get(random.nextInt(possibleValues.size()));
    }
}
