package org.example.memorydump.cache;

import lombok.extern.slf4j.Slf4j;
import org.example.memorydump.model.User;
import org.springframework.stereotype.Component;

import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class UserCache {
    private final Map<Long, User> cache = new ConcurrentHashMap<>();
    private final Map<Long, SoftReference<FakePayload>> fakePayLoadCache = new ConcurrentHashMap<>();

    public User get(Long id) {
        return cache.get(id);
    }

    public void put(User user) {
        cache.put(user.id(), user);
        fakePayLoadCache.put(user.id(), new SoftReference<>(new FakePayload()));
    }

    static class FakePayload {
        private final byte[] fakePayload;

        FakePayload() {
            fakePayload = new byte[1024 * 1024];
            Arrays.fill(fakePayload, (byte) 1);
        }
    }
}
