import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import javax.crypto.SecretKey;

public class Main {
    // Dung chung Scanner cho toan bo chuong trinh.
    private static final Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    private static final int LINE_WIDTH = 72;

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
                    printSuccess("Program terminated. See you again!");
                    break;
                default:
                    printError("Invalid choice. Please choose 0, 1, or 2.");
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
        printBorder();
        printCentered("AES-128 ENCRYPTION TOOL");
        printCentered("ECB mode | PKCS5Padding | Hex output");
        printBorder();
        printMenuItem("1. Encrypt plaintext");
        printMenuItem("2. Decrypt ciphertext");
        printMenuItem("0. Exit");
        printBorder();
        System.out.print("Choose an option: ");
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
        printSection("ENCRYPT MODULE");

        String plaintext = readRequiredLine("Plaintext: ", "Plaintext cannot be empty.");
        SecretKey secretKey = readSecretKey();

        try {
            long startTime = System.nanoTime();
            String cipherText = AesEncryption.encrypt(plaintext, secretKey);
            long endTime = System.nanoTime();

            printResult("ENCRYPTION RESULT", "Ciphertext (hex)", cipherText, "Encryption time", (endTime - startTime) + " ns");
            saveResultIfRequested(cipherText);
        } catch (Exception e) {
            printError("Encryption failed. Reason: " + e.getMessage());
        }
    }

    /*
     * Chuc nang giai ma AES.
     */
    private static void decryptMenu() {
        System.out.println();
        printSection("DECRYPT MODULE");

        String cipherText = readRequiredLine("Ciphertext (hex): ", "Ciphertext cannot be empty.");
        SecretKey secretKey = readSecretKey();

        try {
            long startTime = System.nanoTime();
            String plainText = AesDecryption.decrypt(cipherText, secretKey);
            long endTime = System.nanoTime();

            printResult("DECRYPTION RESULT", "Plaintext", plainText, "Decryption time", (endTime - startTime) + " ns");
            saveResultIfRequested(plainText);
        } catch (Exception e) {
            printError("Decryption failed. Reason: " + e.getMessage());
        }
    }

    /*
     * Doc input bat buoc co noi dung.
     */
    private static String readRequiredLine(String prompt, String errorMessage) {
        while (true) {
            System.out.print("> " + prompt);
            String input = scanner.nextLine();

            if (!input.trim().isEmpty()) {
                return input;
            }

            printError(errorMessage);
        }
    }

    /*
     * Doc va kiem tra key truoc khi ma hoa/giai ma.
     */
    private static SecretKey readSecretKey() {
        while (true) {
            System.out.print("> Key (16 bytes): ");
            String keyText = scanner.nextLine();

            try {
                return KeyManager.getSecretKey(keyText);
            } catch (IllegalArgumentException e) {
                printError("Invalid key. " + e.getMessage());
            }
        }
    }

    /*
     * Luu ket qua ra file neu nguoi dung muon.
     */
    private static void saveResultIfRequested(String result) {
        System.out.print("> Save result to file? (y/n): ");
        String answer = scanner.nextLine().trim();

        if (!answer.equalsIgnoreCase("y")) {
            printSuccess("Result was not saved. Returning to main menu.");
            return;
        }

        String filePath = readRequiredLine("Output file path: ", "File path cannot be empty.");

        try {
            Path outputPath = Path.of(filePath);
            Path parent = outputPath.getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }

            Files.writeString(outputPath, result, StandardCharsets.UTF_8);
            printSuccess("Saved to: " + outputPath.toAbsolutePath());
        } catch (Exception e) {
            printError("Save failed. Reason: " + e.getMessage());
        }
    }

    private static void printSection(String title) {
        printBorder();
        printCentered(title);
        printBorder();
    }

    private static void printResult(String title, String valueLabel, String value, String timeLabel, String timeValue) {
        System.out.println();
        printSection(title);
        printWrappedLine(valueLabel + ": " + value);
        printWrappedLine(timeLabel + ": " + timeValue);
        printBorder();
    }

    private static void printSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    private static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    private static void printBorder() {
        System.out.println("+" + repeat("-", LINE_WIDTH - 2) + "+");
    }

    private static void printCentered(String text) {
        int contentWidth = LINE_WIDTH - 4;
        String clippedText = text.length() > contentWidth ? text.substring(0, contentWidth) : text;
        int leftPadding = (contentWidth - clippedText.length()) / 2;
        int rightPadding = contentWidth - clippedText.length() - leftPadding;
        System.out.println("| " + repeat(" ", leftPadding) + clippedText + repeat(" ", rightPadding) + " |");
    }

    private static void printWrappedLine(String text) {
        int contentWidth = LINE_WIDTH - 4;
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + contentWidth, text.length());
            String part = text.substring(start, end);
            System.out.println("| " + part + repeat(" ", contentWidth - part.length()) + " |");
            start = end;
        }
    }

    private static void printMenuItem(String text) {
        int contentWidth = LINE_WIDTH - 4;
        String item = "  " + text;
        System.out.println("| " + item + repeat(" ", contentWidth - item.length()) + " |");
    }

    private static String repeat(String text, int count) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            builder.append(text);
        }

        return builder.toString();
    }
}
