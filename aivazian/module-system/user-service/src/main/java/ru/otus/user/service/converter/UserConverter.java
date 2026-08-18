package ru.otus.user.service.converter;

import lombok.experimental.UtilityClass;
import ru.otus.user.provider.entity.UserEntity;
import ru.otus.user.service.model.User;

@UtilityClass
public class UserConverter {
    public User convert(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .lastname(entity.getLastname())
                .build();
    }
    public UserEntity convert(User user) {
        return UserEntity.builder()
                .id(user.id())
                .name(user.name())
                .lastname(user.lastname())
                .build();
    }
}
