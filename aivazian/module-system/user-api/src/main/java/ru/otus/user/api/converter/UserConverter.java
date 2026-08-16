package ru.otus.user.api.converter;

import lombok.experimental.UtilityClass;
import ru.otus.user.api.dto.UserCreateRequest;
import ru.otus.user.api.dto.UserResponse;
import ru.otus.user.service.model.User;

@UtilityClass
public class UserConverter {
    public User convert(UserCreateRequest request) {
        return User.builder()
                .name(request.name())
                .lastname(request.lastname())
                .build();
    }

    public UserResponse convert(User user) {
        return UserResponse.builder()
                .id(user.id())
                .name(user.name())
                .lastname(user.lastname())
                .build();
    }
}
