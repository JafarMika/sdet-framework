package framework.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JsonHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String readJsonFile(String fileName) {
        String resourcePath = fileName.startsWith("payloads/") ? fileName : "payloads/" + fileName;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("JSON file not found: " + resourcePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON file: " + fileName, e);
        }
    }

    public Map<String, Object> toMap(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to parse JSON", e);
        }
    }

    public Object getValue(String json, String key) {
        Map<String, Object> map = toMap(json);
        if (!map.containsKey(key)) {
            throw new IllegalStateException("Key not found in JSON: " + key);
        }
        return map.get(key);
    }

    public String updateValue(String json, String key, Object value) {
        try {
            Map<String, Object> map = toMap(json);
            map.put(key, value);
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to update JSON", e);
        }
    }

}
