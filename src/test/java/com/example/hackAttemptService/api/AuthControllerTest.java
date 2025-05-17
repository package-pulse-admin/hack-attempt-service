package com.example.hackAttemptService.api;

import com.example.hackAttemptService.utils.JwtUtil;
import com.example.hackAttemptService.model.JwtResponse;
import com.example.hackAttemptService.model.LoginRequest;
import com.example.hackAttemptService.model.entity.User;
import com.example.hackAttemptService.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("testPass");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPass");

        when(userService.findUserByUserName("testUser")).thenReturn(user);
        when(jwtUtil.generateToken("testUser")).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertNotNull(jwtResponse);
        assertEquals("Bearer", jwtResponse.getTokenType());
        assertEquals("fake-jwt-token", jwtResponse.getToken());
    }

    @Test
    public void testLoginFailure_InvalidPassword() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testUser");
        loginRequest.setPassword("wrongPass");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("correctPass");

        when(userService.findUserByUserName("testUser")).thenReturn(user);

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid PASSWORD", response.getBody());
    }

    @Test
    public void testLoginFailure_UserNotFound() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonExistingUser");
        loginRequest.setPassword("any");

        when(userService.findUserByUserName("nonExistingUser")).thenReturn(null);

        ResponseEntity<?> response = authController.login(loginRequest);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid PASSWORD", response.getBody());
    }

    @Test
    public void testRegister() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("pass");

        ResponseEntity<Void> response = authController.register(newUser);

        verify(userService, times(1)).save(newUser);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
