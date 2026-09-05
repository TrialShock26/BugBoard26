import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IssuesListScreen extends BaseFrame {

    public IssuesListScreen() {
        super("BugBoard26 - My Issues");

        setLocationRelativeTo(null);
        setFullscreen(isFullscreen);

        setLayout(new BorderLayout());

        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);
        add(createIssuesContent(), BorderLayout.CENTER);
    }

    // ================= SIDEBAR =================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(229, 231, 235)));

        JLabel logo = new JLabel("BugBoard26");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        logo.setForeground(new Color(220, 38, 38));
        logo.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        String[][] menuItems = {
                {"📊 Dashboard", "Dashboard"},
                {"🐛 Issues", "Issues"},
                {"➕ Nuova Issue", "NewIssue"},
                {"👑 Admin Dashboard", "Admin"},
                {"📈 Reports", "Reports"}
        };

        for (String[] item : menuItems) {
            JPanel menuItem = createMenuItem(item[0], item[1]);

            if (item[1].equals("Issues")) {
                menuItem.setBackground(new Color(254, 242, 242));
            }

            sidebar.add(menuItem);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createMenuItem("🚪 Logout", "Logout"));
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JPanel createMenuItem(String text, String page) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(280, 45));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));

        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                item.setBackground(new Color(229, 231, 235));
            }

            public void mouseExited(MouseEvent evt) {
                item.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent evt) {
                handleNavigation(page, item);
            }
        });

        return item;
    }

    // ================= NAVIGATION =================
    private void handleNavigation(String page, Component source) {

        JFrame next = null;

        switch (page) {

            case "Dashboard":
                next = new Hub();
                break;

            case "Issues":
                return; // già qui

            case "NewIssue":
                JOptionPane.showMessageDialog(null, "Nuova Issue non ancora implementata");
                return;

            case "Admin":
                JOptionPane.showMessageDialog(null, "Admin non ancora implementato");
                return;

            case "Reports":
                JOptionPane.showMessageDialog(null, "Reports non ancora implementato");
                return;

            case "Logout":
                new LoginScreen().setVisible(true);
                closeWindow(source);
                return;
        }

        if (next != null) {
            //next.setFullscreen(BaseFrame.isFullscreen);
            next.setVisible(true);
            closeWindow(source);
        }
    }

    private void closeWindow(Component c) {
        Window w = SwingUtilities.getWindowAncestor(c);
        if (w != null) w.dispose();
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        userPanel.setBackground(Color.WHITE);

        JLabel avatar = new JLabel("👤");
        avatar.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel userLabel = new JLabel("Mario Rossi (Admin)");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userLabel.setForeground(new Color(75, 85, 85));

        userPanel.add(avatar);
        userPanel.add(userLabel);

        topBar.add(userPanel, BorderLayout.WEST);

        return topBar;
    }

    // ================= CONTENT =================
    private JPanel createIssuesContent() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(new Color(243, 244, 246));
        container.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(new Color(243, 244, 246));

        listPanel.add(createIssueCard("Bug nel login", 5, "IN PROGRESS"));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createIssueCard("Dark mode", 2, "TODO"));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createIssueCard("Database timeout", 4, "IN PROGRESS"));
        listPanel.add(Box.createVerticalStrut(15));
        listPanel.add(createIssueCard("Export PDF", 1, "RESOLVED"));

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        container.add(scrollPane, BorderLayout.CENTER);
        return container;
    }

    // ================= ISSUE CARD =================
    private JPanel createIssueCard(String title, int priority, String status) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titleLabel.setForeground(new Color(31, 41, 55));

        JPanel tagsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tagsPanel.setBackground(Color.WHITE);

        JLabel priorityLabel = createTag("P" + priority, getPriorityColor(priority));
        JLabel statusLabel = createTag(status, getStatusColor(status));

        tagsPanel.add(priorityLabel);
        tagsPanel.add(statusLabel);

        card.add(titleLabel, BorderLayout.WEST);
        card.add(tagsPanel, BorderLayout.EAST);

        return card;
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

    private Color getPriorityColor(int priority) {
        switch (priority) {
            case 1: return new Color(34, 197, 94);
            case 2: return new Color(59, 130, 246);
            case 3: return new Color(234, 179, 8);
            case 4: return new Color(249, 115, 22);
            case 5: return new Color(220, 38, 38);
            default: return new Color(107, 114, 128);
        }
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "TODO": return new Color(156, 163, 175);
            case "IN PROGRESS": return new Color(59, 130, 246);
            case "RESOLVED": return new Color(34, 197, 94);
            default: return new Color(107, 114, 128);
        }
    }
}