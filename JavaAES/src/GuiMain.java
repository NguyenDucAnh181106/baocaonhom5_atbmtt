import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import javax.crypto.SecretKey;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GuiMain extends JFrame {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String KEY_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Color BACKGROUND = new Color(230, 242, 255);
    private static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    private static final Color KEY_PANEL_BACKGROUND = new Color(238, 252, 246);
    private static final Color WORK_PANEL_BACKGROUND = new Color(246, 249, 255);
    private static final Color FIELD_BACKGROUND = new Color(252, 254, 255);
    private static final Color BORDER = new Color(159, 190, 232);
    private static final Color TITLE_TEXT = new Color(19, 52, 96);
    private static final Color LABEL_TEXT = new Color(28, 55, 85);
    private static final Color MUTED_TEXT = new Color(76, 92, 112);
    private static final Color BUTTON_TEXT = Color.BLACK;
    private static final Color PRIMARY = new Color(119, 211, 255);
    private static final Color SECONDARY_BUTTON = new Color(255, 248, 214);
    private static final Color SUCCESS = new Color(22, 128, 89);
    private static final Color DANGER = new Color(190, 64, 77);

    private final JComboBox<String> keySizeBox = new JComboBox<>(new String[] {"128 bit"});
    private final JTextField keyField = new JTextField();
    private final JTextArea plainEncryptArea = new JTextArea();
    private final JTextArea cipherEncryptArea = new JTextArea();
    private final JTextArea cipherDecryptArea = new JTextArea();
    private final JTextArea plainDecryptArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Sẵn sàng.");
    private JButton copyCipherButton;
    private JTabbedPane workTabs;

    private SecretKey activeKey;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Giữ giao diện mặc định nếu không tải được giao diện hệ điều hành.
            }

            configureVietnameseUiTexts();
            new GuiMain().setVisible(true);
        });
    }

    private static void configureVietnameseUiTexts() {
        UIManager.put("FileChooser.openDialogTitleText", "Mở file");
        UIManager.put("FileChooser.saveDialogTitleText", "Lưu file");
        UIManager.put("FileChooser.openButtonText", "Mở");
        UIManager.put("FileChooser.saveButtonText", "Lưu");
        UIManager.put("FileChooser.cancelButtonText", "Hủy");
        UIManager.put("FileChooser.fileNameLabelText", "Tên file:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Loại file:");
        UIManager.put("FileChooser.lookInLabelText", "Thư mục:");
        UIManager.put("FileChooser.saveInLabelText", "Lưu tại:");
        UIManager.put("FileChooser.upFolderToolTipText", "Lên thư mục cha");
        UIManager.put("FileChooser.homeFolderToolTipText", "Thư mục chính");
        UIManager.put("FileChooser.newFolderToolTipText", "Tạo thư mục mới");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Xem dạng danh sách");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Xem chi tiết");
        UIManager.put("FileChooser.acceptAllFileFilterText", "Tất cả file");
        UIManager.put("OptionPane.okButtonText", "Đồng ý");
        UIManager.put("OptionPane.cancelButtonText", "Hủy");
    }

    public GuiMain() {
        super("AES Nhóm 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1040, 660));

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(14, 14, 10, 14));
        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createWorkspace(), BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        generateKey();
        activeKey = KeyManager.getSecretKey(keyField.getText());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 4));
        header.setOpaque(false);

        JLabel title = new JLabel("Công cụ mã hóa AES-128");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TITLE_TEXT);

        JLabel subtitle = new JLabel("Mã hóa và giải mã văn bản hoặc nội dung file bằng khóa 16 byte.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(MUTED_TEXT);

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createWorkspace() {
        JPanel workspace = new JPanel(new BorderLayout(14, 0));
        workspace.setOpaque(false);
        workspace.add(createKeyPanel(), BorderLayout.WEST);
        workspace.add(createTabbedWorkPanel(), BorderLayout.CENTER);
        return workspace;
    }

    private JPanel createKeyPanel() {
        JPanel panel = createSurfacePanel(new Dimension(285, 0));
        panel.setBackground(KEY_PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = baseConstraints();
        addSectionTitle(panel, "Thiết lập khóa", gbc, 0);
        addLabel(panel, "Độ dài khóa", gbc, 1);
        keySizeBox.setBackground(FIELD_BACKGROUND);
        keySizeBox.setForeground(Color.BLACK);
        addFull(panel, keySizeBox, gbc, 2, 0);
        addLabel(panel, "Khóa AES", gbc, 3);
        keyField.setBackground(FIELD_BACKGROUND);
        keyField.setForeground(Color.BLACK);
        keyField.setCaretColor(TITLE_TEXT);
        keyField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(134, 170, 214)),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        addFull(panel, keyField, gbc, 4, 0);

        addButton(panel, "Sinh khóa tự động", this::generateKey, true, gbc, 5);
        addButton(panel, "Xác nhận khóa", this::confirmKey, false, gbc, 6);

        addSeparator(panel, gbc, 7);
        addButton(panel, "Mở khóa từ file", this::openKey, false, gbc, 8);
        addButton(panel, "Lưu khóa ra file", this::saveKey, false, gbc, 9);
        addButton(panel, "Làm mới tất cả", this::clearAll, false, gbc, 10);

        JLabel note = new JLabel("<html>Khóa AES-128 phải có đúng 16 byte. Với ký tự tiếng Việt, số byte có thể khác số ký tự.</html>");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        note.setForeground(MUTED_TEXT);
        addFull(panel, note, gbc, 11, 0);

        gbc.gridy = 12;
        gbc.weighty = 1;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        panel.add(spacer, gbc);
        return panel;
    }

    private JTabbedPane createTabbedWorkPanel() {
        workTabs = new JTabbedPane();
        workTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        workTabs.setBackground(new Color(218, 237, 255));
        workTabs.setForeground(TITLE_TEXT);
        workTabs.addTab("Mã hóa", createEncryptTab());
        workTabs.addTab("Giải mã", createDecryptTab());
        return workTabs;
    }

    private JPanel createEncryptTab() {
        JPanel panel = createSurfacePanel(null);
        panel.setBackground(WORK_PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        addSectionTitle(panel, "Mã hóa bản rõ", gbc, 0);
        addTextBlock(panel, "Bản rõ", plainEncryptArea, gbc, 1);
        addButtonRow(panel, gbc, 2,
                new ActionButton("Mở file bản rõ", () -> openTextFile(plainEncryptArea)),
                new ActionButton("Lưu bản rõ", () -> saveTextFile(plainEncryptArea, "ban_ro.txt")),
                new ActionButton("Mã hóa", this::encryptText));

        addTextBlock(panel, "Bản mã hex", cipherEncryptArea, gbc, 3);

        // Tao nut "Chuyen sang giai ma", disabled cho den khi ma hoa thanh cong.
        copyCipherButton = createButton("Chuyển sang giải mã", this::copyCipherToDecrypt, false);
        copyCipherButton.setEnabled(false);
        JButton saveButton = createButton("Lưu bản mã", () -> saveTextFile(cipherEncryptArea, "ban_ma.txt"), false);

        // Them truc tiep vao panel theo hang, khong qua ActionButton wrapper.
        JPanel row4 = new JPanel(new GridBagLayout());
        row4.setOpaque(false);
        GridBagConstraints bc0 = new GridBagConstraints();
        bc0.gridx = 0; bc0.gridy = 0; bc0.weightx = 1;
        bc0.fill = GridBagConstraints.HORIZONTAL;
        bc0.insets = new Insets(0, 0, 0, 0);
        row4.add(copyCipherButton, bc0);
        GridBagConstraints bc1 = new GridBagConstraints();
        bc1.gridx = 1; bc1.gridy = 0; bc1.weightx = 1;
        bc1.fill = GridBagConstraints.HORIZONTAL;
        bc1.insets = new Insets(0, 6, 0, 0);
        row4.add(saveButton, bc1);
        addFull(panel, row4, gbc, 4, 0);

        return panel;
    }

    private JPanel createDecryptTab() {
        JPanel panel = createSurfacePanel(null);
        panel.setBackground(WORK_PANEL_BACKGROUND);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = baseConstraints();

        addSectionTitle(panel, "Giải mã bản mã", gbc, 0);
        addTextBlock(panel, "Bản mã hex", cipherDecryptArea, gbc, 1);
        addButtonRow(panel, gbc, 2,
                new ActionButton("Mở file bản mã", () -> openTextFile(cipherDecryptArea)),
                new ActionButton("Giải mã", this::decryptText));

        addTextBlock(panel, "Bản rõ sau giải mã", plainDecryptArea, gbc, 3);
        addButtonRow(panel, gbc, 4,
                new ActionButton("Lưu bản rõ", () -> saveTextFile(plainDecryptArea, "ban_ro.txt")));
        return panel;
    }

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(true);
        bar.setBackground(new Color(224, 244, 235));
        bar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(MUTED_TEXT);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private JPanel createSurfacePanel(Dimension preferredSize) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        if (preferredSize != null) {
            panel.setPreferredSize(preferredSize);
        }
        return panel;
    }

    private GridBagConstraints baseConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 4, 5, 4);
        return gbc;
    }

    private void addSectionTitle(JPanel panel, String text, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 17));
        label.setForeground(TITLE_TEXT);
        addFull(panel, label, gbc, row, 0);
    }

    private void addLabel(JPanel panel, String text, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(LABEL_TEXT);
        addFull(panel, label, gbc, row, 0);
    }

    private void addTextBlock(JPanel panel, String labelText, JTextArea area, GridBagConstraints gbc, int row) {
        JPanel block = new JPanel(new BorderLayout(0, 6));
        block.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TITLE_TEXT);

        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(FIELD_BACKGROUND);
        area.setForeground(Color.BLACK);
        area.setCaretColor(TITLE_TEXT);
        area.setSelectionColor(new Color(185, 225, 255));
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        block.add(label, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(134, 170, 214)));
        block.add(scrollPane, BorderLayout.CENTER);
        addFull(panel, block, gbc, row, 1);
    }

    private void addButtonRow(JPanel panel, GridBagConstraints gbc, int row, ActionButton... actions) {
        JPanel rowPanel = new JPanel(new GridBagLayout());
        rowPanel.setOpaque(false);

        for (int i = 0; i < actions.length; i++) {
            GridBagConstraints buttonGbc = new GridBagConstraints();
            buttonGbc.gridx = i;
            buttonGbc.gridy = 0;
            buttonGbc.weightx = 1;
            buttonGbc.fill = GridBagConstraints.HORIZONTAL;
            buttonGbc.insets = new Insets(0, i == 0 ? 0 : 6, 0, 0);
            ActionButton action = actions[i];
            // Su dung nut da tao san neu co, nguoc lai tao moi.
            JButton btn = (action.button != null)
                    ? action.button
                    : createButton(action.text, action.action, i == actions.length - 1);
            rowPanel.add(btn, buttonGbc);
        }

        addFull(panel, rowPanel, gbc, row, 0);
    }

    private void addButton(JPanel panel, String text, Runnable action, boolean primary, GridBagConstraints gbc, int row) {
        addFull(panel, createButton(text, action, primary), gbc, row, 0);
    }

    private JButton createButton(String text, Runnable action, boolean primary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setForeground(BUTTON_TEXT);
        button.setBackground(SECONDARY_BUTTON);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 178, 88)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        button.addActionListener(event -> action.run());

        if (primary) {
            button.setBackground(PRIMARY);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(28, 137, 198)),
                    BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        }

        return button;
    }

    private void addSeparator(JPanel panel, GridBagConstraints gbc, int row) {
        addFull(panel, new JSeparator(SwingConstants.HORIZONTAL), gbc, row, 0);
    }

    private void addFull(JPanel panel, java.awt.Component component, GridBagConstraints gbc, int row, double weightY) {
        gbc.gridy = row;
        gbc.weighty = weightY;
        gbc.fill = weightY > 0 ? GridBagConstraints.BOTH : GridBagConstraints.HORIZONTAL;
        panel.add(component, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
    }

    private void generateKey() {
        StringBuilder key = new StringBuilder(KeyManager.AES_BLOCK_SIZE);

        for (int i = 0; i < KeyManager.AES_BLOCK_SIZE; i++) {
            key.append(KEY_CHARS.charAt(RANDOM.nextInt(KEY_CHARS.length())));
        }

        keyField.setText(key.toString());
        activeKey = KeyManager.getSecretKey(keyField.getText());
        setStatus("Đã sinh khóa AES-128 mới.", SUCCESS);
    }

    private void confirmKey() {
        try {
            activeKey = KeyManager.getSecretKey(keyField.getText());
            showInfo("Khóa hợp lệ. Bạn có thể dùng khóa này để mã hóa hoặc giải mã.");
            setStatus("Khóa đã được xác nhận.", SUCCESS);
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
            setStatus("Khóa chưa hợp lệ.", DANGER);
        }
    }

    private SecretKey getActiveKey() {
        if (activeKey == null || !keyField.getText().equals(new String(activeKey.getEncoded(), StandardCharsets.UTF_8))) {
            activeKey = KeyManager.getSecretKey(keyField.getText());
        }

        return activeKey;
    }

    private void encryptText() {
        try {
            String cipherText = AesEncryption.encrypt(plainEncryptArea.getText(), getActiveKey());
            cipherEncryptArea.setText(cipherText);
            // Bat nut "Chuyen sang giai ma" sau khi ma hoa thanh cong.
            if (copyCipherButton != null) {
                copyCipherButton.setEnabled(true);
            }
            showInfo("Mã hóa thành công. Bản mã đã được tạo.");
            setStatus("Mã hóa thành công.", SUCCESS);
        } catch (Exception e) {
            showError("Mã hóa thất bại. " + e.getMessage());
            setStatus("Mã hóa thất bại.", DANGER);
        }
    }

    private void decryptText() {
        try {
            String plaintext = AesDecryption.decrypt(cipherDecryptArea.getText(), getActiveKey());
            plainDecryptArea.setText(plaintext);
            showInfo("Giải mã thành công. Bản rõ đã được khôi phục.");
            setStatus("Giải mã thành công.", SUCCESS);
        } catch (IllegalArgumentException e) {
            showError("Giải mã thất bại. " + e.getMessage());
            setStatus("Giải mã thất bại.", DANGER);
        } catch (Exception e) {
            String msg = e.getMessage();
            showError(msg);
            if (AesDecryption.ERR_BOTH.equals(msg)) {
                setStatus("Giải mã thất bại — cả bản mã và khóa bị thay đổi.", DANGER);
            } else if (AesDecryption.ERR_KEY.equals(msg)) {
                setStatus("Giải mã thất bại — khóa bị thay đổi.", DANGER);
            } else if (AesDecryption.ERR_CIPHER.equals(msg)) {
                setStatus("Giải mã thất bại — bản mã bị thay đổi.", DANGER);
            } else {
                setStatus("Giải mã thất bại — phát hiện thay đổi.", DANGER);
            }
        }
    }


    private void copyCipherToDecrypt() {
        cipherDecryptArea.setText(cipherEncryptArea.getText());
        // Chuyen sang tab Giai ma (index 1).
        if (workTabs != null) {
            workTabs.setSelectedIndex(1);
        }
        setStatus("Dã chuyển bản mã sang tab Giải mã.", SUCCESS);
    }

    private void openTextFile(JTextArea target) {
        JFileChooser chooser = createTextChooser();

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            File file = chooser.getSelectedFile();
            target.setText(Files.readString(file.toPath(), StandardCharsets.UTF_8));
            setStatus("Đã mở file: " + file.getName(), SUCCESS);
        } catch (Exception e) {
            showError("Không thể mở file. Vui lòng kiểm tra file đã chọn có tồn tại và bạn có quyền đọc file.");
            setStatus("Mở file thất bại.", DANGER);
        }
    }

    private void saveTextFile(JTextArea source, String suggestedName) {
        JFileChooser chooser = createTextChooser();
        chooser.setSelectedFile(new File(suggestedName));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            Files.writeString(chooser.getSelectedFile().toPath(), source.getText(), StandardCharsets.UTF_8);
            showInfo("Đã lưu file thành công.");
            setStatus("Đã lưu file: " + chooser.getSelectedFile().getName(), SUCCESS);
        } catch (Exception e) {
            showError("Không thể lưu file. Vui lòng kiểm tra thư mục lưu có tồn tại và bạn có quyền ghi file.");
            setStatus("Lưu file thất bại.", DANGER);
        }
    }

    private void saveKey() {
        String keyText = keyField.getText();

        if (keyText == null || keyText.trim().isEmpty()) {
            showError("Không có khóa để lưu.");
            setStatus("Lưu khóa thất bại — ô khóa đang trống.", DANGER);
            return;
        }

        saveTextFile(new JTextArea(keyText), "aes_key.txt");
    }

    private void openKey() {
        JFileChooser chooser = createTextChooser();

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {
            String key = Files.readString(chooser.getSelectedFile().toPath(), StandardCharsets.UTF_8).trim();
            keyField.setText(key);
            activeKey = KeyManager.getSecretKey(key);
            showInfo("Đã mở và xác nhận khóa thành công.");
            setStatus("Đã mở khóa từ file.", SUCCESS);
        } catch (Exception e) {
            showError("Không thể mở khóa. File khóa phải chứa đúng 16 byte ký tự và có thể đọc được.");
            setStatus("Mở khóa thất bại.", DANGER);
        }
    }

    private JFileChooser createTextChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Tệp văn bản (*.txt)", "txt"));
        return chooser;
    }

    private void clearAll() {
        plainEncryptArea.setText("");
        cipherEncryptArea.setText("");
        cipherDecryptArea.setText("");
        plainDecryptArea.setText("");
        // Tat nut "Chuyen sang giai ma" khi lam moi.
        if (copyCipherButton != null) {
            copyCipherButton.setEnabled(false);
        }
        generateKey();
        setStatus("Đã làm mới dữ liệu và sinh khóa mới.", SUCCESS);
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private static class ActionButton {
        private final String text;
        private final Runnable action;
        private final JButton button; // Nut da tao san (neu co).

        private ActionButton(String text, Runnable action) {
            this.text = text;
            this.action = action;
            this.button = null;
        }

        // Constructor nhan JButton da tao san de su dung truc tiep trong addButtonRow.
        private ActionButton(String text, JButton button) {
            this.text = text;
            this.action = null;
            this.button = button;
        }
    }
}
