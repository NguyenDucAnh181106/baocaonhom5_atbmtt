import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
    /*
     * AES co kich thuoc khoi 16 byte.
     * AES-128 yeu cau khoa cung co do dai 16 byte.
     */
    public static final int AES_BLOCK_SIZE = 16;

    /*
     * Ham getSecretKey()
     *
     * Chuc nang:
     * - Nhan key do nguoi dung nhap
     * - Kiem tra key co dung 16 byte hay khong
     * - Tao SecretKey dung cho AES-128
     */
    public static SecretKey getSecretKey(String keyText) {
        byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != AES_BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be exactly 16 bytes for AES-128.");
        }

        return new SecretKeySpec(keyBytes, "AES");
    }
}
