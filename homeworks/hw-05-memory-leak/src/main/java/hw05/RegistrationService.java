package hw05;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    // кэш "профилей" пользователей: ключ - логин, значение - полмегабайта данных
    private final Map<String, byte[]> profileCache = new ConcurrentHashMap<>();

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    public long register(String login, String password) {
        profileCache.put(login, new byte[512 * 1024]);
        return users.save(new User(login, password)).getId();
    }
}
