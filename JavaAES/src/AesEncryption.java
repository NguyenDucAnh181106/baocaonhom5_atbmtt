import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesEncryption {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    // Dinh dang payload ben trong ma hoa: AES:<sha256_of_plaintext>:<plaintext>
    private static final String PAYLOAD_PREFIX = "AES:";

    // Dinh dang ban ma cuoi cung (chuoi hex tra ve):
    //   <keyHash(64 ky tu hex)><encryptedPayload(hex)>
    // keyHash nam ngoai phan ma hoa de AesDecryption co the kiem tra khoa
    // ma khong can giai ma thanh cong truoc.

    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        if (plaintext == null || plaintext.isEmpty()) {
            throw new IllegalArgumentException("Bản rõ không được để trống.");
        }

        String keyHash   = IntegrityUtils.sha256HexKey(secretKey);
        String plainHash = IntegrityUtils.sha256(plaintext);
        // Payload ma hoa: AES:<plainHash>:<plaintext>
        String payload = PAYLOAD_PREFIX + plainHash + ":" + plaintext;
        byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(payloadBytes);
        // Noi keyHash (khong ma hoa) vao truoc ciphertext hex.
        return keyHash + HexUtils.toHex(encryptedData);
    }
}
