package com.example.hackAttemptService.api;

import com.example.hackAttemptService.api.config.JwtUtil;
import com.example.hackAttemptService.model.JwtResponse;
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
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.findUserByUserName(request.getUsername());
        if (user != null && user.getPassword().equals(request.getPassword())) {
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse("Bearer", token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid PASSWORD");
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody User request) {
        userService.save(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
