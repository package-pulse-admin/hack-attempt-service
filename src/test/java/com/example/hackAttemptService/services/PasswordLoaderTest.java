package com.example.hackAttemptService.services;

import com.example.hackAttemptService.service.PasswordLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordLoaderTest {

    private static final String TEST_RESOURCE_DIR = "test-passwords/";
    @BeforeAll
    static void setup() throws IOException {
        Path resourcePath = Paths.get("target", "test-classes", TEST_RESOURCE_DIR);
        Files.createDirectories(resourcePath);

        Files.write(resourcePath.resolve("sample1.txt"),
                Arrays.asList("123456", "password", "qwerty"));

        Files.write(resourcePath.resolve("sample2.txt"),
                Arrays.asList("letmein", "password", "123456"));
    }

    @Test
    void loadFromResource_existingFile_returnsPasswords() {
        List<String> result = PasswordLoader.loadFromResource(TEST_RESOURCE_DIR + "sample1.txt");

        assertEquals(3, result.size());
        assertTrue(result.contains("123456"));
        assertTrue(result.contains("password"));
    }

    @Test
    void loadFromResource_missingFile_returnsEmptyList() {
        List<String> result = PasswordLoader.loadFromResource(TEST_RESOURCE_DIR + "nonexistent.txt");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void loadMultiple_mergesAndDeduplicates() {
        List<String> result = PasswordLoader.loadMultiple(Arrays.asList(
                TEST_RESOURCE_DIR + "sample1.txt",
                TEST_RESOURCE_DIR + "sample2.txt"
        ));

        assertEquals(4, result.size()); // 123456, password, qwerty, letmein
        assertTrue(result.contains("qwerty"));
        assertTrue(result.contains("letmein"));
    }

    @Test
    void loadMultiple_withEmptyList_returnsEmptyList() {
        List<String> result = PasswordLoader.loadMultiple(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
