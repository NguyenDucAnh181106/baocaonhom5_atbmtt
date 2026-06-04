import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesEncryption {
    /*
     * Ham encrypt()
     *
     * Chuc nang:
     * - Nhan plaintext can ma hoa
     * - Su dung khoa bi mat AES-128
     * - Ma hoa bang AES/ECB/NoPadding
     * - Tra ve ciphertext o dang chuoi hex
     *
     * Luu y:
     * - Vi dung NoPadding, plaintext phai co do dai byte la boi so cua 16
     */
    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);

        if (plaintextBytes.length == 0 || plaintextBytes.length % KeyManager.AES_BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Plaintext length must be a positive multiple of 16 bytes when using NoPadding.");
        }

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedData = cipher.doFinal(plaintextBytes);
        return HexUtils.toHex(encryptedData);
    }
}
