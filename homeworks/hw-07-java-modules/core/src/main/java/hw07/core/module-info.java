module hw07.core {
    exports hw07.core.model;
    exports hw07.core.repository;

    provides hw07.core.repository.ClientRepository
            with hw07.core.repository.memory.InMemoryClientRepository;
}
