import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NoorTranslatorApp extends JFrame {

    private JComboBox<String> sourceLangBox, targetLangBox;
    private JTextArea inputArea, outputArea;
    private JLabel statusLabel, statsLabel;
    private JButton translateBtn, copyBtn, clearBtn;

    // Translation Dictionary Database
    private final Map<String, String> dictionary = new HashMap<>();

    public NoorTranslatorApp() {
        setTitle("Noor Multi-Lingual Translator Hub");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(24, 28, 36));

        seedDictionaryData();
        initUI();
    }

    private void seedDictionaryData() {
        // Urdu to English / Arabic / Pashto mappings
        dictionary.put("URDU_ENGLISH_سلام", "Hello");
        dictionary.put("URDU_ARABIC_سلام", "مرحبا");
        dictionary.put("URDU_PASHTO_سلام", "سلامونه");

        dictionary.put("URDU_ENGLISH_آپ کیسے ہیں؟", "How are you?");
        dictionary.put("URDU_ARABIC_آپ کیسے ہیں؟", "كيف حالك؟");
        dictionary.put("URDU_PASHTO_آپ کیسے ہیں؟", "تاسو څنګه یاست؟");

        dictionary.put("URDU_ENGLISH_شکریہ", "Thank you");
        dictionary.put("URDU_ARABIC_شکریہ", "شكرا لك");
        dictionary.put("URDU_PASHTO_شکریہ", "مننه");

        dictionary.put("URDU_ENGLISH_خوش آمدید", "Welcome");
        dictionary.put("URDU_ARABIC_خوش آمدید", "أهلا بك");
        dictionary.put("URDU_PASHTO_خوش آمدید", "ښه راغلاست");

        // English to Urdu / Arabic / Pashto mappings
        dictionary.put("ENGLISH_URDU_hello", "سلام");
        dictionary.put("ENGLISH_ARABIC_hello", "مرحبا");
        dictionary.put("ENGLISH_PASHTO_hello", "سلامونه");
        
        dictionary.put("ENGLISH_URDU_thank you", "شکریہ");
        dictionary.put("ENGLISH_ARABIC_thank you", "شكرا لك");
        dictionary.put("ENGLISH_PASHTO_thank you", "مننه");
    }

    private void initUI() {
        // 1. Header (Branding)
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(24, 28, 36));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel titleLabel = new JLabel("NOOR TRANSLATOR HUB", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 215, 0)); // Gold Accent

        JLabel subtitle = new JLabel("Multi-Lingual Engine | Urdu • English • Arabic • Pashto | Designed by Noor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(new Color(180, 180, 180));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // 2. Language Selection Bar
        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        langPanel.setBackground(new Color(24, 28, 36));

        String[] languages = {"Urdu", "English", "Arabic", "Pashto"};
        sourceLangBox = new JComboBox<>(languages);
        targetLangBox = new JComboBox<>(languages);
        targetLangBox.setSelectedIndex(1); // Default Target: English

        styleComboBox(sourceLangBox);
        styleComboBox(targetLangBox);

        JLabel arrowLabel = new JLabel("➔");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrowLabel.setForeground(new Color(0, 206, 209));

        langPanel.add(new JLabel("From:") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        langPanel.add(sourceLangBox);
        langPanel.add(arrowLabel);
        langPanel.add(new JLabel("To:") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        langPanel.add(targetLangBox);

        // 3. Text Areas (Input & Output)
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        textPanel.setBackground(new Color(24, 28, 36));
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        inputArea = new JTextArea();
        configureTextArea(inputArea, "Type text here (e.g. سلام, آپ کیسے ہیں؟)");

        outputArea = new JTextArea();
        configureTextArea(outputArea, "Translation output will appear here...");
        outputArea.setEditable(false);

        textPanel.add(new JScrollPane(inputArea));
        textPanel.add(new JScrollPane(outputArea));

        // Combined Center Panel
        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setBackground(new Color(24, 28, 36));
        centerContainer.add(langPanel, BorderLayout.NORTH);
        centerContainer.add(textPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // 4. Action Buttons & Footer
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBackground(new Color(24, 28, 36));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(24, 28, 36));

        translateBtn = new JButton("TRANSLATE");
        styleButton(translateBtn, new Color(46, 204, 113));

        copyBtn = new JButton("COPY OUTPUT");
        styleButton(copyBtn, new Color(52, 152, 219));

        clearBtn = new JButton("CLEAR");
        styleButton(clearBtn, new Color(231, 76, 60));

        btnPanel.add(translateBtn);
        btnPanel.add(copyBtn);
        btnPanel.add(clearBtn);

        // Status & Stats Bar
        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setBackground(new Color(24, 28, 36));

        statusLabel = new JLabel("Ready", SwingConstants.LEFT);
        statusLabel.setForeground(new Color(0, 206, 209));

        statsLabel = new JLabel("Words: 0 | Characters: 0", SwingConstants.RIGHT);
        statsLabel.setForeground(Color.LIGHT_GRAY);

        infoBar.add(statusLabel, BorderLayout.WEST);
        infoBar.add(statsLabel, BorderLayout.EAST);

        footerPanel.add(btnPanel, BorderLayout.NORTH);
        footerPanel.add(infoBar, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.SOUTH);

        // Listeners
        translateBtn.addActionListener(e -> performTranslation());
        copyBtn.addActionListener(e -> copyToClipboard());
        clearBtn.addActionListener(e -> clearAll());

        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStats(); }
            public void removeUpdate(DocumentEvent e) { updateStats(); }
            public void changedUpdate(DocumentEvent e) { updateStats(); }
        });
    }

    private void configureTextArea(JTextArea area, String placeholder) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(new Color(36, 41, 51));
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.YELLOW);
        area.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 80, 95)),
                placeholder, 0, 0, new Font("Segoe UI", Font.PLAIN, 12), Color.GRAY));
    }

    private void styleComboBox(JComboBox<String> box) {
        box.setFont(new Font("Segoe UI", Font.BOLD, 14));
        box.setPreferredSize(new Dimension(130, 35));
        box.setBackground(new Color(45, 52, 65));
        box.setForeground(Color.WHITE);
    }

    private void styleButton(JButton btn, Color c) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(c);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(140, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void performTranslation() {
        String input = inputArea.getText().trim();
        if (input.isEmpty()) {
            statusLabel.setText("Please enter text to translate!");
            return;
        }

        String sourceLang = ((String) sourceLangBox.getSelectedItem()).toUpperCase();
        String targetLang = ((String) targetLangBox.getSelectedItem()).toUpperCase();

        if (sourceLang.equals(targetLang)) {
            outputArea.setText(input);
            statusLabel.setText("Source and Target languages are identical.");
            return;
        }

        String lookupKey = sourceLang + "_" + targetLang + "_" + (sourceLang.equals("ENGLISH") ? input.toLowerCase() : input);
        String translation = dictionary.get(lookupKey);

        if (translation != null) {
            outputArea.setText(translation);
            statusLabel.setText("Translation Successful!");
        } else {
            // Fallback strategy for unregistered phrases
            outputArea.setText("[" + targetLang + " Translation]: " + input);
            statusLabel.setText("Direct match not in local database. Displaying formatted output.");
        }
    }

    private void copyToClipboard() {
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            statusLabel.setText("Output copied to clipboard!");
        }
    }

    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("Ready");
        updateStats();
    }

    private void updateStats() {
        String txt = inputArea.getText();
        int chars = txt.length();
        int words = txt.trim().isEmpty() ? 0 : txt.trim().split("\\s+").length;
        statsLabel.setText("Words: " + words + " | Characters: " + chars);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoorTranslatorApp().setVisible(true));
    }
}
