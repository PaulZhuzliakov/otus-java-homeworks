package hw12;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RegistrationController {

    private final RegistrationService service;

    public RegistrationController(RegistrationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Mono<String> register(@RequestParam String login,
                                 @RequestParam String password,
                                 @RequestParam(required = false) String email) {
        return service.register(login, password, email).map(id -> "registered, id: " + id);
    }

    @GetMapping("/users")
    public Flux<User> users() {
        return service.allUsers();
    }

    @GetMapping("/users/names")
    public Mono<List<String>> names() {
        return service.allLogins().collectList();
    }

    @GetMapping("/users/emails")
    public Mono<List<String>> emails() {
        return service.filledEmails().collectList();
    }
}
