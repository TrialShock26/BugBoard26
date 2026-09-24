package frontend.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import frontend.controller.IssueController;
import frontend.controller.Session;
import frontend.dto.IssueDTO;
import frontend.dto.IssuePriority;
import frontend.dto.IssueStatus;
import frontend.dto.IssueType;
import frontend.exception.ApiException;

public class IssuesListScreen extends BaseFrame {

    private JPanel listPanel;
    private JComboBox<ComboItem> typeFilter;
    private JComboBox<ComboItem> statusFilter;
    private JComboBox<ComboItem> priorityFilter;
    private JComboBox<ComboItem> sortCombo;

    public IssuesListScreen() {
        super("BugBoard26 - Issues");

        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);
        add(createIssuesContent(), BorderLayout.CENTER);

        reloadIssues();
    }

    //  SIDEBAR
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
            if (item[1].equals("Issues")) menuItem.setBackground(new Color(254, 242, 242));
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
            case "Issues": return; // gia' qui
            case "NewIssue": next = new NewIssueScreen(); break;
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

        // Logo BugBoard26 accanto al titolo/info utente, in ogni schermata
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

    private JPanel createIssuesContent() {
        JPanel container = new JPanel(new BorderLayout(0, 15));
        container.setBackground(new Color(243, 244, 246));
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        container.add(createFilterBar(), BorderLayout.NORTH);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 244, 246));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        typeFilter = new JComboBox<>(new ComboItem[]{
                new ComboItem(null, "All types"),
                new ComboItem("BUG", "Bug"),
                new ComboItem("QUESTION", "Question"),
                new ComboItem("DOCUMENTATION", "Documentation"),
                new ComboItem("FEATURE", "Feature")
        });
        statusFilter = new JComboBox<>(new ComboItem[]{
                new ComboItem(null, "All statuses"),
                new ComboItem("TODO", "Todo"),
                new ComboItem("ONGOING", "Ongoing"),
                new ComboItem("RESOLVED", "Resolved")
        });
        priorityFilter = new JComboBox<>(new ComboItem[]{
                new ComboItem(null, "All priorities"),
                new ComboItem("NONE", "None"),
                new ComboItem("LOW", "Low"),
                new ComboItem("MEDIUM", "Medium"),
                new ComboItem("HIGH", "High"),
                new ComboItem("CRITICAL", "Critical")
        });
        sortCombo = new JComboBox<>(new ComboItem[]{
                new ComboItem("createdAt", "Most recent"),
                new ComboItem("priority", "Priority"),
                new ComboItem("title", "Title (A-Z)"),
                new ComboItem("status", "Status")
        });

        JButton applyBtn = new JButton("Apply filters");
        applyBtn.setBackground(new Color(220, 38, 38));
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFocusPainted(false);
        applyBtn.addActionListener(e -> reloadIssues());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> reloadIssues());

        bar.add(new JLabel("Filter:"));
        bar.add(typeFilter);
        bar.add(statusFilter);
        bar.add(priorityFilter);
        bar.add(new JLabel("  Sort:"));
        bar.add(sortCombo);
        bar.add(applyBtn);
        bar.add(refreshBtn);

        return bar;
    }

    private void reloadIssues() {
        listPanel.removeAll();
        String typeStr = ((ComboItem) typeFilter.getSelectedItem()).value;
        String statusStr = ((ComboItem) statusFilter.getSelectedItem()).value;
        String priorityStr = ((ComboItem) priorityFilter.getSelectedItem()).value;
        String sort = ((ComboItem) sortCombo.getSelectedItem()).value;

        IssueType type = typeStr == null ? null : IssueType.valueOf(typeStr);
        IssueStatus status = statusStr == null ? null : IssueStatus.valueOf(statusStr);
        IssuePriority priority = priorityStr == null ? null : IssuePriority.valueOf(priorityStr);

        try {
            List<IssueDTO> issues = IssueController.listIssues(type, status, priority, sort);
            if (issues.isEmpty()) {
                JLabel empty = new JLabel("No issues found matching the selected filters.");
                empty.setForeground(new Color(107, 114, 128));
                empty.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 0));
                listPanel.add(empty);
            } else {
                for (IssueDTO issue : issues) {
                    listPanel.add(createIssueCard(issue));
                    listPanel.add(Box.createVerticalStrut(15));
                }
            }
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void handleIssue(String issueId) {
        try {
            IssueController.handleIssue(issueId);
            reloadIssues();
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            reloadIssues();
        }
    }

    private void resolveIssue(String issueId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Mark issue " + issueId + " as resolved?",
                "Resolve issue", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            IssueController.resolveIssue(issueId);
            reloadIssues();
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            reloadIssues();
        }
    }

    private void deleteIssue(String issueId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Delete issue " + issueId + "? This cannot be undone.",
                "Delete issue", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            IssueController.deleteIssue(issueId);
            reloadIssues();
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewImage(String issueId) {
        byte[] bytes = null;
        try {
            bytes = IssueController.getIssueImage(issueId);
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (bytes == null) {
            JOptionPane.showMessageDialog(this, "This issue has no attached image.",
                    "No image", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ImageIcon icon = new ImageIcon(bytes);
        JDialog dialog = new JDialog(this, "Image for issue " + issueId, true);
        dialog.add(new JScrollPane(new JLabel(icon)));
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createIssueCard(IssueDTO issue) {
        String id = issue.getId();
        IssueStatus status = issue.getStatus();
        IssueType type = issue.getType();
        IssuePriority priority = issue.getPriority();
        String assignee = issue.getAssigneeEmail();
        List<String> labels = issue.getLabels();

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        // Riga superiore: titolo + tag
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(issue.getTitle() + "  (" + id + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(31, 41, 55));

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tagsPanel.setBackground(Color.WHITE);
        tagsPanel.add(createTag(type.name(), new Color(37, 99, 235)));
        tagsPanel.add(createTag(priority == null ? "NONE" : priority.name(), getPriorityColor(priority)));
        tagsPanel.add(createTag(status.name(), getStatusColor(status)));

        topRow.add(titleLabel, BorderLayout.WEST);
        topRow.add(tagsPanel, BorderLayout.EAST);
        card.add(topRow);

        String description = String.valueOf(issue.getDescription());
        if (description.length() > 140) description = description.substring(0, 140) + "...";
        JLabel descLabel = new JLabel("<html><body style='width:700px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(75, 85, 99));
        descLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        card.add(descLabel);

        JPanel infoRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        infoRow.setBackground(Color.WHITE);
        JLabel assigneeLabel = new JLabel(assignee == null ? "Unassigned" : "Assigned to " + assignee);
        assigneeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        assigneeLabel.setForeground(new Color(107, 114, 128));
        infoRow.add(assigneeLabel);
        if (labels != null) {
            for (String l : labels) infoRow.add(createChip(l));
        }
        card.add(infoRow);

        JPanel actionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        actionsRow.setBackground(Color.WHITE);

        boolean canHandle = Session.isDev() && type == IssueType.BUG && assignee == null;
        if (canHandle) {
            JButton handleBtn = smallButton("Handle");
            handleBtn.addActionListener(e -> handleIssue(id));
            actionsRow.add(handleBtn);
        }

        boolean canResolve = (Session.isAdmin() || Session.getEmail().equalsIgnoreCase(assignee))
                && status != IssueStatus.RESOLVED;
        if (canResolve) {
            JButton resolveBtn = smallButton("Resolve");
            resolveBtn.addActionListener(e -> resolveIssue(id));
            actionsRow.add(resolveBtn);
        }

        JButton imageBtn = smallButton("View image");
        imageBtn.addActionListener(e -> viewImage(id));
        actionsRow.add(imageBtn);

        JButton labelBtn = smallButton("+ Label");
        labelBtn.addActionListener(e -> openAddLabelDialog(id));
        actionsRow.add(labelBtn);

        if (Session.isAdmin()) {
            JButton deleteBtn = smallButton("Delete");
            deleteBtn.addActionListener(e -> deleteIssue(id));
            actionsRow.add(deleteBtn);
        }

        card.add(actionsRow);

        return card;
    }

    private JButton smallButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setFocusPainted(false);
        b.setMargin(new Insets(3, 8, 3, 8));
        return b;
    }

    private JLabel createTag(String text, Color color) {
        JLabel tag = new JLabel(text);
        tag.setFont(new Font("Segoe UI", Font.BOLD, 11));
        tag.setForeground(color);
        tag.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tag;
    }

    private JLabel createChip(String text) {
        JLabel chip = new JLabel(text);
        chip.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        chip.setForeground(new Color(55, 65, 81));
        chip.setOpaque(true);
        chip.setBackground(new Color(229, 231, 235));
        chip.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        return chip;
    }

    private Color getPriorityColor(IssuePriority priority) {
        if (priority == null) return new Color(107, 114, 128);
        switch (priority) {
            case NONE: return new Color(107, 114, 128);
            case LOW: return new Color(34, 197, 94);
            case MEDIUM: return new Color(59, 130, 246);
            case HIGH: return new Color(234, 179, 8);
            case CRITICAL: return new Color(220, 38, 38);
            default: return new Color(107, 114, 128);
        }
    }

    private Color getStatusColor(IssueStatus status) {
        switch (status) {
            case TODO: return new Color(156, 163, 175);
            case ONGOING: return new Color(59, 130, 246);
            case RESOLVED: return new Color(34, 197, 94);
            default: return new Color(107, 114, 128);
        }
    }

    private void openAddLabelDialog(String issueId) {
        String label = JOptionPane.showInputDialog(this, "New label (e.g. frontend, urgent, security):",
                "Add label", JOptionPane.PLAIN_MESSAGE);
        if (label != null && !label.trim().isEmpty()) {
            try {
                IssueController.addLabel(issueId, label.trim());
                reloadIssues();
            } catch (ApiException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ComboItem {
        final String value;
        final String label;
        ComboItem(String value, String label) { this.value = value; this.label = label; }
        @Override public String toString() { return label; }
    }
}
