import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AesDecryption {
    /*
     * Ham decrypt()
     *
     * Chuc nang:
     * - Nhan ciphertext dang hex
     * - Su dung khoa bi mat AES-128
     * - Giai ma bang AES/ECB/NoPadding
     * - Tra ve plaintext ban dau
     *
     * Luu y:
     * - Thuat toan, mode va padding phai trung voi luc ma hoa
     */
    public static String decrypt(String ciphertextHex, SecretKey secretKey) throws Exception {
        byte[] encryptedData = HexUtils.fromHex(ciphertextHex);

        if (encryptedData.length == 0 || encryptedData.length % KeyManager.AES_BLOCK_SIZE != 0) {
            throw new IllegalArgumentException("Ciphertext length must be a positive multiple of 16 bytes.");
        }

        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedData = cipher.doFinal(encryptedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
