package com.architecture.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestHelper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T>T buildEntityFromFile(String file, Class<T> entity) throws IOException {
        var content = Files.readString(Path.of(file));
        T entityObj = objectMapper.readValue(content, entity);
        return entityObj;
    }
}
