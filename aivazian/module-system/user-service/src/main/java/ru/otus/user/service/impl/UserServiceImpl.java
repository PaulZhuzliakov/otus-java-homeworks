package ru.otus.user.service.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.user.provider.api.UserProvider;
import ru.otus.user.provider.entity.UserEntity;
import ru.otus.user.service.api.UserService;
import ru.otus.user.service.converter.UserConverter;
import ru.otus.user.service.exception.UserNotFoundException;
import ru.otus.user.service.model.User;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserProvider  userProvider;

    @Override
    public User get(Long id) {
        return userProvider.findById(id)
                .map(UserConverter::convert)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь[id=%s] не найден", id)));
    }

    @Override
    public User save(User user) {
        var entity = userProvider.save(UserConverter.convert(user));
        return UserConverter.convert(entity);
    }
}
