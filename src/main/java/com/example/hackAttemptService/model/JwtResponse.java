package com.example.hackAttemptService.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String tokenType;
    private String token;
}
