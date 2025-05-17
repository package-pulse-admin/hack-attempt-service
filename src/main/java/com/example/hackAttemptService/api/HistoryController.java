package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.entity.PasswordHistory;
import com.example.hackAttemptService.model.entity.User;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<PasswordHistory>> getLibrary(@PathVariable String username) {

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<PasswordHistory> passwords = passwordService.findByUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(passwords);
    }
}
