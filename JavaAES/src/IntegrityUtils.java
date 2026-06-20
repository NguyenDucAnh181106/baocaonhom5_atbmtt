import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.SecretKey;

public class IntegrityUtils {
    // Tao chuoi SHA-256 dang hex de kiem tra tinh toan ven cua plaintext.
    public static String sha256(String text) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return HexUtils.toHex(hashBytes);
    }

    // Tao chuoi SHA-256 dang hex tu cac byte cua SecretKey de nhan dien khoa.
    public static String sha256HexKey(SecretKey secretKey) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(secretKey.getEncoded());
        return HexUtils.toHex(hashBytes);
    }
}
