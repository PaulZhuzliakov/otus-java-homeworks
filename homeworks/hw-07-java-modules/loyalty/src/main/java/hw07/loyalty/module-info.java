module hw07.loyalty {
    exports hw07.loyalty;

    provides hw07.loyalty.LoyaltyService
            with hw07.loyalty.internal.RandomLoyaltyService;
}
