package hw16;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public String register(@RequestParam String login, @RequestParam String email, @RequestParam int age) {
        return "registered, id: " + service.register(login, email, age);
    }

    @GetMapping("/users")
    public List<String> users() {
        return service.users().stream()
                .map(u -> "id: " + u.getId() + ", email: " + u.getEmail())
                .toList();
    }
}
