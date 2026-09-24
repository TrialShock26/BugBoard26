package frontend.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import frontend.controller.IssueController;
import frontend.controller.Session;
import frontend.dto.IssueDTO;
import frontend.dto.IssuePriority;
import frontend.dto.IssueType;


public class NewIssueScreen extends BaseFrame {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<String> typeCombo;
    private JComboBox<String> priorityCombo;
    private JTextField labelsField;
    private JLabel imageStatusLabel;
    private File selectedImage;
    private JButton submitButton;

    public NewIssueScreen() {
        super("BugBoard26 - New Issue");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);
        add(createFormContent(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        JLabel logo;
        ImageIcon sidebarLogoIcon = AppLogo.full(90);
        if (sidebarLogoIcon != null) {
            logo = new JLabel(sidebarLogoIcon);
        } else {
            logo = new JLabel("BugBoard26");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
            logo.setForeground(new Color(220, 38, 38));
        }
        logo.setBorder(BorderFactory.createEmptyBorder(25, 0, 20, 0));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        List<String[]> menuItems = new ArrayList<>();
        menuItems.add(new String[]{"Dashboard", "Dashboard"});
        menuItems.add(new String[]{"Issues", "Issues"});
        menuItems.add(new String[]{"New Issue", "NewIssue"});
        if (Session.isAdmin()) {
            menuItems.add(new String[]{"Admin Dashboard", "Admin"});
            menuItems.add(new String[]{"Reports", "Reports"});
            menuItems.add(new String[]{"Create Project", "CreateProject"});
        } else {
            menuItems.add(new String[]{"Choose Project", "ChooseProject"});
        }

        for (String[] item : menuItems) {
            JPanel menuItem = createMenuItem(item[0], item[1]);
            if (item[1].equals("NewIssue")) menuItem.setBackground(new Color(254, 242, 242));
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

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { item.setBackground(new Color(229, 231, 235)); }
            public void mouseExited(MouseEvent evt) { item.setBackground(Color.WHITE); }
            public void mouseClicked(MouseEvent evt) { handleNavigation(page); }
        });

        return item;
    }

    private void handleNavigation(String page) {
        JFrame next = null;
        switch (page) {
            case "Dashboard": next = new Hub(); break;
            case "Issues": next = new IssuesListScreen(); break;
            case "NewIssue": return; // gia' qui
            case "Admin": next = new AdminDashboardScreen(); break;
            case "Reports": next = new ReportsScreen(); break;
            case "CreateProject": next = new CreateProjectScreen(); break;
            case "ChooseProject": next = new ChooseProjectScreen(); break;
            case "Logout":
                Session.clear();
                next = new LoginScreen();
                break;
        }
        if (next != null) {
            navigateTo(next);
        }
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        userPanel.setBackground(Color.WHITE);

        JLabel brand;
        ImageIcon topBarLogoIcon = AppLogo.icon(28);
        if (topBarLogoIcon != null) {
            brand = new JLabel(topBarLogoIcon);
        } else {
            brand = new JLabel("BugBoard26");
            brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
            brand.setForeground(new Color(220, 38, 38));
        }

        JLabel avatar = new JLabel("\u25CF");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        avatar.setForeground(new Color(220, 38, 38));

        String roleLabel = Session.isAdmin() ? "Admin" : "Dev";
        JLabel userLabel = new JLabel((Session.getName() != null ? Session.getName() : "User") + " (" + roleLabel + ")");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(75, 85, 85));

        userPanel.add(brand);
        userPanel.add(avatar);
        userPanel.add(userLabel);

