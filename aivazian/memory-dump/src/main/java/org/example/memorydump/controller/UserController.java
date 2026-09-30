package org.example.memorydump.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.memorydump.dto.UserCreateRequest;
import org.example.memorydump.dto.UserResponse;
import org.example.memorydump.model.User;
import org.example.memorydump.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Flux<UserResponse> getAll() {
        return userService.findAllUsers().map(UserResponse::from);
    }

    @GetMapping("/names")
    public Mono<List<String>> getUserNames() {
        return userService.findAllUsers()
                .map(user -> user.firstName() + " " + user.lastName())
                .collectList();
    }

    @GetMapping("/emails")
    public Mono<List<String>> getAllEmails() {
        return userService.findAllUsers()
                .map(User::email)
                .filter(StringUtils::hasText)
                .collectList();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getById(@PathVariable Long id) {
        return userService.getById(id)
                .map(UserResponse::from)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<UserResponse>> create(@Valid @RequestBody UserCreateRequest request) {
        return userService.save(request)
                .map(UserResponse::from)
                .map(ResponseEntity::ok);
    }
}
