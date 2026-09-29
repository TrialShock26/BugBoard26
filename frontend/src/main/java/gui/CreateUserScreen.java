package gui;

import controller.Session;
import controller.UserController;
import dto.UserRole;
import exception.ApiException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class CreateUserScreen extends BaseFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<UserRole> roleCombo;

    public CreateUserScreen() {
        super("BugBoard26 - Create User");
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);
        add(Session.isAdmin() ? createContent() : accessDeniedPanel(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        JLabel logo;
        ImageIcon icon = AppLogo.full(90);
        if (icon != null) logo = new JLabel(icon);
        else {
            logo = new JLabel("BugBoard26");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
            logo.setForeground(new Color(220, 38, 38));
        }
        logo.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        List<String[]> items = new ArrayList<>();
        items.add(new String[]{"My Issues", "My Issues"});
        items.add(new String[]{"List Issues", "List Issues"});
        items.add(new String[]{"New Issue", "NewIssue"});
        items.add(new String[]{"Choose Project", "ChooseProject"});
        if (Session.isAdmin()) {
            items.add(new String[]{"Dashboard", "Admin"});
            items.add(new String[]{"Reports", "Reports"});
            items.add(new String[]{"Create Project", "CreateProject"});
            items.add(new String[]{"Create User", "CreateUser"});
        }
        for (String[] item : items) {
            if (item[1].equals("Admin")) addAdminSectionDivider(sidebar);
            JPanel menuItem = createMenuItem(item[0], item[1]);
            if (item[1].equals("CreateUser")) markCurrentPage(menuItem);
            sidebar.add(menuItem);
            sidebar.add(Box.createVerticalStrut(5));
        }
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createMenuItem("Logout", "Logout"));
        sidebar.add(Box.createVerticalStrut(20));
        return sidebar;
    }

    private JPanel createMenuItem(String text, String page) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(250, 45));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boolean current = page.equals("CreateUser");
        if (current) markCurrentPage(item);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(55, 65, 81));
        item.add(textLabel);
        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!current) item.setBackground(new Color(229, 231, 235));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!current) item.setBackground(Color.WHITE);
            }
            @Override public void mouseClicked(MouseEvent e) { navigate(page); }
        });
        return item;
    }

    private void addAdminSectionDivider(JPanel sidebar) {
        sidebar.add(Box.createVerticalStrut(6));
        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setForeground(new Color(229, 231, 235));
        divider.setMaximumSize(new Dimension(210, 1));
        divider.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(divider);
        sidebar.add(Box.createVerticalStrut(6));
    }

    private void markCurrentPage(JPanel item) {
        item.setBackground(new Color(254, 242, 242));
        item.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(220, 38, 38)));
    }

    private void navigate(String page) {
        JFrame next = null;
        switch (page) {
            case "Dashboard": next = new Hub(); break;
            case "My Issues": next = new Hub(); break;
            case "List Issues": next = new IssuesListScreen(); break;
            case "NewIssue": next = new NewIssueScreen(); break;
            case "Admin": next = new AdminDashboardScreen(); break;
            case "Reports": next = new ReportsScreen(); break;
            case "CreateProject": next = new CreateProjectScreen(); break;
            case "CreateUser": return;
            case "ChooseProject": next = new ChooseProjectScreen(); break;
            case "Logout": Session.clear(); next = new LoginScreen(); break;
        }
        if (next != null) navigateTo(next);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        userPanel.setBackground(Color.WHITE);
        ImageIcon icon = AppLogo.icon(28);
        JLabel brand = icon == null ? new JLabel("BugBoard26") : new JLabel(icon);
        if (icon == null) {
            brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
            brand.setForeground(new Color(220, 38, 38));
        }
        JLabel avatar = new JLabel("\u25CF");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        avatar.setForeground(new Color(220, 38, 38));
        String name = Session.getName() == null ? "User" : Session.getName();
        JLabel user = new JLabel(name + " (Admin)");
        user.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        user.setForeground(new Color(75, 85, 85));
        userPanel.add(brand);
        userPanel.add(avatar);
        userPanel.add(user);
        topBar.add(userPanel, BorderLayout.WEST);
        return topBar;
    }

    private JPanel accessDeniedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 244, 246));
        JLabel message = new JLabel("Creating users is restricted to administrators.");
        message.setFont(new Font("Segoe UI", Font.BOLD, 16));
        message.setForeground(new Color(107, 114, 128));
        panel.add(message);
        return panel;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(243, 244, 246));
        content.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Create User");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(17, 24, 39));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(title);
        JLabel hint = new JLabel("Create a new account and choose its access role.");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hint.setForeground(new Color(107, 114, 128));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(6));
        form.add(hint);
        form.add(Box.createVerticalStrut(22));

        addField(form, "Email *");
        emailField = new JTextField();
        styleField(emailField);
        form.add(emailField);
        form.add(Box.createVerticalStrut(16));

        addField(form, "Password *");
        passwordField = new JPasswordField();
        styleField(passwordField);
        form.add(passwordField);
        form.add(Box.createVerticalStrut(16));

        addField(form, "Role *");
        roleCombo = new JComboBox<>(UserRole.values());
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setMaximumSize(new Dimension(420, 36));
        roleCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(roleCombo);
        form.add(Box.createVerticalStrut(22));

        JButton createButton = new JButton("Create User");
        createButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        createButton.setBackground(new Color(220, 38, 38));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        createButton.addActionListener(e -> createUser());
        form.add(createButton);

        content.add(form, BorderLayout.NORTH);
        return content;
    }

    private void addField(JPanel form, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(75, 85, 85));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(label);
        form.add(Box.createVerticalStrut(6));
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setMaximumSize(new Dimension(420, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void createUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        UserRole role = (UserRole) roleCombo.getSelectedItem();
        if (email.isEmpty() || password.isBlank() || role == null) {
            JOptionPane.showMessageDialog(this, "Enter an email address, a password, and a role.",
                    "Required information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(this, "Enter a valid email address (for example, name@example.com).",
                    "Invalid email", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            UserController.createUser(email, password, role);
            emailField.setText("");
            passwordField.setText("");
            roleCombo.setSelectedItem(UserRole.DEV);
            JOptionPane.showMessageDialog(this, "The account for " + email + " was created successfully.",
                    "User created", JOptionPane.INFORMATION_MESSAGE);
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, "Could not create the account. " + ex.getMessage(),
                    "User creation failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
