package hu.fodor.genesys_interview.service;

import hu.fodor.genesys_interview.dto.CreateUserRequest;
import hu.fodor.genesys_interview.dto.LoginRequest;
import hu.fodor.genesys_interview.dto.UpdateUserRequest;
import hu.fodor.genesys_interview.dto.UserResponse;
import hu.fodor.genesys_interview.entity.User;
import hu.fodor.genesys_interview.exceptions.ConflictException;
import hu.fodor.genesys_interview.exceptions.InvalidCredentialsException;
import hu.fodor.genesys_interview.exceptions.ResourceNotFoundException;
import hu.fodor.genesys_interview.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserResponse create(CreateUserRequest req) {

        if (repo.existsByEmail(req.email())) {
            throw new ConflictException("Email already exists");
        }

        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(encoder.encode(req.password()))
                .build();

        return UserResponse.toDto(repo.save(user));
    }

    public UserResponse update(UUID id, UpdateUserRequest req) {

        User user = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getEmail().equals(req.email()) &&
                repo.existsByEmailAndIdNot(req.email(), id)) {
            throw new ConflictException("Email already exists");
        }

        user.setName(req.name());
        user.setEmail(req.email());
        if (req.password() != null && !req.password().isBlank()){

            user.setPassword(encoder.encode(req.password()));
        }

        return UserResponse.toDto(repo.save(user));
    }

    public void delete(UUID id) {
        User user = repo.findById(id)
                        .orElseThrow(()->new ResourceNotFoundException("User not found"));
        repo.delete(user);
    }

    public List<UserResponse> getAll() {
        return repo.findAll().stream().map(UserResponse::toDto).toList();
    }



    public UserResponse login(LoginRequest req) {

        User user = repo.findByEmail(req.email())
                .orElseThrow(()-> new InvalidCredentialsException("Invalid credentials"));

        if (!encoder.matches(req.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        user.setLastLogin(LocalDateTime.now());
        repo.save(user);

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLastLogin()
        );
    }

}
