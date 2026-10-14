package hw14;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register/exception")
    public String registerWithExceptions(@RequestParam String login, @RequestParam String email, @RequestParam String age) {
        return "registered, id: " + service.registerWithExceptions(login, email, age);
    }

    @PostMapping("/register/db")
    public String registerWithExtraQueries(@RequestParam String login, @RequestParam String email, @RequestParam String age) {
        try {
            return "registered, id: " + service.registerWithExtraQueries(login, email, age);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "rejected: " + e.getMessage();
        }
    }

    @PostMapping("/register/lock")
    public String registerWithLock(@RequestParam String login, @RequestParam String email, @RequestParam String age) {
        return "registered, id: " + service.registerWithLock(login, email, age);
    }

    @PostMapping("/register")
    public String register(@RequestParam String login, @RequestParam String email, @RequestParam String age) {
        return "registered, id: " + service.register(login, email, age);
    }
}
