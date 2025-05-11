package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.Password;
import com.example.hackAttemptService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PasswordRepository extends JpaRepository<Password, Long> , JpaSpecificationExecutor<Password> {
    List<Password> findByUserUsername(String username);
    Password findByUserAndAppName(User user, String appName);

}
