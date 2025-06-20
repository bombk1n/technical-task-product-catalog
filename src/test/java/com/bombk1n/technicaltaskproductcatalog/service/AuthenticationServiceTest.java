package com.bombk1n.technicaltaskproductcatalog.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bombk1n.technicaltaskproductcatalog.model.User;
import com.bombk1n.technicaltaskproductcatalog.model.UserRole;
import com.bombk1n.technicaltaskproductcatalog.exception.UsernameAlreadyExistsException;
import com.bombk1n.technicaltaskproductcatalog.dto.AuthRequest;
import com.bombk1n.technicaltaskproductcatalog.dto.AuthResponse;
import com.bombk1n.technicaltaskproductcatalog.repository.UserRepository;
import com.bombk1n.technicaltaskproductcatalog.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> authTokenCaptor;

    private AuthRequest authRequest;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "encodedPassword123";
    private static final String JWT_TOKEN = "jwt.token.here";

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest(TEST_USERNAME, TEST_PASSWORD);
    }

    @Test
    @DisplayName("Register should create a new user when username does not exist")
    void register_WithNewUsername_ShouldCreateUser() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(TEST_USERNAME)).thenReturn(JWT_TOKEN);

        AuthResponse response = authenticationService.register(authRequest);

        assertNotNull(response);
        assertEquals(JWT_TOKEN, response.getToken());

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(TEST_USERNAME, savedUser.getUsername());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertEquals(Set.of(UserRole.USER), savedUser.getRoles());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Register should throw exception when username already exists")
    void register_WithExistingUsername_ShouldThrowException() {
        User existingUser = new User();
        existingUser.setUsername(TEST_USERNAME);
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(existingUser));

        UsernameAlreadyExistsException exception = assertThrows(
                UsernameAlreadyExistsException.class,
                () -> authenticationService.register(authRequest)
        );
        assertEquals("Username: " + TEST_USERNAME + " already exists", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(authenticationManager, never()).authenticate(any());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Login should authenticate user and return token")
    void login_WithValidCredentials_ShouldReturnToken() {
        when(jwtService.generateToken(TEST_USERNAME)).thenReturn(JWT_TOKEN);

        AuthResponse response = authenticationService.login(authRequest);

        assertNotNull(response);
        assertEquals(JWT_TOKEN, response.getToken());

        verify(authenticationManager).authenticate(authTokenCaptor.capture());
        UsernamePasswordAuthenticationToken capturedToken = authTokenCaptor.getValue();
        assertEquals(TEST_USERNAME, capturedToken.getPrincipal());
        assertEquals(TEST_PASSWORD, capturedToken.getCredentials());

        verify(jwtService).generateToken(TEST_USERNAME);
    }

    @Test
    @DisplayName("Login should throw exception when credentials are invalid")
    void login_WithInvalidCredentials_ShouldThrowException() {
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(authRequest)
        );
        assertEquals("Bad credentials", exception.getMessage());

        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("Register should correctly encode password")
    void register_ShouldEncodePassword() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(anyString())).thenReturn(JWT_TOKEN);

        authenticationService.register(authRequest);

        verify(passwordEncoder).encode(TEST_PASSWORD);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
    }

    @Test
    @DisplayName("Register should set USER role")
    void register_ShouldSetUserRole() {
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(jwtService.generateToken(anyString())).thenReturn(JWT_TOKEN);

        authenticationService.register(authRequest);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().contains(UserRole.USER));
    }
}