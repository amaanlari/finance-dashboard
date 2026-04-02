package io.zorvyn.task.financedashboard.service;
import io.zorvyn.task.financedashboard.dto.UpdateUserRequest;
import io.zorvyn.task.financedashboard.dto.UserResponse;
import io.zorvyn.task.financedashboard.exception.ResourceAlreadyExistsException;
import io.zorvyn.task.financedashboard.exception.ResourceNotFoundException;
import io.zorvyn.task.financedashboard.model.Role;
import io.zorvyn.task.financedashboard.model.User;
import io.zorvyn.task.financedashboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user1;
    private User user2;
    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .password("password")
                .role(Role.VIEWER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
        user2 = User.builder()
                .id(2L)
                .username("user2")
                .email("user2@example.com")
                .password("password")
                .role(Role.ANALYST)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<UserResponse> result = userService.getAllUsers();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
        assertThat(result.get(1).getUsername()).isEqualTo("user2");
        verify(userRepository).findAll();
    }
    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        UserResponse result = userService.getUserById(1L);
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("user1");
        assertThat(result.getEmail()).isEqualTo("user1@example.com");
        verify(userRepository).findById(1L);
    }
    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 999");
        verify(userRepository).findById(999L);
    }
    @Test
    void testUpdateUserSuccessfully() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updateduser");
        request.setEmail("updated@example.com");
        request.setRole(Role.ADMIN);
        request.setActive(true);
        User updatedUser = User.builder()
                .id(1L)
                .username("updateduser")
                .email("updated@example.com")
                .password("password")
                .role(Role.ADMIN)
                .active(true)
                .createdAt(user1.getCreatedAt())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("updateduser")).thenReturn(false);
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        UserResponse result = userService.updateUser(1L, request);
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("updateduser");
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getRole()).isEqualTo("ADMIN");
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
    @Test
    void testUpdateUserWithDuplicateUsername() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("user2");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("user2")).thenReturn(true);
        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Username already exists");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }
    @Test
    void testUpdateUserWithDuplicateEmail() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("user2@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByEmail("user2@example.com")).thenReturn(true);
        assertThatThrownBy(() -> userService.updateUser(1L, request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already exists");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }
    @Test
    void testDeleteUserSuccessfully() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }
    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: 999");
        verify(userRepository).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }
    @Test
    void testUpdateUserPartialUpdate() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("newusername");
        User updatedUser = User.builder()
                .id(1L)
                .username("newusername")
                .email("user1@example.com")
                .password("password")
                .role(Role.VIEWER)
                .active(true)
                .createdAt(user1.getCreatedAt())
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.existsByUsername("newusername")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        UserResponse result = userService.updateUser(1L, request);
        assertThat(result.getUsername()).isEqualTo("newusername");
        assertThat(result.getEmail()).isEqualTo("user1@example.com");
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }
}
