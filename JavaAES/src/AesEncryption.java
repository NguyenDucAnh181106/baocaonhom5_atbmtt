import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesEncryption {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String PAYLOAD_PREFIX = "AES:";

    /*
     * Ham encrypt()
     *
     * Chuc nang:
     * - Nhan plaintext can ma hoa
     * - Su dung khoa bi mat AES-128
     * - Ma hoa bang AES/ECB/PKCS5Padding
     * - Tra ve ciphertext o dang chuoi hex
     *
     * Luu y:
     * - PKCS5Padding tu them padding, nen plaintext khong can la boi so 16 byte
     */
    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new IllegalArgumentException("Plaintext cannot be empty.");
        }

        String payload = PAYLOAD_PREFIX + IntegrityUtils.sha256(plaintext) + ":" + plaintext;
        byte[] plaintextBytes = payload.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(plaintextBytes);
        return HexUtils.toHex(encryptedData);
    }
}
