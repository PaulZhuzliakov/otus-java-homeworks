package hw07.registration;

import hw07.core.model.Client;
import hw07.core.repository.ClientRepository;
import hw07.loyalty.LoyaltyCard;
import hw07.loyalty.LoyaltyService;

import java.util.List;
import java.util.ServiceLoader;

public class RegistrationService {

    private final LoyaltyService loyalty;
    private final ClientRepository repository;

    public RegistrationService(LoyaltyService loyalty, ClientRepository repository) {
        this.loyalty = loyalty;
        this.repository = repository;
    }

    public static RegistrationService fromServiceLoader() {
        return new RegistrationService(
                ServiceLoader.load(LoyaltyService.class).findFirst().orElseThrow(),
                ServiceLoader.load(ClientRepository.class).findFirst().orElseThrow());
    }

    public Client register(String name, String email) {
        if (name == null || email == null) {
            throw new IllegalArgumentException("в теле запроса нужны поля name и email");
        }
        LoyaltyCard card = loyalty.issueCard(name);
        Client saved = repository.save(new Client(0, name, email, card.cardNumber(), card.discountPercent()));
        System.out.println("registration: клиент " + saved.name() + " сохранён с id=" + saved.id());
        return saved;
    }

    public List<Client> findAll() {
        return repository.findAll();
    }
}
