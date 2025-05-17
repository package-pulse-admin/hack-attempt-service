package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.bruteForce.BruteForceRequest;
import com.example.hackAttemptService.model.bruteForce.BruteForceResult;
import com.example.hackAttemptService.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brute")
@RequiredArgsConstructor
public class BruteForceController {

    private final PasswordService passwordService;

    @PostMapping("/smart-attack")
    public ResponseEntity<BruteForceResult> smartAttack (@RequestBody BruteForceRequest bruteForceRequest) {
        BruteForceResult result = passwordService.performSmartAttack(bruteForceRequest, 20);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/attack")
    public ResponseEntity<BruteForceResult> performPureDictionaryAttack (@RequestBody BruteForceRequest bruteForceRequest) {
        BruteForceResult result = passwordService.performPureDictionaryAttack(bruteForceRequest);
        return ResponseEntity.ok(result);
    }

}
