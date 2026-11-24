package hw17;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    public long register(String login, String email, String age) {
        int ageValue = Integer.parseInt(age);
        return users.save(new User(login, email, ageValue)).getId();
    }

    public long registerWithExceptions(String login, String email, String age) {
        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            ageValue = 18;
        }
        return users.save(new User(login, email, ageValue)).getId();
    }

    public long registerWithExtraQueries(String login, String email, String age) {
        int ageValue = Integer.parseInt(age);
        if (users.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            throw new IllegalStateException("email already registered: " + email);
        }
        return users.save(new User(login, email, ageValue)).getId();
    }

    public synchronized long registerWithLock(String login, String email, String age) {
        int ageValue = Integer.parseInt(age);
        User saved = users.save(new User(login, email, ageValue));
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        return saved.getId();
    }
}
