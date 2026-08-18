package ru.otus.user.core.impl;

import ru.otus.user.core.api.DataStore;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDataStore<T, ID> implements DataStore<T, ID> {
    private final Map<ID, T> store = new ConcurrentHashMap<>();

    @Override
    public T put(ID key, T value) {
        store.put(key, value);
        return value;
    }

    @Override
    public T get(ID key) {
        return store.get(key);
    }

    @Override
    public void delete(ID key) {
        store.remove(key);
    }

    @Override
    public Optional<T> find(ID key) {
        return Optional.ofNullable(get(key));
    }

    @Override
    public int size() {
        return store.size();
    }
}
