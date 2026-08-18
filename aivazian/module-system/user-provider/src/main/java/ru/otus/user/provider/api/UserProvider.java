package ru.otus.user.provider.api;

import ru.otus.user.provider.entity.UserEntity;

import java.util.Optional;

public interface UserProvider {
    Optional<UserEntity> findById(Long id);
    UserEntity save(UserEntity entity);
}
