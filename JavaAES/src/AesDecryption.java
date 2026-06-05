import java.nio.charset.StandardCharsets;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

public class AesDecryption {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String PAYLOAD_PREFIX = "AES:";
    private static final int SHA256_HEX_LENGTH = 64;
    private static final String INTEGRITY_ERROR_MESSAGE = "Du lieu da bi thay doi hoac khoa khong chinh xac!";

    // Giai ma ciphertext hex va kiem tra payload truoc khi tra plaintext.
    public static String decrypt(String ciphertextHex, SecretKey secretKey) throws Exception {
        byte[] encryptedData = HexUtils.fromHex(ciphertextHex);

        if (encryptedData.length == 0 || encryptedData.length % KeyManager.AES_BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a positive multiple of 16 bytes.");
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedData;
        try {
            decryptedData = cipher.doFinal(encryptedData);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // Sai khoa hoac ciphertext bi sua thuong lam padding khong hop le.
            throw new Exception(INTEGRITY_ERROR_MESSAGE);
        }
        String payload = new String(decryptedData, StandardCharsets.UTF_8);

        // Payload hop le phai co dang AES:<SHA-256-hex>:<plaintext>.
        if (!payload.startsWith(PAYLOAD_PREFIX)) {
            throw new Exception(INTEGRITY_ERROR_MESSAGE);
        }

        int hashStart = PAYLOAD_PREFIX.length();
        int hashEnd = hashStart + SHA256_HEX_LENGTH;

        if (payload.length() <= hashEnd || payload.charAt(hashEnd) != ':') {
            throw new Exception(INTEGRITY_ERROR_MESSAGE);
        }

        String expectedHash = payload.substring(hashStart, hashEnd);
        String plainText = payload.substring(hashEnd + 1);
        String actualHash = IntegrityUtils.sha256(plainText);

        // So sanh hash de phat hien plaintext sau giai ma da bi thay doi.
        if (!expectedHash.equals(actualHash)) {
            throw new Exception(INTEGRITY_ERROR_MESSAGE);
        }

        return plainText;
    }
}
