package com.example.hackAttemptService.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PasswordLoader {

    public static List<String> loadFromResource(String filename) {
        try (InputStream is = PasswordLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                System.err.println("File not found: " + filename);
                return Collections.emptyList();
            }

            return new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Error loading file '" + filename + "': " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<String> loadMultiple(List<String> filenames) {
        return filenames.stream()
                .map(PasswordLoader::loadFromResource)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
