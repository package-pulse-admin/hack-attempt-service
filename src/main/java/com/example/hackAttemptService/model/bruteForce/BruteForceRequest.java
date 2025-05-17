package com.example.hackAttemptService.model.bruteForce;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BruteForceRequest {
    private String username;
    private String targetUrl;
    private int maxAtt;
    private List<String> dictionary; // Optional: allow user to provide custom wordlist
}
