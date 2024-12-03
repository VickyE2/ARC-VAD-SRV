package com.arcvad.schoolquest.server.server.Playerutils;
import java.util.UUID;

public class UUIDGenerator {
    // Generate a UUID based on the string input and namespace
    public static UUID generateUUIDFromString(String input) {
        // Use a namespace UUID (you can use a fixed one or generate a custom one)
        UUID namespace = UUID.nameUUIDFromBytes("namespace".getBytes());
        return UUID.nameUUIDFromBytes((namespace.toString() + input).getBytes());
    }
}
