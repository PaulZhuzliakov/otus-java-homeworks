package hw05;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    public long register(String login, String password) {
        // кэш профилей убрал: данные и так в h2, в памяти они только копились
        return users.save(new User(login, password)).getId();
    }
}
