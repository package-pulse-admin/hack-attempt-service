package com.example.hackAttemptService.model.bruteForce;

import lombok.*;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class BruteForceResult {
    private boolean success;
    private String correctPassword;
    private int attempts;
    private long timeTakenMillis;
}
