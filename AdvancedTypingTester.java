import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class AdvancedTypingTester extends JFrame {

    private final String[] easyTexts = {
        "cat dog sun sky blue run fast jump high tree leaf water fish bird star moon"
    };
    private final String[] mediumTexts = {
        "Java is a powerful object oriented programming language used to build desktop apps and games."
    };
    private final String[] hardTexts = {
        "System.out.println(\"Hello World!\"); public static void main(String[] args) { int x = 45; }"
    };

    private JTextPane targetPane;
    private JTextArea inputArea;
    private JLabel timerLabel, wpmLabel, accuracyLabel, highScoreLabel;
    private JComboBox<String> difficultyBox, timeBox;
    private JButton startButton, resetButton;
    private VirtualKeyboardPanel keyboardPanel;

    private SoundManager soundManager;
    private Timer timer;
    private int timeRemaining = 30;
    private int initialTime = 30;
    private boolean isRunning = false;
    private String currentText = "";
    private int highScore = 0;
    private final String HIGH_SCORE_FILE = "noor_highscore.txt";

    public AdvancedTypingTester() {
        setTitle("Noor Speed Typer - Pro Edition");
        setSize(850, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));
        getContentPane().setBackground(new Color(24, 28, 36));

        soundManager = new SoundManager();
        loadHighScore();

        // Header Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 28, 36));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel title = new JLabel("NOOR SPEED TYPER PRO", SwingConstants.LEFT);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(255, 215, 0));

        JLabel creator = new JLabel("Engineered & Designed by Noor", SwingConstants.LEFT);
        creator.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        creator.setForeground(new Color(170, 170, 170));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setBackground(new Color(24, 28, 36));
        titleGroup.add(title);
        titleGroup.add(creator);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        // Options Bar (Difficulty & Time Selectors)
        JPanel controlsGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsGroup.setBackground(new Color(24, 28, 36));

        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        timeBox = new JComboBox<>(new String[]{"15 Seconds", "30 Seconds", "60 Seconds"});
        timeBox.setSelectedIndex(1);

        controlsGroup.add(new JLabel("Mode:") {{ setForeground(Color.WHITE); }});
        controlsGroup.add(difficultyBox);
        controlsGroup.add(new JLabel("Time:") {{ setForeground(Color.WHITE); }});
        controlsGroup.add(timeBox);
        headerPanel.add(controlsGroup, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Center Area (Target Text, Input & Keyboard)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(24, 28, 36));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        targetPane = new JTextPane();
        targetPane.setFont(new Font("Consolas", Font.BOLD, 17));
        targetPane.setEditable(false);
        targetPane.setBackground(new Color(36, 41, 51));
        targetPane.setPreferredSize(new Dimension(800, 90));

        inputArea = new JTextArea(4, 50);
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setEnabled(false);
        inputArea.setBackground(new Color(45, 52, 65));
        inputArea.setForeground(Color.WHITE);

        keyboardPanel = new VirtualKeyboardPanel();

        JPanel textStack = new JPanel(new GridLayout(2, 1, 8, 8));
        textStack.setBackground(new Color(24, 28, 36));
        textStack.add(new JScrollPane(targetPane));
        textStack.add(new JScrollPane(inputArea));

        centerPanel.add(textStack, BorderLayout.NORTH);
        centerPanel.add(keyboardPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Dashboard & Stats Footer
        JPanel footerPanel = new JPanel(new BorderLayout(10, 10));
        footerPanel.setBackground(new Color(24, 28, 36));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel statsDashboard = new JPanel(new GridLayout(1, 4, 10, 0));
        statsDashboard.setBackground(new Color(24, 28, 36));

        timerLabel = createMetricLabel("Time: 30s");
        wpmLabel = createMetricLabel("WPM: 0");
        accuracyLabel = createMetricLabel("Accuracy: 100%");
        highScoreLabel = createMetricLabel("High Score: " + highScore + " WPM");

        statsDashboard.add(timerLabel);
        statsDashboard.add(wpmLabel);
        statsDashboard.add(accuracyLabel);
        statsDashboard.add(highScoreLabel);

        footerPanel.add(statsDashboard, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(24, 28, 36));

        startButton = new JButton("START TEST");
        styleBtn(startButton, new Color(46, 204, 113));
        resetButton = new JButton("RESET");
        styleBtn(resetButton, new Color(231, 76, 60));

        btnPanel.add(startButton);
        btnPanel.add(resetButton);
        footerPanel.add(btnPanel, BorderLayout.SOUTH);

        add(footerPanel, BorderLayout.SOUTH);

        // Actions
        startButton.addActionListener(e -> startTest());
        resetButton.addActionListener(e -> resetTest());

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!isRunning) return;
                char c = e.getKeyChar();
                String typed = inputArea.getText() + c;
                int idx = typed.length() - 1;

                if (idx < currentText.length()) {
                    boolean correct = (c == currentText.charAt(idx));
                    keyboardPanel.highlightKey(c, correct);
                    if (correct) soundManager.playKeyPressSound();
                    else soundManager.playErrorSound();
                }
                updateTextHighlights();
                calculateMetrics();
            }
        });
    }

    private JLabel createMetricLabel(String title) {
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(0, 206, 209));
        return lbl;
    }

    private void styleBtn(JButton b, Color c) {
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(150, 42));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void startTest() {
        if (isRunning) return;

        // Determine Selected Time
        String timeStr = (String) timeBox.getSelectedItem();
        initialTime = Integer.parseInt(timeStr.split(" ")[0]);
        timeRemaining = initialTime;

        // Select Text according to difficulty
        String mode = (String) difficultyBox.getSelectedItem();
        if ("Easy".equals(mode)) currentText = easyTexts[0];
        else if ("Hard".equals(mode)) currentText = hardTexts[0];
        else currentText = mediumTexts[0];

        targetPane.setText(currentText);
        inputArea.setText("");
        inputArea.setEnabled(true);
        inputArea.requestFocus();

        isRunning = true;
        startButton.setEnabled(false);
        difficultyBox.setEnabled(false);
        timeBox.setEnabled(false);

        timer = new Timer(1000, e -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining + "s");
            if (timeRemaining <= 0) finishTest();
        });
        timer.start();
    }

    private void updateTextHighlights() {
        StyledDocument doc = targetPane.getStyledDocument();
        SimpleAttributeSet greenAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(greenAttr, new Color(46, 204, 113));

        SimpleAttributeSet redAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(redAttr, new Color(231, 76, 60));

        SimpleAttributeSet defaultAttr = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultAttr, Color.WHITE);

        String typed = inputArea.getText();
        for (int i = 0; i < currentText.length(); i++) {
            if (i < typed.length()) {
                if (typed.charAt(i) == currentText.charAt(i)) {
                    doc.setCharacterAttributes(i, 1, greenAttr, true);
                } else {
                    doc.setCharacterAttributes(i, 1, redAttr, true);
                }
            } else {
                doc.setCharacterAttributes(i, 1, defaultAttr, true);
            }
        }
    }

    private void calculateMetrics() {
        String typed = inputArea.getText();
        int typedLength = typed.length();
        int words = typed.trim().isEmpty() ? 0 : typed.trim().split("\\s+").length;

        double elapsedMinutes = (initialTime - timeRemaining) / 60.0;
        int wpm = (elapsedMinutes > 0) ? (int) (words / elapsedMinutes) : 0;
        wpmLabel.setText("WPM: " + wpm);

        int correctChars = 0;
        for (int i = 0; i < Math.min(typedLength, currentText.length()); i++) {
            if (typed.charAt(i) == currentText.charAt(i)) correctChars++;
        }
        int accuracy = (typedLength > 0) ? (correctChars * 100 / typedLength) : 100;
        accuracyLabel.setText("Accuracy: " + accuracy + "%");
    }

    private void finishTest() {
        timer.stop();
        isRunning = false;
        inputArea.setEnabled(false);
        startButton.setEnabled(true);
        difficultyBox.setEnabled(true);
        timeBox.setEnabled(true);

        int finalWpm = Integer.parseInt(wpmLabel.getText().replace("WPM: ", ""));
        if (finalWpm > highScore) {
            highScore = finalWpm;
            saveHighScore();
            highScoreLabel.setText("High Score: " + highScore + " WPM");
            JOptionPane.showMessageDialog(this, "NEW HIGH SCORE! 🎉\nWPM: " + finalWpm, "Noor Speed Typer Pro", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Test Finished!\n" + wpmLabel.getText() + "\n" + accuracyLabel.getText(), "Noor Speed Typer Pro", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetTest() {
        if (timer != null) timer.stop();
        isRunning = false;
        targetPane.setText("");
        inputArea.setText("");
        inputArea.setEnabled(false);
        startButton.setEnabled(true);
        difficultyBox.setEnabled(true);
        timeBox.setEnabled(true);

        timerLabel.setText("Time: " + initialTime + "s");
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
    }

    private void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (Exception ignored) {
            highScore = 0;
        }
    }

    private void saveHighScore() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
            pw.println(highScore);
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdvancedTypingTester().setVisible(true));
    }
}