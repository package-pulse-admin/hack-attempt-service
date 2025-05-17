package com.example.hackAttemptService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private String value;
    private String appName;
    private String passwordLabel;
}
