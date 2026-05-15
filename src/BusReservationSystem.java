import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class BusReservationSystem extends JFrame {

    boolean isAdmin = false;

    static final int TOTAL_SEATS = 40;
    int[] seatStatus = new int[TOTAL_SEATS];
    String[] passengerNames = new String[TOTAL_SEATS];
    static final String FILE_PATH = "seats.txt";
    JButton[] seatButtons = new JButton[TOTAL_SEATS];
    JLabel statusLabel;

    Color COLOR_AVAILABLE = new Color(50, 180, 80);
    Color COLOR_BOOKED = new Color(220, 60, 60);
    Color COLOR_VIP_BOOKED = new Color(255, 215, 0);
    Color COLOR_BUTTON_TEXT = Color.WHITE;
    Color COLOR_BG = new Color(30, 30, 50);
    Color COLOR_PANEL_BG = new Color(45, 45, 70);
    Color COLOR_ACTION_BTN = new Color(70, 130, 210);
    Color COLOR_RESET_BTN = new Color(200, 100, 30);
    Color COLOR_EXIT_BTN = new Color(160, 40, 40);

    public BusReservationSystem(boolean isAdmin) {
        this.isAdmin = isAdmin;

        for (int i = 0; i < TOTAL_SEATS; i++) {
            passengerNames[i] = "";
        }

        loadFromFile();
        buildGUI();
    }

    private void buildGUI() {
        setTitle("Bus Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(COLOR_BG);
        topPanel.setBorder(new EmptyBorder(15, 20, 5, 20));
        JLabel titleLabel = new JLabel("BUS RESERVATION SYSTEM", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        statusLabel = new JLabel("", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(180, 210, 255));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
        centerPanel.setBackground(COLOR_BG);
        centerPanel.setBorder(new EmptyBorder(5, 20, 5, 20));
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(COLOR_BG);
        legendPanel.add(createLegendItem("Available", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("Booked", COLOR_BOOKED));
        legendPanel.add(createLegendItem("VIP Booked", COLOR_VIP_BOOKED));
        centerPanel.add(legendPanel, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(5, 8, 6, 6));
        gridPanel.setBackground(COLOR_PANEL_BG);
        gridPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(80, 80, 120), 2),
                new EmptyBorder(12, 12, 12, 12)));

        for (int i = 0; i < TOTAL_SEATS; i++) {
            final int seatNum = i + 1;

            JButton btn = new JButton("S" + seatNum);

            btn.setFont(new Font("Arial", Font.BOLD, 11));
            btn.setForeground(Color.WHITE);

            btn.setBackground(COLOR_AVAILABLE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(true); // IMPORTANT

            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                boolean isVIP = (e.getModifiers() & ActionEvent.SHIFT_MASK) != 0;
                handleSeatClick(seatNum, isVIP);
            });

            seatButtons[i] = btn;
            gridPanel.add(btn);
        }
        centerPanel.add(gridPanel, BorderLayout.CENTER);

        JPanel driverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        driverPanel.setBackground(COLOR_BG);
        JLabel driverLabel = new JLabel("  DRIVER  |  Front of Bus");
        driverLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        driverLabel.setForeground(new Color(150, 180, 220));
        driverPanel.add(driverLabel);
        centerPanel.add(driverPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(COLOR_PANEL_BG);
        buttonPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel actionsLabel = new JLabel("ACTIONS");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        actionsLabel.setForeground(new Color(180, 200, 255));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(actionsLabel);
        buttonPanel.add(Box.createVerticalStrut(12));
        addActionButton(buttonPanel, "Show All Seats", COLOR_ACTION_BTN, e -> showAllSeats());
        buttonPanel.add(Box.createVerticalStrut(8));
        addActionButton(buttonPanel, "Book a Seat", COLOR_ACTION_BTN, e -> bookSeat());
        buttonPanel.add(Box.createVerticalStrut(8));
        addActionButton(buttonPanel, "Cancel a Seat", COLOR_ACTION_BTN, e -> cancelSeat());
        buttonPanel.add(Box.createVerticalStrut(8));
        addActionButton(buttonPanel, "Search Seat", COLOR_ACTION_BTN, e -> searchSeat());
        buttonPanel.add(Box.createVerticalStrut(8));
        addActionButton(buttonPanel, "Available Count", COLOR_ACTION_BTN, e -> showAvailableCount());
        buttonPanel.add(Box.createVerticalStrut(20));

        if (isAdmin) {
            addActionButton(buttonPanel, "Show All Bookings", COLOR_ACTION_BTN, e -> showAllBookings());
            buttonPanel.add(Box.createVerticalStrut(8));
            addActionButton(buttonPanel, "Reset All Seats", COLOR_RESET_BTN, e -> resetAllSeats());
        }

        buttonPanel.add(Box.createVerticalStrut(8));

        addActionButton(buttonPanel, "Logout", new Color(120, 120, 120), e -> {
            dispose(); // window se option pane par a jao ya current window close karne ka button
            new LoginFrame(false);
        });

        buttonPanel.add(Box.createVerticalStrut(8));

        addActionButton(buttonPanel, "Exit", COLOR_EXIT_BTN, e -> exitApp());

        add(buttonPanel, BorderLayout.EAST);

        updateSeatGrid();
        updateStatusLabel();
        setSize(780, 500);
        setMinimumSize(new Dimension(700, 460));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(COLOR_BG);
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(16, 16));
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        item.add(colorBox);
        item.add(lbl);
        return item;
    }

    private void addActionButton(JPanel panel, String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(160, 35));
        btn.setPreferredSize(new Dimension(160, 35));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        panel.add(btn);
    }

    private void updateSeatGrid() {
        for (int i = 0; i < TOTAL_SEATS; i++) {

            if (seatStatus[i] == 0) {
                seatButtons[i].setBackground(COLOR_AVAILABLE);
                seatButtons[i].setToolTipText("Seat " + (i + 1) + " - Available");
            } else if (seatStatus[i] == 2) {
                seatButtons[i].setBackground(COLOR_VIP_BOOKED);
                seatButtons[i].setToolTipText(
                        "Seat " + (i + 1) + " - VIP Booked by: " + passengerNames[i]);
            } else {
                seatButtons[i].setBackground(COLOR_BOOKED);
                seatButtons[i].setToolTipText(
                        "Seat " + (i + 1) + " - Booked by: " + passengerNames[i]);
            }
            seatButtons[i].setOpaque(true);
            seatButtons[i].setContentAreaFilled(true);
        }
    }

    private void updateStatusLabel() {
        int available = 0;
        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (seatStatus[i] == 0)
                available++;
        }
        int booked = TOTAL_SEATS - available;
        statusLabel.setText("Total Seats: " + TOTAL_SEATS + "  |  Available: " + available + "  |  Booked: " + booked);
    }

    private void handleSeatClick(int seatNum, boolean isVIP) {
        int index = seatNum - 1;
        if (seatStatus[index] == 0) {
            if (isVIP) {
                int vipCount = 0;
                for (int status : seatStatus) {
                    if (status == 2)
                        vipCount++;
                }
                if (vipCount >= 10) {
                    JOptionPane.showMessageDialog(this, "Sorry, all 10 VIP seats are already booked!", "VIP Sold Out",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String title = isVIP ? "VIP Booking Seat " + seatNum : "Book Seat " + seatNum;
            String msg = isVIP ? "Seat " + seatNum + " is AVAILABLE.\nEnter passenger name for VIP booking:"
                    : "Seat " + seatNum + " is AVAILABLE.\nEnter passenger name to book:";
            String name = JOptionPane.showInputDialog(this, msg, title, JOptionPane.PLAIN_MESSAGE);
            if (name != null && !name.trim().isEmpty()) {
                seatStatus[index] = isVIP ? 2 : 1;
                passengerNames[index] = name.trim();
                saveToFile();
                updateSeatGrid();
                updateStatusLabel();
                JOptionPane.showMessageDialog(this, "Seat " + seatNum + " booked for: " + name.trim(),
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (!isAdmin) {
                String verifyName = JOptionPane.showInputDialog(this, "Enter passenger name to verify cancellation:", "Security Check", JOptionPane.PLAIN_MESSAGE);
                if (verifyName == null || !verifyName.trim().equalsIgnoreCase(passengerNames[index])) {
                    JOptionPane.showMessageDialog(this, "Name does not match. Cancellation denied.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String type = seatStatus[index] == 2 ? "VIP BOOKED" : "BOOKED";
            int choice = JOptionPane.showConfirmDialog(this,
                    "Seat " + seatNum + " is " + type + " by: " + passengerNames[index] + "\nCancel this booking?",
                    "Cancel Booking", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                seatStatus[index] = 0;
                passengerNames[index] = "";
                saveToFile();
                updateSeatGrid();
                updateStatusLabel();
                JOptionPane.showMessageDialog(this, "Seat " + seatNum + " has been cancelled.", "Booking Cancelled",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void showAllSeats() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-12s %-20s%n", "Seat", "Status", "Passenger"));
        sb.append("-".repeat(42)).append("\n");
        for (int i = 0; i < TOTAL_SEATS; i++) {
            String status = seatStatus[i] == 0 ? "Available" : (seatStatus[i] == 2 ? "VIP Booked" : "Booked");
            String name = seatStatus[i] == 0 ? "---" : passengerNames[i];
            sb.append(String.format("%-8s %-12s %-20s%n", "Seat " + (i + 1), status, name));
        }
        showScrollableDialog("All Seats", sb.toString());
    }

    private Integer getValidSeatNumber(String message) {
        String input = JOptionPane.showInputDialog(this, message);
        if (input == null)
            return null;

        try {
            int seat = Integer.parseInt(input.trim());
            if (seat < 1 || seat > TOTAL_SEATS) {
                JOptionPane.showMessageDialog(this, "Seat must be between 1 and " + TOTAL_SEATS);
                return null;
            }
            return seat;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number!");
            return null;
        }
    }

    private void bookSeat() {
        Integer seatNum = getValidSeatNumber("Enter seat number to book:");
        if (seatNum == null)
            return;

        int index = seatNum - 1;

        if (seatStatus[index] >= 1) {
            JOptionPane.showMessageDialog(this, "Already booked by: " + passengerNames[index]);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Enter passenger name:");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty!");
            return;
        }

        seatStatus[index] = 1;
        passengerNames[index] = name.trim();

        saveToFile();
        updateSeatGrid();
        updateStatusLabel();

        JOptionPane.showMessageDialog(this, "Seat booked successfully!");
    }

    private void cancelSeat() {
        Integer seatNum = getValidSeatNumber("Enter seat number to cancel:");
        if (seatNum == null)
            return;

        int index = seatNum - 1;

        if (seatStatus[index] == 0) {
            JOptionPane.showMessageDialog(this, "Seat already available!");
            return;
        }

        if (!isAdmin) {
            String verifyName = JOptionPane.showInputDialog(this, "Enter passenger name to verify cancellation:", "Security Check", JOptionPane.PLAIN_MESSAGE);
            if (verifyName == null || !verifyName.trim().equalsIgnoreCase(passengerNames[index])) {
                JOptionPane.showMessageDialog(this, "Name does not match. Cancellation denied.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Cancel booking for " + passengerNames[index] + "?");

        if (choice == JOptionPane.YES_OPTION) {
            seatStatus[index] = 0;
            passengerNames[index] = "";

            saveToFile();
            updateSeatGrid();
            updateStatusLabel();

            JOptionPane.showMessageDialog(this, "Cancelled successfully!");
        }
    }

    private void searchSeat() {
        Integer seatNum = getValidSeatNumber("Enter seat number to search:");
        if (seatNum == null)
            return;

        int index = seatNum - 1;

        String msg;
        if (seatStatus[index] == 0) {
            msg = "Seat is AVAILABLE";
        } else if (seatStatus[index] == 2) {
            msg = "VIP Booked by: " + passengerNames[index];
        } else {
            msg = "Booked by: " + passengerNames[index];
        }

        JOptionPane.showMessageDialog(this, msg);
    }

    private void showAllBookings() {

        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Admin access required!");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-8s %-20s%n", "Seat", "Passenger Name"));
        sb.append("-".repeat(30)).append("\n");

        int count = 0;
        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (seatStatus[i] >= 1) {
                String type = seatStatus[i] == 2 ? "(VIP)" : "";
                sb.append(String.format("%-8s %-20s%n",
                        "Seat " + (i + 1),
                        passengerNames[i] + " " + type));
                count++;
            }
        }

        if (count == 0)
            sb.append("\nNo bookings found.");
        else
            sb.append("\nTotal booked: ").append(count);

        showScrollableDialog("All Bookings", sb.toString());
    }

    private void showAvailableCount() {
        int available = 0;
        for (int i = 0; i < TOTAL_SEATS; i++) {
            if (seatStatus[i] == 0)
                available++;
        }
        JOptionPane.showMessageDialog(this, "Total Seats  : " + TOTAL_SEATS + "\nAvailable    : " + available
                + "\nBooked       : " + (TOTAL_SEATS - available), "Seat Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetAllSeats() {

        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "Admin access required!");
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset ALL seats?\nThis will cancel every booking!",
                "Reset Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            for (int i = 0; i < TOTAL_SEATS; i++) {
                seatStatus[i] = 0;
                passengerNames[i] = "";
            }
            saveToFile();
            updateSeatGrid();
            updateStatusLabel();

            JOptionPane.showMessageDialog(this, "All seats have been reset to available.");
        }
    }

    private void exitApp() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION)
            System.exit(0);
    }

    private void saveToFile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH));
            for (int i = 0; i < TOTAL_SEATS; i++)
                writer.println(seatStatus[i] + "," + passengerNames[i]);
            writer.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage(), "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null && index < TOTAL_SEATS) {
                String[] parts = line.split(",", 2);
                if (parts.length >= 1)
                    seatStatus[index] = Integer.parseInt(parts[0].trim());
                if (parts.length == 2)
                    passengerNames[index] = parts[1];
                index++;
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            for (int i = 0; i < TOTAL_SEATS; i++) {
                seatStatus[i] = 0;
                passengerNames[i] = "";
            }
        }
    }

    private void showScrollableDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(40, 40, 60));
        textArea.setForeground(Color.WHITE);
        textArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(420, 300));
        JOptionPane.showMessageDialog(this, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
    }

    public static class LoginFrame extends JFrame {

        JTextField usernameField;
        JPasswordField passwordField;

        boolean selectedAdmin;

        public LoginFrame(boolean selectedAdmin) {

            this.selectedAdmin = selectedAdmin;

            setTitle("Login - Bus Reservation System");
            setSize(400, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(EXIT_ON_CLOSE);

            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(new Color(30, 30, 50));

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(new Color(45, 45, 70));
            card.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

            JLabel title = new JLabel("LOGIN");
            title.setFont(new Font("Arial", Font.BOLD, 20));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT);

            usernameField = new JTextField();
            styleField(usernameField, "Username");

            passwordField = new JPasswordField();
            styleField(passwordField, "Password");

            JButton loginBtn = createButton("Login", new Color(70, 130, 210));
            JButton exitBtn = createButton("Exit", new Color(160, 40, 40));

            JPanel btnPanel = new JPanel();
            btnPanel.setBackground(card.getBackground());
            btnPanel.add(loginBtn);
            btnPanel.add(exitBtn);

            card.add(title);
            card.add(Box.createVerticalStrut(15));
            card.add(usernameField);
            card.add(Box.createVerticalStrut(10));
            card.add(passwordField);
            card.add(Box.createVerticalStrut(15));
            card.add(btnPanel);

            mainPanel.add(card);
            add(mainPanel);

            loginBtn.addActionListener(e -> login());
            exitBtn.addActionListener(e -> System.exit(0));

            setVisible(true);
        }

        private void styleField(JTextField field, String title) {
            field.setMaximumSize(new Dimension(250, 35));
            field.setFont(new Font("Arial", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.GRAY),
                    title,
                    0, 0,
                    new Font("Arial", Font.PLAIN, 12),
                    Color.WHITE));
            field.setBackground(new Color(60, 60, 90));
            field.setForeground(Color.WHITE);
            field.setCaretColor(Color.WHITE);
        }

        private JButton createButton(String text, Color color) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setBackground(color);
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 13));
            btn.setPreferredSize(new Dimension(100, 35));
            return btn;
        }

        private void login() {
            String user = usernameField.getText();
            String pass = new String(passwordField.getPassword());

            boolean isAdmin = false;

            if (selectedAdmin) {

                if (user.equals("admin") && pass.equals("12345678")) {
                    isAdmin = true;
                    JOptionPane.showMessageDialog(this, "Admin Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong Admin Credentials!");
                    return;
                }
            } else {

                JOptionPane.showMessageDialog(this, "Logged in as User");
            }

            new BusReservationSystem(isAdmin);

            dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {

            String[] options = { "Admin", "User" };

            int choice = JOptionPane.showOptionDialog(
                    null,
                    "How do you want to login?",
                    "Select Role",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == -1)
                System.exit(0);
            boolean isAdmin = (choice == 0);

            // Open login screen
            new LoginFrame(isAdmin);
        });
    }

}
