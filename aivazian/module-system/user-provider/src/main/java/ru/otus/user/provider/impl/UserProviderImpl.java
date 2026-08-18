package ru.otus.user.provider.impl;

import lombok.RequiredArgsConstructor;
import ru.otus.user.core.api.DataStore;
import ru.otus.user.provider.api.UserProvider;
import ru.otus.user.provider.entity.UserEntity;

import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class UserProviderImpl implements UserProvider {
    private final DataStore<UserEntity, Long> dataStore;
    private final Lock lock = new ReentrantLock();

    @Override
    public Optional<UserEntity> findById(Long id) {
        return dataStore.find(id);
    }

    @Override
    public UserEntity save(UserEntity entity) {
        try {
            lock.lock();
            if (entity.getId() == null) {
                Long id = dataStore.size() + 1L;
                return dataStore.put(id, entity.toBuilder().id(id).build());
            } else {
                return dataStore.put(entity.getId(), entity);
            }
        } finally {
            lock.unlock();
        }
    }
}
