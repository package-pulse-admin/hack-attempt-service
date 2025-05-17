package com.example.hackAttemptService.services;

import com.example.hackAttemptService.model.Password;
import com.example.hackAttemptService.model.PasswordHistory;
import com.example.hackAttemptService.model.User;
import com.example.hackAttemptService.model.bruteForce.BruteForceRequest;
import com.example.hackAttemptService.model.bruteForce.BruteForceResult;
import com.example.hackAttemptService.service.PasswordLoader;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.repositories.PasswordHistoryRepository;
import com.example.hackAttemptService.service.repositories.PasswordRepository;
import com.example.hackAttemptService.service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;
    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        passwordService = new PasswordService(passwordRepository, passwordHistoryRepository, userRepository);
    }

    @Test
    void testFindByUser() {
        User user = new User();
        List<PasswordHistory> histories = List.of(new PasswordHistory());

        when(passwordHistoryRepository.findAllByUser(user)).thenReturn(histories);

        List<PasswordHistory> result = passwordService.findByUser(user);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByUserAndAppName() {
        User user = new User();
        Password password = new Password();

        when(passwordRepository.findByUserAndAppName(user, "App")).thenReturn(password);

        Password result = passwordService.findByUserAndAppName(user, "App");
        assertEquals(password, result);
    }

    @Test
    void testSavePassword() {
        Password password = new Password();
        passwordService.savePassword(password);
        verify(passwordRepository, times(1)).save(password);
    }

    @Test
    void testDeletePassword() {
        Password password = new Password();
        passwordService.deletePassword(password);
        verify(passwordRepository, times(1)).delete(password);
    }

    @Test
    void testSaveHistory() {
        PasswordHistory history = new PasswordHistory();
        passwordService.saveHistory(history);
        verify(passwordHistoryRepository, times(1)).save(history);
    }

    @Test
    void testGetPasswordsByUsernameAndFilter_userNotFound_throwsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);
        assertThrows(IllegalArgumentException.class,
                () -> passwordService.getPasswordsByUsernameAndFilter("unknown", null, null));
    }

    @Test
    void testGetPasswordsByUsername() {
        List<Password> passwords = List.of(new Password());
        when(passwordRepository.findByUserUsername("test")).thenReturn(passwords);

        List<Password> result = passwordService.getPasswordsByUsername("test");
        assertEquals(1, result.size());
    }

    @Test
    void testPerformSmartAttack_emptyDict_returnsFail() {
        BruteForceRequest request = new BruteForceRequest();
        request.setMaxAtt(5);

        List<String> emptyList = new ArrayList<>();
        try (MockedStatic<PasswordLoader> mocked = Mockito.mockStatic(PasswordLoader.class)) {
            mocked.when(() -> PasswordLoader.loadFromResource(anyString())).thenReturn(emptyList);

            BruteForceResult result = passwordService.performSmartAttack(request, 50);
            assertFalse(result.isSuccess());
            assertEquals(0, result.getAttempts());
        }
    }

    @Test
    void testPerformPureDictionaryAttack_emptyDict_returnsFail() {
        BruteForceRequest request = new BruteForceRequest();
        request.setMaxAtt(5);

        try (MockedStatic<PasswordLoader> mocked = Mockito.mockStatic(PasswordLoader.class)) {
            mocked.when(() -> PasswordLoader.loadMultiple(any())).thenReturn(Collections.emptyList());

            BruteForceResult result = passwordService.performPureDictionaryAttack(request);
            assertFalse(result.isSuccess());
            assertEquals(0, result.getAttempts());
        }
    }
}
