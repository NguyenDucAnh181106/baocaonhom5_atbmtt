import java.nio.charset.StandardCharsets;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

public class AesDecryption {
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String PAYLOAD_PREFIX = "AES:";
    // SHA-256 hex = 64 ky tu
    private static final int SHA256_HEX_LENGTH = 64;

    // 3 thong bao loi phan biet ro nguon goc thay doi.
    static final String ERR_BOTH   = "Cả bản mã và khóa đã bị thay đổi.";
    static final String ERR_KEY    = "Khóa đã bị thay đổi.";
    static final String ERR_CIPHER = "Bản mã đã bị thay đổi.";

    public static String decrypt(String ciphertextFull, SecretKey secretKey) throws Exception {
        // Ban ma day du = <keyHash(64 hex)><encryptedPayload(hex)>
        // keyHash nam ngoai phan ma hoa => co the kiem tra khoa truoc.
        if (ciphertextFull == null || ciphertextFull.length() <= SHA256_HEX_LENGTH) {
            throw new Exception(ERR_BOTH);
        }

        String embeddedKeyHash  = ciphertextFull.substring(0, SHA256_HEX_LENGTH);
        String encryptedHex     = ciphertextFull.substring(SHA256_HEX_LENGTH);

        boolean keyChanged = !embeddedKeyHash.equals(IntegrityUtils.sha256HexKey(secretKey));

        // Giai ma phan payload.
        byte[] encryptedData;
        try {
            encryptedData = HexUtils.fromHex(encryptedHex);
        } catch (Exception e) {
            // Hex khong hop le => ban ma bi sua.
            throw new Exception(keyChanged ? ERR_BOTH : ERR_CIPHER);
        }

        if (encryptedData.length == 0 || encryptedData.length % KeyManager.AES_BLOCK_SIZE != 0) {
            throw new Exception(keyChanged ? ERR_BOTH : ERR_CIPHER);
        }

        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] decryptedData;
        try {
            decryptedData = cipher.doFinal(encryptedData);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            // Padding loi: neu khoa sai thi la do khoa, neu khoa dung thi ban ma bi sua.
            throw new Exception(keyChanged ? ERR_KEY : ERR_CIPHER);
        }

        String payload = new String(decryptedData, StandardCharsets.UTF_8);

        // Cau truc payload: AES:<plainHash(64)>:<plaintext>
        int plainHashStart = PAYLOAD_PREFIX.length();
        int plainHashEnd   = plainHashStart + SHA256_HEX_LENGTH;

        if (!payload.startsWith(PAYLOAD_PREFIX)
                || payload.length() <= plainHashEnd
                || payload.charAt(plainHashEnd) != ':') {
            throw new Exception(keyChanged ? ERR_BOTH : ERR_CIPHER);
        }

        String embeddedPlainHash = payload.substring(plainHashStart, plainHashEnd);
        String plainText         = payload.substring(plainHashEnd + 1);

        boolean cipherChanged = !embeddedPlainHash.equals(IntegrityUtils.sha256(plainText));

        if (keyChanged && cipherChanged) throw new Exception(ERR_BOTH);
        if (keyChanged)                  throw new Exception(ERR_KEY);
        if (cipherChanged)               throw new Exception(ERR_CIPHER);

        return plainText;
    }
}
