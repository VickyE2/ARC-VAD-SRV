package com.arcvad.schoolquest.server.server.GlobalUtils;

import java.util.Random;

public class EnumRandomizer {
    private static final Random RANDOM = new Random();

    public static <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] enumValues = enumClass.getEnumConstants();
        int randomIndex = RANDOM.nextInt(enumValues.length);
        return enumValues[randomIndex];
    }
}

