public class HexUtils {
    // Chuyen mang byte thanh chuoi hex in hoa.
    public static String toHex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);

        for (byte value : data) {
            hex.append(String.format("%02X", value & 0xFF));
        }

        return hex.toString();
    }

    // Chuyen chuoi hex ve mang byte va bao loi neu hex khong hop le.
    public static byte[] fromHex(String hexText) {
        String normalizedHex = hexText.replaceAll("\\s+", "");

        if (normalizedHex.length() == 0 || normalizedHex.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex ciphertext must contain an even number of characters.");
        }

        byte[] data = new byte[normalizedHex.length() / 2];

        for (int i = 0; i < normalizedHex.length(); i += 2) {
            int high = Character.digit(normalizedHex.charAt(i), 16);
            int low = Character.digit(normalizedHex.charAt(i + 1), 16);

            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Hex ciphertext contains invalid characters.");
            }

            data[i / 2] = (byte) ((high << 4) + low);
        }

        return data;
    }
}
