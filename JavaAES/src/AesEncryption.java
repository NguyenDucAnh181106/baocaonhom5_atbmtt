import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesEncryption {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String PAYLOAD_PREFIX = "AES:";

    // Ma hoa plaintext bang AES va tra ve ciphertext dang hex.
    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be empty.");
        }

        // Gan prefix va SHA-256 vao payload de phat hien sai khoa/du lieu bi sua khi giai ma.
        String payload = PAYLOAD_PREFIX + IntegrityUtils.sha256(plaintext) + ":" + plaintext;
        byte[] plaintextBytes = payload.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(plaintextBytes);
        return HexUtils.toHex(encryptedData);
    }
}
