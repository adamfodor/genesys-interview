package hu.fodor.genesys_interview.service;

import hu.fodor.genesys_interview.dto.*;
import hu.fodor.genesys_interview.entity.User;
import hu.fodor.genesys_interview.exceptions.InvalidCredentialsException;
import hu.fodor.genesys_interview.exceptions.ResourceNotFoundException;
import hu.fodor.genesys_interview.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {

        CreateUserRequest req = new CreateUserRequest(
                "John",
                "john@test.com",
                "password"
        );

        when(repo.existsByEmail(req.email())).thenReturn(false);
        when(encoder.encode(req.password())).thenReturn("hashed");

        User saved = User.builder()
                .id(UUID.randomUUID())
                .name(req.name())
                .email(req.email())
                .password("hashed")
                .build();

        when(repo.save(any(User.class))).thenReturn(saved);

        UserResponse res = userService.create(req);

        assertEquals("John", res.name());
        assertEquals("john@test.com", res.email());

        verify(repo).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailExists() {

        CreateUserRequest req = new CreateUserRequest(
                "John",
                "john@test.com",
                "password"
        );

        when(repo.existsByEmail(req.email())).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> userService.create(req));
    }

    @Test
    void shouldUpdateUser() {

        UUID id = UUID.randomUUID();

        User existing = User.builder()
                .id(id)
                .name("Old")
                .email("old@test.com")
                .password("oldpass")
                .build();

        UpdateUserRequest req = new UpdateUserRequest(
                "New Name",
                "new@test.com",
                "newpass"
        );

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.existsByEmailAndIdNot(req.email(), id)).thenReturn(false);
        when(encoder.encode(req.password())).thenReturn("hashed");

        User updated = User.builder()
                .id(id)
                .name(req.name())
                .email(req.email())
                .password("hashed")
                .build();

        when(repo.save(any(User.class))).thenReturn(updated);

        UserResponse res = userService.update(id, req);

        assertEquals("New Name", res.name());
        assertEquals("new@test.com", res.email());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnUpdate() {

        UUID id = UUID.randomUUID();

        UpdateUserRequest req = new UpdateUserRequest(
                "Name",
                "email@test.com",
                "pass"
        );

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(id, req));
    }

    @Test
    void shouldDeleteUser() {

        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(id)
                .name("John")
                .email("john@test.com")
                .password("pass")
                .build();

        when(repo.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(repo).delete(user);
    }
    @Test
    void shouldThrowWhenDeletingMissingUser() {

        UUID id = UUID.randomUUID();

        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.delete(id));
    }
    @Test
    void shouldReturnAllUsers() {

        List<User> users = List.of(
                User.builder().id(UUID.randomUUID()).name("A").email("a@test.com").build(),
                User.builder().id(UUID.randomUUID()).name("B").email("b@test.com").build()
        );

        when(repo.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest req = new LoginRequest("john@test.com", "pass");

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("John")
                .email(req.email())
                .password("hashed")
                .build();

        when(repo.findByEmail(req.email())).thenReturn(Optional.of(user));
        when(encoder.matches(req.password(), user.getPassword())).thenReturn(true);

        UserResponse res = userService.login(req);

        assertEquals("john@test.com", res.email());

        verify(repo).save(any(User.class)); // lastLogin update
    }
    @Test
    void shouldFailLoginInvalidPassword() {

        LoginRequest req = new LoginRequest("john@test.com", "wrong");

        User user = User.builder()
                .id(UUID.randomUUID())
                .email(req.email())
                .password("hashed")
                .build();

        when(repo.findByEmail(req.email())).thenReturn(Optional.of(user));
        when(encoder.matches(req.password(), user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> userService.login(req));
    }

}