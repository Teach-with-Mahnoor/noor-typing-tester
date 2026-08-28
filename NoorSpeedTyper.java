import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NoorSpeedTyper extends JFrame {

    private final String sampleText = "Java programming builds problem solving skills and strong logic.";
    
    private JLabel timerLabel, wpmLabel, accuracyLabel;
    private JTextArea targetText, inputArea;
    private JButton startButton, resetButton;

    private Timer timer;
    private int timeRemaining = 30;
    private boolean isRunning = false;

    public NoorSpeedTyper() {
        setTitle("Noor Speed Typer");
        setSize(700, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(new Color(24, 28, 36));

        // Header Branding
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(24, 28, 36));
        
        JLabel title = new JLabel("NOOR SPEED TYPER", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(255, 215, 0));

        JLabel sub = new JLabel("Designed & Developed by Noor", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        sub.setForeground(Color.LIGHT_GRAY);

        header.add(title);
        header.add(sub);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(header, BorderLayout.NORTH);

        // Center Panel - Target Text & Typing Area
        JPanel center = new JPanel(new GridLayout(2, 1, 10, 10));
        center.setBackground(new Color(24, 28, 36));
        center.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        targetText = new JTextArea(sampleText);
        targetText.setFont(new Font("Consolas", Font.BOLD, 17));
        targetText.setLineWrap(true);
        targetText.setWrapStyleWord(true);
        targetText.setEditable(false);
        targetText.setBackground(new Color(36, 41, 51));
        targetText.setForeground(Color.WHITE);
        targetText.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Text To Type", 0, 0, new Font("Segoe UI", Font.PLAIN, 12), Color.LIGHT_GRAY));

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setEnabled(false); // Initially Disabled
        inputArea.setBackground(new Color(45, 52, 65));
        inputArea.setForeground(Color.WHITE);
        inputArea.setCaretColor(Color.YELLOW); // Yellow Cursor
        inputArea.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY), "Type Here After Clicking START", 0, 0, new Font("Segoe UI", Font.PLAIN, 12), Color.LIGHT_GRAY));

        center.add(new JScrollPane(targetText));
        center.add(new JScrollPane(inputArea));
        add(center, BorderLayout.CENTER);

        // Stats Panel & Buttons
        JPanel footer = new JPanel(new GridLayout(2, 1, 10, 10));
        footer.setBackground(new Color(24, 28, 36));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel stats = new JPanel(new GridLayout(1, 3));
        stats.setBackground(new Color(24, 28, 36));

        timerLabel = new JLabel("Time: 30s", SwingConstants.CENTER);
        wpmLabel = new JLabel("WPM: 0", SwingConstants.CENTER);
        accuracyLabel = new JLabel("Accuracy: 100%", SwingConstants.CENTER);

        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        wpmLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        accuracyLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        timerLabel.setForeground(new Color(0, 206, 209));
        wpmLabel.setForeground(new Color(0, 206, 209));
        accuracyLabel.setForeground(new Color(0, 206, 209));

        stats.add(timerLabel);
        stats.add(wpmLabel);
        stats.add(accuracyLabel);
        footer.add(stats);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        btns.setBackground(new Color(24, 28, 36));

        startButton = new JButton("START TEST");
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setPreferredSize(new Dimension(140, 40));
        startButton.setFocusPainted(false);

        resetButton = new JButton("RESET");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetButton.setBackground(new Color(231, 76, 60));
        resetButton.setForeground(Color.WHITE);
        resetButton.setPreferredSize(new Dimension(140, 40));
        resetButton.setFocusPainted(false);

        btns.add(startButton);
        btns.add(resetButton);
        footer.add(btns);

        add(footer, BorderLayout.SOUTH);

        // Event Listeners
        startButton.addActionListener(e -> startTest());
        resetButton.addActionListener(e -> resetTest());

        inputArea.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateStats(); }
            public void removeUpdate(DocumentEvent e) { updateStats(); }
            public void changedUpdate(DocumentEvent e) { updateStats(); }
        });
    }

    private void startTest() {
        if (!isRunning) {
            inputArea.setEnabled(true);
            inputArea.setText("");
            
            // SwingUtilities.invokeLater Focus fix karta hai taake keyboard instantly activate ho jaye
            SwingUtilities.invokeLater(() -> inputArea.requestFocusInWindow());

            timeRemaining = 30;
            isRunning = true;
            startButton.setEnabled(false);

            timer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timeRemaining--;
                    timerLabel.setText("Time: " + timeRemaining + "s");
                    if (timeRemaining <= 0) {
                        timer.stop();
                        isRunning = false;
                        inputArea.setEnabled(false);
                        startButton.setEnabled(true);
                        JOptionPane.showMessageDialog(null, "Time's Up!\n" + wpmLabel.getText() + "\n" + accuracyLabel.getText(), "Noor Speed Typer Result", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            timer.start();
        }
    }

    private void updateStats() {
        if (!isRunning) return;
        String typed = inputArea.getText();
        
        // Calculate WPM
        int words = typed.trim().isEmpty() ? 0 : typed.trim().split("\\s+").length;
        double mins = (30 - timeRemaining) / 60.0;
        int wpm = mins > 0 ? (int)(words / mins) : 0;
        wpmLabel.setText("WPM: " + wpm);

        // Calculate Accuracy
        int correct = 0;
        for (int i = 0; i < Math.min(typed.length(), sampleText.length()); i++) {
            if (typed.charAt(i) == sampleText.charAt(i)) correct++;
        }
        int acc = typed.length() > 0 ? (correct * 100 / typed.length()) : 100;
        accuracyLabel.setText("Accuracy: " + acc + "%");
    }

    private void resetTest() {
        if (timer != null) timer.stop();
        isRunning = false;
        timeRemaining = 30;
        inputArea.setText("");
        inputArea.setEnabled(false);
        startButton.setEnabled(true);
        timerLabel.setText("Time: 30s");
        wpmLabel.setText("WPM: 0");
        accuracyLabel.setText("Accuracy: 100%");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoorSpeedTyper().setVisible(true));
    }
}
