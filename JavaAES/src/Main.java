import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class Main {
    // Dung chung Scanner cho toan bo chuong trinh.
    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);

    public static void main(String[] args) {
        int choice;

        do {
            showMenu();
            choice = getChoice();

            switch (choice) {
                case 1:
                    encryptMenu();
                    break;
                case 2:
                    decryptMenu();
                    break;
                case 0:
                    System.out.println("Program terminated.");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        } while (choice != 0);

        scanner.close();
    }

    /*
     * Hien thi menu chuc nang cua chuong trinh.
     */
    private static void showMenu() {
        System.out.println();
        System.out.println("===== AES-128 ECB NoPadding Program =====");
        System.out.println("1. Encrypt");
        System.out.println("2. Decrypt");
        System.out.println("0. Exit");
        System.out.print("Choose: ");
    }

    /*
     * Doc lua chon tu nguoi dung.
     * Neu nhap khong phai so nguyen thi tra ve -1.
     */
    private static int getChoice() {
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /*
     * Chuc nang ma hoa AES.
     */
    private static void encryptMenu() {
        System.out.println();
        System.out.println("[Encrypt Module]");

        System.out.print("Enter Plaintext: ");
        String plaintext = scanner.nextLine();

        System.out.print("Enter Key (16 bytes): ");
        String keyText = scanner.nextLine();

        try {
            SecretKey secretKey = KeyManager.getSecretKey(keyText);

            long startTime = System.nanoTime();
            String cipherText = AesEncryption.encrypt(plaintext, secretKey);
            long endTime = System.nanoTime();

            System.out.println();
            System.out.println("===== RESULT =====");
            System.out.println("Ciphertext (hex): " + cipherText);
            System.out.println("Encryption Time: " + (endTime - startTime) + " ns");
        } catch (Exception e) {
            System.out.println("Encryption failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    /*
     * Chuc nang giai ma AES.
     */
    private static void decryptMenu() {
        System.out.println();
        System.out.println("[Decrypt Module]");

        System.out.print("Enter Ciphertext (hex): ");
        String cipherText = scanner.nextLine();

        System.out.print("Enter Key (16 bytes): ");
        String keyText = scanner.nextLine();

        try {
            SecretKey secretKey = KeyManager.getSecretKey(keyText);

            long startTime = System.nanoTime();
            String plainText = AesDecryption.decrypt(cipherText, secretKey);
            long endTime = System.nanoTime();

            System.out.println();
            System.out.println("===== RESULT =====");
            System.out.println("Plaintext: " + plainText);
            System.out.println("Decryption Time: " + (endTime - startTime) + " ns");
        } catch (Exception e) {
            System.out.println("Decryption failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
