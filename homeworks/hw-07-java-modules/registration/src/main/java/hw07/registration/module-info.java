module hw07.registration {
    requires transitive hw07.core;
    requires hw07.loyalty;

    uses hw07.loyalty.LoyaltyService;
    uses hw07.core.repository.ClientRepository;

    exports hw07.registration;
}
