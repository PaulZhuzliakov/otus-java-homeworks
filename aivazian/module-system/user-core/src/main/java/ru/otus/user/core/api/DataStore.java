package ru.otus.user.core.api;

import java.util.Optional;

public interface DataStore<T, ID> {
    T put(ID key, T value);
    T get(ID key);
    void delete(ID key);
    Optional<T> find(ID key);
    int size();
}
