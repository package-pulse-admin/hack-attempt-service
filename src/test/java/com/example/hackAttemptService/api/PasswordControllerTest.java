package com.example.hackAttemptService.api;

import com.example.hackAttemptService.model.Password;
import com.example.hackAttemptService.model.PasswordDTO;
import com.example.hackAttemptService.model.User;
import com.example.hackAttemptService.service.PasswordService;
import com.example.hackAttemptService.service.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PasswordControllerTest {

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PasswordController passwordController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("john");
    }

    @Test
    void getLibrary_returnsFilteredPasswords() {
        List<Password> expected = Collections.singletonList(
                new Password("123", "gmail", "work", user)
        );

        when(passwordService.getPasswordsByUsernameAndFilter("john", "gmail", "work"))
                .thenReturn(expected);

        ResponseEntity<List<Password>> response =
                passwordController.getLibrary("john", "gmail", "work");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(passwordService).getPasswordsByUsernameAndFilter("john", "gmail", "work");
    }

    @Test
    void addPassword_userFound_createsPassword() {
        PasswordDTO dto = new PasswordDTO("abc", "github", "personal");

        when(userRepository.findByUsername("john")).thenReturn(user);

        ResponseEntity<String> response = passwordController.addPassword("john", dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Password added successfully", response.getBody());

        Password saved = new Password(dto.getValue(), dto.getAppName(), dto.getPasswordLabel(), user);
        verify(passwordService).savePassword(refEq(saved, "id"));
    }

    @Test
    void addPassword_userNotFound_returns404() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        ResponseEntity<String> response =
                passwordController.addPassword("john", new PasswordDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(passwordService, never()).savePassword(any());
    }

    @Test
    void updatePassword_existingPassword_updatesAndSavesHistory() {
        PasswordDTO dto = new PasswordDTO("newVal", "gmail", "newLabel");

        Password existing = new Password("oldVal", "gmail", "oldLabel", user);
        existing.setId(10L);

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordService.findByUserAndAppName(user, "gmail")).thenReturn(existing);

        ResponseEntity<String> response =
                passwordController.updatePassword("john", dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully", response.getBody());

        // After update the same object should be mutated and saved
        assertEquals("newVal", existing.getPasswordValue());
        assertEquals("newLabel", existing.getPasswordLabel());

        verify(passwordService).savePassword(existing);
        verify(passwordService).saveHistory(argThat(h ->
                h.getUser().equals(user) &&
                        h.getPasswordValue().equals("oldVal")
        ));
    }

    @Test
    void updatePassword_userNotFound_returns404() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        ResponseEntity<String> response =
                passwordController.updatePassword("john", new PasswordDTO());

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(passwordService, never()).savePassword(any());
    }

    @Test
    void deletePassword_passwordFound_deletes() {
        Password p1 = new Password("val", "app", "label", user);
        p1.setId(99L);

        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordService.getPasswordsByUsername("john")).thenReturn(Collections.singletonList(p1));

        ResponseEntity<String> response =
                passwordController.deletePassword("john", 99L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password deleted successfully", response.getBody());
        verify(passwordService).deletePassword(p1);
    }

    @Test
    void deletePassword_passwordNotFound_returns404() {
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordService.getPasswordsByUsername("john")).thenReturn(Collections.emptyList());

        ResponseEntity<String> response =
                passwordController.deletePassword("john", 99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Password not found", response.getBody());
        verify(passwordService, never()).deletePassword(any());
    }

    @Test
    void deletePassword_userNotFound_returns404() {
        when(userRepository.findByUsername("john")).thenReturn(null);

        ResponseEntity<String> response =
                passwordController.deletePassword("john", 99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }
}
