package org.example.memorydump.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.memorydump.dto.UserCreateRequest;
import org.example.memorydump.dto.UserResponse;
import org.example.memorydump.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getById(Long id) {
        var user = userService.getById(id);
        return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        var user = userService.save(request.login(), request.password());
        return ResponseEntity.ok(new UserResponse(user.id(), user.login()));
    }

}
