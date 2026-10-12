package hw13;

import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository users;

    public RegistrationService(UserRepository users) {
        this.users = users;
    }

    // обычная регистрация: один save, дальше с ней сравниваю проблемные варианты
    public long register(String login, String email, String age) {
        int ageValue = Integer.parseInt(age);
        return users.save(new User(login, email, ageValue)).getId();
    }

    // возраст может прийти кривым с формы - ловлю и ставлю дефолтный
    public long registerWithExceptions(String login, String email, String age) {
        int ageValue;
        try {
            ageValue = Integer.parseInt(age);
        } catch (NumberFormatException e) {
            ageValue = 18;
        }
        return users.save(new User(login, email, ageValue)).getId();
    }

    // проверка на дубли: выгружаю всех пользователей и ищу email по памяти
    public long registerWithExtraQueries(String login, String email, String age) {
        int ageValue = Integer.parseInt(age);
        if (users.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email))) {
            throw new IllegalStateException("email already registered: " + email);
        }
        return users.save(new User(login, email, ageValue)).getId();
    }

    // synchronized, чтобы id не путались при параллельной регистрации.
    // внутри ещё "отправка письма" - ждём почтовый сервер, держа замок
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
