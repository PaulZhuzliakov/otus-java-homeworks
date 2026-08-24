package hw07.core.repository.memory;

import hw07.core.model.Client;
import hw07.core.repository.ClientRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// имитация базы данных: вместо таблицы коллекция, id ставит счётчик
public class InMemoryClientRepository implements ClientRepository {

    private final Map<Long, Client> clients = new HashMap<>();
    private long idCounter;

    @Override
    public Client save(Client client) {
        long id = ++idCounter;
        Client saved = new Client(id, client.name(), client.email(), client.cardNumber(),
                client.discountPercent());
        clients.put(id, saved);
        return saved;
    }

    @Override
    public List<Client> findAll() {
        return List.copyOf(clients.values());
    }
}
