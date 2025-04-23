package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.Password;
import com.example.hackAttemptService.model.PasswordDTO;
import com.example.hackAttemptService.model.User;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<Password>> register(@PathVariable String username) {
        List<Password> passwords = passwordService.getPasswordsByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).body(passwords);
    }

    @PostMapping("/{username}")
    public ResponseEntity<String> addPassword(@PathVariable String username, @RequestBody PasswordDTO passwordDto) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Password password = new Password(passwordDto.getValue(), user);
        passwordService.savePassword(password);
        return ResponseEntity.status(HttpStatus.CREATED).body("Password added successfully");
    }
}
