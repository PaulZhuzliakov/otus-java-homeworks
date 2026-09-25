package hw12;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RegistrationService {

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    public Mono<Long> register(String login, String password, String email) {
        return users.save(new User(login, password, email)).map(User::getId);
    }

    public Flux<User> allUsers() {
        return users.findAll();
    }

    public Flux<String> allLogins() {
        return users.findAll().map(User::getLogin);
    }

    public Flux<String> filledEmails() {
        return users.findAll()
                .filter(user -> user.getEmail() != null && !user.getEmail().isBlank())
                .map(User::getEmail);
    }
}
