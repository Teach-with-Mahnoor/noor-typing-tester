import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NoorWeatherApp extends JFrame {

    // UI Components
    private JComboBox<String> locationSelector;
    private JLabel tempLabel, conditionLabel, humidityLabel, windLabel, iconLabel;
    private JLabel fajrLabel, dhuhrLabel, asrLabel, maghribLabel, ishaLabel;
    private JToggleButton unitToggle;

    private boolean isCelsius = true;
    private double currentTempC = 34.0; // Default temp for Layyah/Dhori Adda

    // Mock Data Store for Weather & Prayers
    private static class CityData {
        double tempC;
        String condition;
        String humidity;
        String wind;
        String[] prayers; // Fajr, Dhuhr, Asr, Maghrib, Isha

        CityData(double tempC, String condition, String humidity, String wind, String[] prayers) {
            this.tempC = tempC;
            this.condition = condition;
            this.humidity = humidity;
            this.wind = wind;
            this.prayers = prayers;
        }
    }

    private final Map<String, CityData> cityDatabase = new HashMap<>();

    public NoorWeatherApp() {
        setTitle("Noor Weather & Prayer Hub");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Populate Location Database (Layyah & Dhori Adda Specifics)
        cityDatabase.put("Layyah, PK", new CityData(35.0, "Sunny", "42%", "12 km/h", new String[]{"04:25 AM", "12:22 PM", "05:01 PM", "06:55 PM", "08:18 PM"}));
        cityDatabase.put("Dhori Adda, PK", new CityData(34.5, "Clear Sky", "45%", "10 km/h", new String[]{"04:24 AM", "12:22 PM", "05:01 PM", "06:55 PM", "08:18 PM"}));
        cityDatabase.put("Multan, PK", new CityData(36.0, "Partly Cloudy", "40%", "14 km/h", new String[]{"04:26 AM", "12:21 PM", "05:00 PM", "06:54 PM", "08:17 PM"}));
        cityDatabase.put("Lahore, PK", new CityData(33.0, "Haze", "55%", "8 km/h", new String[]{"04:18 AM", "12:14 PM", "04:54 PM", "06:48 PM", "08:11 PM"}));
        cityDatabase.put("Islamabad, PK", new CityData(31.0, "Thunderstorm", "65%", "18 km/h", new String[]{"04:14 AM", "12:15 PM", "04:57 PM", "06:52 PM", "08:17 PM"}));

        initUI();
        updateCityData("Layyah, PK");
    }

    private void initUI() {
        // Main Container Panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(20, 24, 33));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Header (Branding & Search Bar)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(20, 24, 33));

        JLabel brandLabel = new JLabel("NOOR WEATHER & PRAYER HUB");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLabel.setForeground(new Color(255, 215, 0)); // Gold Accent

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, dd MMMM yyyy").format(new Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(Color.LIGHT_GRAY);

        JPanel titleStack = new JPanel(new GridLayout(2, 1));
        titleStack.setBackground(new Color(20, 24, 33));
        titleStack.add(brandLabel);
        titleStack.add(dateLabel);

        // Location Selector & Unit Toggle
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchControls.setBackground(new Color(20, 24, 33));

        locationSelector = new JComboBox<>(cityDatabase.keySet().toArray(new String[0]));
        locationSelector.setFont(new Font("Segoe UI", Font.BOLD, 13));
        locationSelector.setPreferredSize(new Dimension(160, 35));
        locationSelector.addActionListener((ActionEvent e) -> {
            String selected = (String) locationSelector.getSelectedItem();
            updateCityData(selected);
        });

        unitToggle = new JToggleButton("°C / °F");
        unitToggle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        unitToggle.setPreferredSize(new Dimension(75, 35));
        unitToggle.setBackground(new Color(45, 52, 65));
        unitToggle.setForeground(Color.WHITE);
        unitToggle.setFocusPainted(false);
        unitToggle.addActionListener(e -> {
            isCelsius = !unitToggle.isSelected();
            refreshTemperatureDisplay();
        });

        searchControls.add(locationSelector);
        searchControls.add(unitToggle);

        headerPanel.add(titleStack, BorderLayout.WEST);
        headerPanel.add(searchControls, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Center Content Split Panel (Weather Left, Prayer Times Right)
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setBackground(new Color(20, 24, 33));

        // --- Weather Card (Left) ---
        JPanel weatherCard = createCardPanel("CURRENT WEATHER");
        weatherCard.setLayout(new BoxLayout(weatherCard, BoxLayout.Y_AXIS));

        iconLabel = new JLabel("☀️", SwingConstants.CENTER); // Dynamic Emoji/Symbol Icon
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        tempLabel = new JLabel("35°C", SwingConstants.CENTER);
        tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        tempLabel.setForeground(Color.WHITE);
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        conditionLabel = new JLabel("Sunny", SwingConstants.CENTER);
        conditionLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        conditionLabel.setForeground(new Color(0, 206, 209));
        conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(300, 10));

        JPanel detailsGrid = new JPanel(new GridLayout(1, 2, 10, 0));
        detailsGrid.setBackground(new Color(30, 36, 48));

        humidityLabel = createDetailLabel("Humidity: 42%");
        windLabel = createDetailLabel("Wind: 12 km/h");

        detailsGrid.add(humidityLabel);
        detailsGrid.add(windLabel);

        weatherCard.add(Box.createVerticalStrut(10));
        weatherCard.add(iconLabel);
        weatherCard.add(tempLabel);
        weatherCard.add(conditionLabel);
        weatherCard.add(Box.createVerticalStrut(15));
        weatherCard.add(sep);
        weatherCard.add(Box.createVerticalStrut(15));
        weatherCard.add(detailsGrid);

        // --- Prayer Times Card (Right) ---
        JPanel prayerCard = createCardPanel("DAILY PRAYER TIMES");
        prayerCard.setLayout(new GridLayout(5, 1, 0, 10));

        fajrLabel = createPrayerRow("Fajr", "04:25 AM");
        dhuhrLabel = createPrayerRow("Dhuhr", "12:22 PM");
        asrLabel = createPrayerRow("Asr", "05:01 PM");
        maghribLabel = createPrayerRow("Maghrib", "06:55 PM");
        ishaLabel = createPrayerRow("Isha", "08:18 PM");

        prayerCard.add(fajrLabel);
        prayerCard.add(dhuhrLabel);
        prayerCard.add(asrLabel);
        prayerCard.add(maghribLabel);
        prayerCard.add(ishaLabel);

        contentPanel.add(weatherCard);
        contentPanel.add(prayerCard);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 3. Footer
        JLabel footerLabel = new JLabel("Designed & Engineered by Noor | Live Updates Enabled", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        footerLabel.setForeground(Color.GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createCardPanel(String title) {
        JPanel card = new JPanel();
        card.setBackground(new Color(30, 36, 48));
        card.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(60, 70, 90)),
                title, 0, 0, new Font("Segoe UI", Font.BOLD, 13), new Color(255, 215, 0)));
        return card;
    }

    private JLabel createDetailLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(Color.LIGHT_GRAY);
        return lbl;
    }

    private JLabel createPrayerRow(String name, String time) {
        JLabel lbl = new JLabel("   " + name + " : " + time, SwingConstants.LEFT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(40, 48, 64));
        lbl.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 85), 1));
        return lbl;
    }

    private void updateCityData(String cityName) {
        CityData data = cityDatabase.get(cityName);
        if (data != null) {
            currentTempC = data.tempC;
            refreshTemperatureDisplay();

            conditionLabel.setText(data.condition);
            humidityLabel.setText("Humidity: " + data.humidity);
            windLabel.setText("Wind: " + data.wind);

            // Update Dynamic Weather Symbol Icon
            if (data.condition.contains("Sunny") || data.condition.contains("Clear")) iconLabel.setText("☀️");
            else if (data.condition.contains("Cloud")) iconLabel.setText("⛅");
            else if (data.condition.contains("Rain") || data.condition.contains("Thunderstorm")) iconLabel.setText("🌧️");
            else iconLabel.setText("🌫️");

            // Update Prayer Times
            fajrLabel.setText("   Fajr : " + data.prayers[0]);
            dhuhrLabel.setText("   Dhuhr : " + data.prayers[1]);
            asrLabel.setText("   Asr : " + data.prayers[2]);
            maghribLabel.setText("   Maghrib : " + data.prayers[3]);
            ishaLabel.setText("   Isha : " + data.prayers[4]);
        }
    }

    private void refreshTemperatureDisplay() {
        if (isCelsius) {
            tempLabel.setText(String.format("%.1f°C", currentTempC));
        } else {
            double tempF = (currentTempC * 9 / 5) + 32;
            tempLabel.setText(String.format("%.1f°F", tempF));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NoorWeatherApp().setVisible(true));
    }
}