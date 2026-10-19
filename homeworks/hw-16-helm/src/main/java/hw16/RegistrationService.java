package hw16;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    public long register(String login, String email, int age) {
        return users.save(new User(login, email, age)).getId();
    }

    public List<User> users() {
        return users.findAll();
    }
}
