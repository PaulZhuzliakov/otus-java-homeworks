package org.example.memorydump.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.memorydump.exception.CommonBusinessException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

@Service
public class HashService {
    public String hashPassword(String password, String algorithmName) {
        try {
            Algorithm algorithm = Algorithm.findByName(algorithmName);
            MessageDigest digest = MessageDigest.getInstance(algorithm.getName());
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (Exception e) {
            throw new CommonBusinessException("Алгоритм не найден", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Getter
    @RequiredArgsConstructor
    public enum Algorithm {
        MD5("MD5"),
        SHA_256("SHA-256"),
        SHA_512("SHA-512");

        private final String name;

        public static Algorithm findByName(String name) {
            return Arrays.stream(values())
                    .filter(a -> a.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(String.format("Алгоритм=[%s] не известен", name)));
        }
    }
}
