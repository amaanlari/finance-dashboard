package io.zorvyn.task.financedashboard.service;
import io.zorvyn.task.financedashboard.dto.AuthResponse;
import io.zorvyn.task.financedashboard.dto.LoginRequest;
import io.zorvyn.task.financedashboard.dto.RegisterRequest;
import io.zorvyn.task.financedashboard.exception.ResourceAlreadyExistsException;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import io.zorvyn.task.financedashboard.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.VIEWER);
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("hashedPassword")
                .role(Role.VIEWER)
                .active(true)
                .build();
    }
    @Test
    void testRegisterUserSuccessfully() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt_token");
        AuthResponse response = authService.register(registerRequest);
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getToken()).isEqualTo("jwt_token");
        assertThat(response.getRole()).isEqualTo("VIEWER");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(user);
    }
    @Test
    void testRegisterWithExistingUsername() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Username already exists");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
    }
    @Test
    void testRegisterWithExistingEmail() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already exists");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }
    @Test
    void testLoginSuccessfully() {
        when(userRepository.findByUsername(loginRequest.getUsername()))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(user)).thenReturn("jwt_token");
        AuthResponse response = authService.login(loginRequest);
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getToken()).isEqualTo("jwt_token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtUtil).generateToken(user);
    }
    @Test
    void testLoginWithNonExistentUser() {
        when(userRepository.findByUsername(loginRequest.getUsername()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
    }
}
