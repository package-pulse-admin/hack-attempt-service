package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.PasswordHistory;
import com.example.hackAttemptService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordHistoryRepository  extends JpaRepository<PasswordHistory, Long> {
}
