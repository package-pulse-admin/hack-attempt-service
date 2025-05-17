package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.entity.Password;
import com.example.hackAttemptService.model.PasswordDTO;
import com.example.hackAttemptService.model.entity.PasswordHistory;
import com.example.hackAttemptService.model.entity.User;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<Password>> getLibrary(@PathVariable String username,
                                                     @RequestParam(required = false) String appName,
                                                     @RequestParam(required = false) String passwordLabel) {
        List<Password> passwords = passwordService.getPasswordsByUsernameAndFilter(username, appName, passwordLabel);
        return ResponseEntity.status(HttpStatus.OK).body(passwords);
    }

    @PostMapping("/{username}")
    public ResponseEntity<String> addPassword(@PathVariable String username, @RequestBody PasswordDTO passwordDto) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Password password = new Password(passwordDto.getValue(),passwordDto.getAppName(), passwordDto.getPasswordLabel(), user);
        passwordService.savePassword(password);
        return ResponseEntity.status(HttpStatus.CREATED).body("Password added successfully");
    }

    @PutMapping("/{username}")
    public ResponseEntity<String> updatePassword(@PathVariable String username, @RequestBody PasswordDTO passwordDto) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        Password currentPassword = passwordService.findByUserAndAppName(user, passwordDto.getAppName());
        if (currentPassword == null) {
            currentPassword = new Password(passwordDto.getValue(), passwordDto.getAppName(), passwordDto.getPasswordLabel(), user);
        }

        PasswordHistory passwordHistory = new PasswordHistory(
                currentPassword,
                user,
                currentPassword.getPasswordValue(),
                LocalDateTime.now()
        );
        passwordService.saveHistory(passwordHistory);

        currentPassword.setPasswordValue(passwordDto.getValue());
        currentPassword.setPasswordLabel(passwordDto.getPasswordLabel());
        passwordService.savePassword(currentPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Password updated successfully");
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deletePassword(@PathVariable String username, @RequestParam Long passId) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Password> passwords = passwordService.getPasswordsByUsername(username);

        Optional<Password> passwordToDelete = passwords.stream()
                .filter(password -> password.getId().equals(passId))
                .findFirst();

        if (!passwordToDelete.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Password not found");
        }

        passwordService.deletePassword(passwordToDelete.get());

        return ResponseEntity.status(HttpStatus.OK).body("Password deleted successfully");
    }
}
