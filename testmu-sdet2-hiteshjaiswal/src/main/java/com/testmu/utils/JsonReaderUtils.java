package com.testmu.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class JsonReaderUtils {
    public static JsonNode readJson(String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException("failed to read json file" + e);
        }
    }
}
