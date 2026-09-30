package org.example.memorydump.service;

import lombok.RequiredArgsConstructor;
import org.example.memorydump.cache.UserCache;
import org.example.memorydump.dto.UserCreateRequest;
import org.example.memorydump.entity.UserEntity;
import org.example.memorydump.exception.UserExistException;
import org.example.memorydump.exception.UserNotFoundException;
import org.example.memorydump.model.User;
import org.example.memorydump.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserCache userCache;
    private final UserRepository userRepository;
    private final HashService hashService;

    public Mono<User> getById(Long id) {
        return Mono.justOrEmpty(userCache.get(id))
                .switchIfEmpty(Mono.defer(() -> getByIdFromRepository(id)));
    }

    public Flux<User> findAllUsers() {
        return userRepository.findAll().map(this::convert);
    }

    public Mono<User> save(UserCreateRequest request) {
        return userRepository.findByLogin(request.login())
                .flatMap(existing -> Mono.<User>error(new UserExistException("Пользователь с именем " + request.login() + " уже существует")))
                .switchIfEmpty(Mono.defer(() ->
                        userRepository.save(createUserEntity(request))
                        .map(this::convert)
                        .doOnNext(userCache::put))
                );
    }

    private Mono<User> getByIdFromRepository(Long id) {
        return userRepository.findById(id)
                .map(this::convert)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("Пользователь с id=" + id + " не найден")));
    }

    private User convert(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .encodedPassword(entity.getEncodedPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .build();
    }

    private UserEntity createUserEntity(UserCreateRequest request) {
        String hashPassword = hashService.hashPassword(request.password(), request.algorithm());
        return UserEntity.builder()
                .login(request.login())
                .encodedPassword(hashPassword)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
    }
}
