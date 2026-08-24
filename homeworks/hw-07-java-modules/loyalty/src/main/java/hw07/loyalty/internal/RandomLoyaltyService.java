package hw07.loyalty.internal;

import hw07.loyalty.LoyaltyCard;
import hw07.loyalty.LoyaltyService;

import java.util.concurrent.ThreadLocalRandom;

public class RandomLoyaltyService implements LoyaltyService {

    @Override
    public LoyaltyCard issueCard(String clientName) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String cardNumber = "LOY-%06d".formatted(random.nextInt(1_000_000));
        int discountPercent = 5 * random.nextInt(1, 4); // 5, 10 или 15
        System.out.println("loyalty: карта " + cardNumber + " (" + discountPercent + "%) для " + clientName);
        return new LoyaltyCard(cardNumber, discountPercent);
    }
}
