package org.example.memorydump.service;

import lombok.RequiredArgsConstructor;
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
    private final HashService hashService;

    public User getById(Long id) {
        var cachedUser = userCache.get(id);
        if (cachedUser != null) {
            return cachedUser;
        }
        return userRepository.findById(id)
                .map(this::convert)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден"));
    }

    public User save(String login, String password, String hashAlgorithm) {
        if (userRepository.findByLogin(login).isPresent()) {
            throw new UserExistException("Пользователь с именем []" + login + " уже существует");
        }

        String hashPassword = hashService.hashPassword(password, hashAlgorithm);

        var userEntity = userRepository.save(new UserEntity(null, login, hashPassword));
        var user = convert(userEntity);
        userCache.put(user);
        return user;
    }

    private User convert(UserEntity entity) {
        return new User(entity.getId(), entity.getLogin(), entity.getEncodedPassword());
    }
}
