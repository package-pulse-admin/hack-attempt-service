package com.example.hackAttemptService.service;

import com.example.hackAttemptService.model.User;
import com.example.hackAttemptService.service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findUserByUserName(String username){
        return userRepository.findByUsername(username);
    }

    public void save(User request) {
        userRepository.saveAndFlush(request);
    }
}
