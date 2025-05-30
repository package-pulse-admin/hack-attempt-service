package com.example.hackAttemptService.service.repositories;

import com.example.hackAttemptService.model.entity.PasswordHistory;
import com.example.hackAttemptService.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
        List<PasswordHistory> findAllByUser (User user);
}
