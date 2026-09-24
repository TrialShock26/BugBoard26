package frontend.gui;

import javax.swing.*;
import java.awt.*;
import frontend.controller.AuthController;
import frontend.controller.Session;
import frontend.dto.UserDTO;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("BugBoard26 - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(430, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        JLabel titleLabel;
        ImageIcon loginLogoIcon = AppLogo.full(150);
        if (loginLogoIcon != null) {
            titleLabel = new JLabel(loginLogoIcon);
        } else {
            titleLabel = new JLabel("BugBoard26");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
            titleLabel.setForeground(new Color(220, 38, 38));
        }
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Issue Reporting System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(75, 85, 85));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(35));

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

        JButton loginButton = new JButton("Log In");
        loginButton.setMaximumSize(new Dimension(300, 45));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginButton.setBackground(new Color(220, 38, 38));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(239, 68, 68));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(new Color(220, 38, 38));
            }
        });

        Runnable doLogin = () -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter email and password.",
                        "Missing fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            loginButton.setEnabled(false);
            loginButton.setText("Logging in...");

            SwingWorker<UserDTO, Void> worker = new SwingWorker<>() {
                @Override
                protected UserDTO doInBackground() {
                    return AuthController.login(email, password);
                }

                @Override
                protected void done() {
                    loginButton.setEnabled(true);
                    loginButton.setText("Log In");
                    try {
                        UserDTO user = get();
                        Session.set(user);
                        new Hub().setVisible(true);
                        dispose();
                    } catch (Exception e) {
                        Throwable cause = e.getCause() != null ? e.getCause() : e;
                        JOptionPane.showMessageDialog(
                                LoginScreen.this,
                                cause.getMessage() != null ? cause.getMessage() : "Invalid credentials!",
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            };
            worker.execute();
        };

        loginButton.addActionListener(e -> doLogin.run());
        passwordField.addActionListener(e -> doLogin.run());

        mainPanel.add(emailLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(loginButton);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}
