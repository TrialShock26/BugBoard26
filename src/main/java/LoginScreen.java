// LoginScreen.java
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("BugBoard26 - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        // Logo/Title
        JLabel titleLabel = new JLabel("BugBoard26");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(new Color(220, 38, 38));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Issue Reporting System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(75, 85, 85));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Spacer
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(50));

        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLabel.setForeground(new Color(75, 85, 85));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Password field
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passwordLabel.setForeground(new Color(75, 85, 85));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Login button
        JButton loginButton = new JButton("Accedi");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(220, 38, 38));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(239, 68, 68));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(220, 38, 38));
            }
        });

        loginButton.addActionListener(e -> {

            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            // Credenziali di default
            if (email.equals("ciao") && password.equals("ciao")) {

                // Apri schermata Issues
                new Hub().setVisible(true);

                // Chiudi login
                dispose();

            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Credenziali non valide!",
                        "Errore Login",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });


        // Add components
        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(loginButton);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(243, 244, 246));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        infoPanel.setMaximumSize(new Dimension(300, 80));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);


        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}