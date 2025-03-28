package crypto;

import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Arrays;

public class KeyFromString {

    public static SecretKeySpec createKeyFromString(String keyString){
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(keyString.getBytes("UTF-8"));
            keyBytes = Arrays.copyOf(keyBytes, 32);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}