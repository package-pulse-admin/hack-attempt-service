package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.LoginRequest;
import com.example.hackAttemptService.model.User;
import com.example.hackAttemptService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User byUsername = userService.findUserByUserName(request.getUsername());
        if (byUsername != null && byUsername.getPassword().equals(request.getPassword())) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PASSWORD");
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody User request) {
        userService.save(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
