package hw07.core.repository;

import hw07.core.model.Client;

import java.util.List;

public interface ClientRepository {

    Client save(Client client);

    List<Client> findAll();
}