        topBar.add(userPanel, BorderLayout.WEST);
        return topBar;
    }

    private JPanel createFormContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(243, 244, 246));
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));
        formCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        formCard.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));

        JLabel header = new JLabel("Report a new issue");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(17, 24, 39));
        formCard.add(header);
        formCard.add(Box.createVerticalStrut(25));

        formCard.add(fieldLabel("Title *"));
        titleField = new JTextField();
        styleTextComponent(titleField);
        formCard.add(titleField);
        formCard.add(Box.createVerticalStrut(18));

        formCard.add(fieldLabel("Description *"));
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        formCard.add(descScroll);
        formCard.add(Box.createVerticalStrut(18));

        JPanel typeRow = new JPanel();
        typeRow.setLayout(new BoxLayout(typeRow, BoxLayout.Y_AXIS));
        typeRow.setBackground(Color.WHITE);
        typeRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        typeRow.add(fieldLabel("Type *"));
        typeCombo = new JComboBox<>(new String[]{"Select a type...", "BUG", "QUESTION", "DOCUMENTATION", "FEATURE"});
        styleTextComponent(typeCombo);
        typeRow.add(typeCombo);
        formCard.add(typeRow);
        formCard.add(Box.createVerticalStrut(18));

        JPanel priorityRow = new JPanel();
        priorityRow.setLayout(new BoxLayout(priorityRow, BoxLayout.Y_AXIS));
        priorityRow.setBackground(Color.WHITE);
        priorityRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        priorityRow.add(fieldLabel("Priority (optional)"));
        priorityCombo = new JComboBox<>(new String[]{"NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL"});
        styleTextComponent(priorityCombo);
        priorityRow.add(priorityCombo);
        formCard.add(priorityRow);
        formCard.add(Box.createVerticalStrut(18));

        formCard.add(fieldLabel("Labels (comma-separated, optional)"));
        labelsField = new JTextField();
        styleTextComponent(labelsField);
        formCard.add(labelsField);
        formCard.add(Box.createVerticalStrut(18));

        formCard.add(fieldLabel("Attached image (optional)"));
        JPanel imageRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imageRow.setBackground(Color.WHITE);
        imageRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton attachBtn = new JButton("Choose image...");
        attachBtn.setFocusPainted(false);
        imageStatusLabel = new JLabel("No image selected");
        imageStatusLabel.setForeground(new Color(107, 114, 128));
        imageStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        attachBtn.addActionListener(e -> chooseImage());
        imageRow.add(attachBtn);
        imageRow.add(imageStatusLabel);
        formCard.add(imageRow);
        formCard.add(Box.createVerticalStrut(30));

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonsRow.setBackground(Color.WHITE);
        buttonsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitButton = new JButton("Create Issue");
        submitButton.setBackground(new Color(220, 38, 38));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        submitButton.addActionListener(e -> submit());

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> navigateTo(new IssuesListScreen()));

        buttonsRow.add(submitButton);
        buttonsRow.add(cancelButton);
        formCard.add(buttonsRow);

        content.add(formCard, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(75, 85, 85));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return l;
    }

    private void styleTextComponent(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (jpg, jpeg, png, gif)", "jpg", "jpeg", "png", "gif"));
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImage = chooser.getSelectedFile();
            imageStatusLabel.setText(selectedImage.getName());
        }
    }

    private void submit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title and description are required.",
                    "Missing fields", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String typeStr = (String) typeCombo.getSelectedItem();
        if (typeCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select an issue type.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }
        IssueType type = IssueType.valueOf(typeStr);

        IssuePriority priority = IssuePriority.valueOf((String) priorityCombo.getSelectedItem());

        List<String> labels = new ArrayList<>();
        String labelsText = labelsField.getText().trim();
        if (!labelsText.isEmpty()) {
            for (String l : labelsText.split(",")) {
                if (!l.trim().isEmpty()) labels.add(l.trim());
            }
        }

        String imageBase64 = null;
        if (selectedImage != null) {
            try {
                byte[] bytes = Files.readAllBytes(selectedImage.toPath());
                imageBase64 = Base64.getEncoder().encodeToString(bytes);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Could not read the selected image: " + e.getMessage(),
                        "Error", JOptionPane.WARNING_MESSAGE);
            }
        }

        submitButton.setEnabled(false);
        submitButton.setText("Creating...");

        String finalImageBase64 = imageBase64;
        SwingWorker<IssueDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected IssueDTO doInBackground() {
                return IssueController.createIssue(title, description, type, priority, labels, finalImageBase64);
            }

            @Override
            protected void done() {
                submitButton.setEnabled(true);
                submitButton.setText("Create Issue");
                try {
                    IssueDTO created = get();
                    JOptionPane.showMessageDialog(NewIssueScreen.this,
                            "Issue " + created.getId() + " created successfully (status: TODO).",
                            "Issue created", JOptionPane.INFORMATION_MESSAGE);
                    navigateTo(new IssuesListScreen());
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(NewIssueScreen.this,
                            "Error creating the issue: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
