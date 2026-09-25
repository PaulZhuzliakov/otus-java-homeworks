package hw09;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class PasswordHasher {

    public static String hash(String password, String algorithm, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = password.getBytes(StandardCharsets.UTF_8);
            for (int i = 0; i < iterations; i++) {
                hash = digest.digest(hash);
            }
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("нет алгоритма " + algorithm, e);
        }
    }
}
