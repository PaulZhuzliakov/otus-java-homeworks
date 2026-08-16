package ru.otus.user.service.api;

import ru.otus.user.service.model.User;

public interface UserService {
    User get(Long id);
    User save(User user);
}
