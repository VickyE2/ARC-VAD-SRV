package com.arcvad.schoolquest.server.server.GlobalUtils;

import java.util.ArrayList;
import java.util.List;

public class Range {
    int min;
    int max;
    double weight;
    int increment;

    public Range(int min, int max, double weight, int increment) {
        this.min = min;
        this.max = max;
        this.weight = weight;
        this.increment = increment;
    }

    List<Integer> getPossibleValues() {
        List<Integer> values = new ArrayList<>();
        for (int i = min; i <= max; i += increment) {
            values.add(i);
        }
        return values;
    }
}
