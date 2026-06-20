import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {
    // AES co kich thuoc khoi 16 byte; AES-128 cung yeu cau key 16 byte.
    public static final int AES_BLOCK_SIZE = 16;

    // Tao SecretKey tu chuoi nguoi dung nhap sau khi kiem tra do dai.
    public static SecretKey getSecretKey(String keyText) {
        byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length != AES_BLOCK_SIZE) {
            throw new IllegalArgumentException("Khóa AES-128 phải có đúng 16 byte.");
        }

        return new SecretKeySpec(keyBytes, "AES");
    }
}
