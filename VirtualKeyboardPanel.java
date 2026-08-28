import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class VirtualKeyboardPanel extends JPanel {
    private final Map<Character, JButton> keyMap = new HashMap<>();
    private final String[] rows = {
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    };

    public VirtualKeyboardPanel() {
        setLayout(new GridLayout(3, 1, 4, 4));
        setBackground(new Color(24, 28, 36));

        for (String row : rows) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            rowPanel.setBackground(new Color(24, 28, 36));

            for (char ch : row.toCharArray()) {
                JButton btn = new JButton(String.valueOf(ch));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btn.setPreferredSize(new Dimension(42, 38));
                btn.setBackground(new Color(45, 52, 65));
                btn.setForeground(Color.WHITE);
                btn.setFocusable(false);

                keyMap.put(Character.toLowerCase(ch), btn);
                keyMap.put(ch, btn);
                rowPanel.add(btn);
            }
            add(rowPanel);
        }
    }

    public void highlightKey(char ch, boolean isCorrect) {
        JButton btn = keyMap.get(Character.toUpperCase(ch));
        if (btn != null) {
            Color originalBg = btn.getBackground();
            btn.setBackground(isCorrect ? new Color(46, 204, 113) : new Color(231, 76, 60));
            
            Timer timer = new Timer(150, e -> btn.setBackground(originalBg));
            timer.setRepeats(false);
            timer.start();
        }
    }
}