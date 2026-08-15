package org.example.memorydump.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Base64Util;
import org.example.memorydump.cache.UserCache;
import org.example.memorydump.entity.UserEntity;
import org.example.memorydump.exception.UserExistException;
import org.example.memorydump.exception.UserNotFoundException;
import org.example.memorydump.model.User;
import org.example.memorydump.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCache userCache;
    private final UserRepository userRepository;

    public User getById(Long id) {
        var cachedUser = userCache.get(id);
        if (cachedUser != null) {
            return cachedUser;
        }
        return userRepository.findById(id)
                .map(this::convert)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public User save(String login, String password) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new UserExistException("Пользователь с именем []" + login + " уже существует");
        }

        var userEntity = userRepository.save(new UserEntity(null, login, encodePassword(password)));
        var user = convert(userEntity);
        userCache.put(user);
        return user;
    }

    private String encodePassword(String password) {
        return Base64Util.encode(password);
    }

    private User convert(UserEntity entity) {
        return new User(entity.getId(), entity.getLogin(), entity.getEncodedPassword());
    }
}
