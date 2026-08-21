package hw06;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    // регистрация: POST /register?login=...&password=...
    @PostMapping("/register")
    public String register(@RequestParam String login, @RequestParam String password) {
        return "registered, id: " + service.register(login, password);
    }
}
