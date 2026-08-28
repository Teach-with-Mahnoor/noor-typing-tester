import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoorLiveTranslator extends JFrame {

    private JComboBox<Language> sourceLangBox, targetLangBox;
    private JTextArea inputArea, outputArea;
    private JLabel statusLabel, statsLabel;
    private JButton translateBtn, copyBtn, clearBtn;

    static class Language {
        String name;
        String code;

        public Language(String name, String code) {
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public NoorLiveTranslator() {
        setTitle("Noor Live Multi-Lingual Translator Hub");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(24, 28, 36));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(24, 28, 36));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));

        JLabel titleLabel = new JLabel("NOOR LIVE TRANSLATOR", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(255, 215, 0));

        JLabel subtitle = new JLabel("Real-Time API Translation Engine | Urdu • English • Arabic • Pashto | Built by Noor", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        subtitle.setForeground(new Color(180, 180, 180));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitle);
        add(headerPanel, BorderLayout.NORTH);

        // Languages Setup
        Language[] languages = {
            new Language("English", "en"),
            new Language("Urdu", "ur"),
            new Language("Arabic", "ar"),
            new Language("Pashto", "ps")
        };

        sourceLangBox = new JComboBox<>(languages);
        targetLangBox = new JComboBox<>(languages);
        targetLangBox.setSelectedIndex(3); // Default target: Pashto

        styleComboBox(sourceLangBox);
        styleComboBox(targetLangBox);

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        langPanel.setBackground(new Color(24, 28, 36));

        JLabel arrowLabel = new JLabel("➔");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        arrowLabel.setForeground(new Color(0, 206, 209));

        langPanel.add(new JLabel("From:") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        langPanel.add(sourceLangBox);
        langPanel.add(arrowLabel);
        langPanel.add(new JLabel("To:") {{ setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 14)); }});
        langPanel.add(targetLangBox);

        // Text Areas
        JPanel textPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        textPanel.setBackground(new Color(24, 28, 36));
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        inputArea = new JTextArea();
        configureTextArea(inputArea, "Aap jo bhi likhna chahein yahan type karein...");

        outputArea = new JTextArea();
        configureTextArea(outputArea, "Live translation yahan show hogi...");
        outputArea.setEditable(false);

        textPanel.add(new JScrollPane(inputArea));
        textPanel.add(new JScrollPane(outputArea));

        JPanel centerContainer = new JPanel(new BorderLayout(10, 10));
        centerContainer.setBackground(new Color(24, 28, 36));
        centerContainer.add(langPanel, BorderLayout.NORTH);
        centerContainer.add(textPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // Buttons & Status Footer
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBackground(new Color(24, 28, 36));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(new Color(24, 28, 36));

        translateBtn = new JButton("TRANSLATE LIVE");
        styleButton(translateBtn, new Color(46, 204, 113));

        copyBtn = new JButton("COPY RESULT");
        styleButton(copyBtn, new Color(52, 152, 219));

        clearBtn = new JButton("CLEAR");
        styleButton(clearBtn, new Color(231, 76, 60));

        btnPanel.add(translateBtn);
        btnPanel.add(copyBtn);
        btnPanel.add(clearBtn);

        JPanel infoBar = new JPanel(new BorderLayout());
        infoBar.setBackground(new Color(24, 28, 36));

        statusLabel = new JLabel("Status: Ready", SwingConstants.LEFT);
        statusLabel.setForeground(new Color(0, 206, 209));

        statsLabel = new JLabel("Words: 0 | Characters: 0", SwingConstants.RIGHT);
        statsLabel.setForeground(Color.LIGHT_GRAY);

        infoBar.add(statusLabel, BorderLayout.WEST);
        infoBar.add(statsLabel, BorderLayout.EAST);

        footerPanel.add(btnPanel, BorderLayout.NORTH);
        footerPanel.add(infoBar, BorderLayout.SOUTH);
        add(footerPanel, BorderLayout.SOUTH);

        // Actions
        translateBtn.addActionListener(e -> startBackgroundTranslation());
        copyBtn.addActionListener(e -> copyToClipboard());
        clearBtn.addActionListener(e -> clearAll());

        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStats(); }
            public void removeUpdate(DocumentEvent e) { updateStats(); }
            public void changedUpdate(DocumentEvent e) { updateStats(); }
        });
    }

    private void styleComboBox(JComboBox<Language> box) {
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
        btn.setPreferredSize(new Dimension(150, 38));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configureTextArea(JTextArea area, String placeholder) {
        // Supporting Unicode Fonts like Segoe UI for Arabic/Urdu/Pashto
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

    private void startBackgroundTranslation() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            statusLabel.setText("Status: Pehle text type karein!");
            return;
        }

        Language fromLang = (Language) sourceLangBox.getSelectedItem();
        Language toLang = (Language) targetLangBox.getSelectedItem();

        statusLabel.setText("Status: Translating live text...");
        translateBtn.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                return fetchTranslation(text, fromLang.code, toLang.code);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    outputArea.setText(result);
                    statusLabel.setText("Status: Live Translation Completed!");
                } catch (Exception ex) {
                    outputArea.setText("Error: Translation request failed.");
                    statusLabel.setText("Status: Error Occurred");
                } finally {
                    translateBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private String fetchTranslation(String textToTranslate, String from, String to) {
        try {
            String encodedText = URLEncoder.encode(textToTranslate, StandardCharsets.UTF_8);
            String langPair = from + "|" + to;
            String urlStr = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + langPair;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String json = response.toString();
            int matchIndex = json.indexOf("\"translatedText\":\"");
            if (matchIndex != -1) {
                int start = matchIndex + 18;
                int end = json.indexOf("\"", start);
                String rawTranslated = json.substring(start, end);
                
                // UNICODE DECODER: Converts \u0632 to actual characters
                return decodeUnicode(rawTranslated);
            }
            return "Translation error!";
        } catch (Exception e) {
            return "Connection Error: Check internet connection!";
        }
    }

    // Helper Method to convert \uXXXX unicode escapes into actual text
    private String decodeUnicode(String input) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuffer decoded = new StringBuffer();

        while (matcher.find()) {
            char ch = (char) Integer.parseInt(matcher.group(1), 16);
            matcher.appendReplacement(decoded, Matcher.quoteReplacement(String.valueOf(ch)));
        }
        matcher.appendTail(decoded);
        return decoded.toString().replace("\\\"", "\"").replace("\\n", "\n");
    }

    private void copyToClipboard() {
        String text = outputArea.getText();
        if (!text.isEmpty()) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
            statusLabel.setText("Status: Result copied to clipboard!");
        }
    }

    private void clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        statusLabel.setText("Status: Ready");
        updateStats();
    }

    private void updateStats() {
        String txt = inputArea.getText();
        int chars = txt.length();
        int words = txt.trim().isEmpty() ? 0 : txt.trim().split("\\s+").length;
        statsLabel.setText("Words: " + words + " | Characters: " + chars);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoorLiveTranslator().setVisible(true));
    }
}