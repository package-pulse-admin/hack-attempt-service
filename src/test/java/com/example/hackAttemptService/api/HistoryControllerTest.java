package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.entity.PasswordHistory;
import com.example.hackAttemptService.model.entity.User;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class HistoryControllerTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HistoryController historyController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getLibrary_userExists_returnsHistory() {
        String username = "john";
        User user = new User();
        user.setUsername(username);

        List<PasswordHistory> historyList = Arrays.asList(
                new PasswordHistory(),
                new PasswordHistory()
        );

        when(userRepository.findByUsername(username)).thenReturn(user);
        when(passwordService.findByUser(user)).thenReturn(historyList);

        ResponseEntity<List<PasswordHistory>> response = historyController.getLibrary(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(historyList, response.getBody());
        verify(userRepository).findByUsername(username);
        verify(passwordService).findByUser(user);
    }

    @Test
    void getLibrary_userNotFound_returns404() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(null);

        ResponseEntity<List<PasswordHistory>> response = historyController.getLibrary(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userRepository).findByUsername(username);
        verify(passwordService, never()).findByUser(any());
    }
}
