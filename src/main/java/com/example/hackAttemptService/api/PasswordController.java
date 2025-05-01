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
import java.util.Optional;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<Password>> getLibrary (@PathVariable String username) {
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
