package hu.fodor.genesys_interview.controllers;

import hu.fodor.genesys_interview.dto.*;
import hu.fodor.genesys_interview.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody CreateUserRequest req) {

        UserResponse user = service.create(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(@RequestBody LoginRequest req) {

        UserResponse user = service.login(req);

        return ResponseEntity
                .ok(ApiResponse.ok("Login successful", user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> userResponseList = service.getAll();
        return ResponseEntity.ok(ApiResponse.ok("Successful request",userResponseList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable UUID id,
                               @Valid @RequestBody UpdateUserRequest req) {
        UserResponse updated = service.update(id, req);
        return ResponseEntity.ok(ApiResponse.ok("Successfully updated",updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
