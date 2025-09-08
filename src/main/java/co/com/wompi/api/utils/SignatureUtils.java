package co.com.wompi.api.utils;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class SignatureUtils {
    public static String calcularFirmaIntegridad(String reference, int amountInCents, String currency,
                                                 String integrityKey) {
        try {
            String data = reference + amountInCents + currency + integrityKey;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}