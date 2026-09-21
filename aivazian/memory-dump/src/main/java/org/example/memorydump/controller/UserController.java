package org.example.memorydump.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.memorydump.dto.UserCreateRequest;
import org.example.memorydump.dto.UserResponse;
import org.example.memorydump.exception.UserCreateException;
import org.example.memorydump.repository.UserRepository;
import org.example.memorydump.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final Object lock = new Object();

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<UserResponse> getById(Long id) {
        var user = userService.getById(id);
        return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        var user = userService.save(request.login(), request.password(), request.algorithm());
        return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
    }

    @PostMapping("/exception")
    public ResponseEntity<UserResponse> createWithException(@Valid @RequestBody UserCreateRequest request) {
        userService.save(request.login(), request.password(), request.algorithm());
        throw new UserCreateException("User created with exception");
    }

    @PostMapping("/lock")
    public ResponseEntity<UserResponse> createWithLock(@Valid @RequestBody UserCreateRequest request) {
        synchronized (lock) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new UserCreateException(e);
            }

            var user = userService.save(request.login(), request.password(), request.algorithm());
            return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
        }
    }

    @PostMapping("/extra")
    public ResponseEntity<UserResponse> createWithExtraRequest(@Valid @RequestBody UserCreateRequest request) {
        var user = userService.save(request.login(), request.password(), request.algorithm());
        for (int i = 0; i < 10; i++) {
            userRepository.findById(user.id());
        }
        return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
    }

}
