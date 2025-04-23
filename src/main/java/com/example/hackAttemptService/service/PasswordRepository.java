package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> {
    List<Password> findByUserUsername(String username);
}
