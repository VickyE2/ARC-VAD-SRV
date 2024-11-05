package com.arcvad.schoolquest.server.server.Managers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonDataExtractor {
    public static Communicator.UserRegister getUserDataFromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Communicator.UserRegister.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

