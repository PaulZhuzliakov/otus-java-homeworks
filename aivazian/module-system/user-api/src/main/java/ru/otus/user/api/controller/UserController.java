package ru.otus.user.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.user.api.converter.UserConverter;
import ru.otus.user.api.dto.UserCreateRequest;
import ru.otus.user.api.dto.UserResponse;
import ru.otus.user.service.api.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getById(Long id) {
        var user = userService.get(id);
        return ResponseEntity.ok(UserConverter.convert(user));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserCreateRequest request) {
        var user = userService.save(UserConverter.convert(request));
        return ResponseEntity.ok(UserConverter.convert(user));
    }

}
