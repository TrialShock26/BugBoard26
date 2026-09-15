import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Hub extends BaseFrame {

    public Hub() {
        super("BugBoard26 - Dashboard");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createDashboardContent(), BorderLayout.CENTER);
    }

    //  SIDEBAR
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, new Color(209, 213, 219)
        ));
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
        // Admin-only sections are shown only when logged in as admin, so the app
        // remains fully usable by a regular user, without menu entries leading
        // to inaccessible screens.
        if (Session.isAdmin()) {
            menuItems.add(new String[]{"Admin Dashboard", "Admin"});
            menuItems.add(new String[]{"Reports", "Reports"});
            menuItems.add(new String[]{"Create Project", "CreateProject"});
        } else {
            menuItems.add(new String[]{"Choose Project", "ChooseProject"});
        }

        for (String[] item : menuItems) {
            JPanel menuItem = createMenuItem(item[0], item[1]);
            sidebar.add(menuItem);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel logoutItem = createMenuItem("Logout", "Logout");
        logoutItem.setBackground(new Color(254, 242, 242));
        sidebar.add(logoutItem);
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
            public void mouseEntered(MouseEvent evt) {
                item.setBackground(new Color(229, 231, 235));
            }

            public void mouseExited(MouseEvent evt) {
                item.setBackground(Color.WHITE);
            }

            public void mouseClicked(MouseEvent evt) {
                navigate(page);
            }
        });

        return item;
    }

    private void navigate(String page) {
        JFrame next = null;

        switch (page) {
            case "Dashboard":
                next = new Hub();
                break;
            case "Issues":
                next = new IssuesListScreen();
                break;
            case "NewIssue":
                next = new NewIssueScreen();
                break;
            case "Admin":
                next = new AdminDashboardScreen();
                break;
            case "Reports":
                next = new ReportsScreen();
                break;
            case "CreateProject":
                next = new CreateProjectScreen();
                break;
            case "ChooseProject":
                next = new ChooseProjectScreen();
                break;
            case "Logout":
                Session.clear();
                next = new LoginScreen();
                break;
        }

        if (next != null) {
            navigateTo(next);
        }
    }

    //  CONTENUTO
    private JPanel createDashboardContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(new Color(243, 244, 246));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JPanel welcomeRow = new JPanel(new BorderLayout());
        welcomeRow.setBackground(Color.WHITE);
        welcomeRow.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        welcomeRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Logo BugBoard26 accanto al titolo, come nelle altre schermate
        JPanel titleWithLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titleWithLogo.setBackground(Color.WHITE);

        JLabel brand;
        ImageIcon hubLogoIcon = AppLogo.icon(32);
        if (hubLogoIcon != null) {
            brand = new JLabel(hubLogoIcon);
        } else {
            brand = new JLabel("BugBoard26");
            brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
            brand.setForeground(new Color(220, 38, 38));
        }

        String name = Session.getName() != null ? Session.getName() : "User";
        JLabel welcomeLabel = new JLabel("Hi, " + name + " - Today's overview");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(17, 24, 39));

        titleWithLogo.add(brand);
        titleWithLogo.add(welcomeLabel);
        welcomeRow.add(titleWithLogo, BorderLayout.WEST);
        header.add(welcomeRow);

        content.add(header, BorderLayout.NORTH);

        // Board panel: Todo column, fed from the back-end (point 3: summary view)
        JPanel boardPanel = new JPanel(new GridLayout(1, 1, 20, 0));
        boardPanel.setBackground(new Color(243, 244, 246));

        List<Map<String, Object>> todoIssues;
        try {
            todoIssues = ApiClient.listIssues(null, "TODO", null, null, "createdAt");
        } catch (ApiClient.ApiException ex) {
            todoIssues = List.of();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }

        boardPanel.add(createColumn("Todo", new Color(156, 163, 175), todoIssues));
        content.add(boardPanel, BorderLayout.CENTER);

        // Punto 14: sezione suggerimento in fondo alla pagina, alla stessa larghezza
        // del resto del contenuto (BorderLayout.SOUTH occupa tutta la larghezza).
        JPanel suggestionBanner = buildSuggestionBanner();
        if (suggestionBanner != null) {
            content.add(suggestionBanner, BorderLayout.SOUTH);
        }

        return content;
    }

    private JPanel buildSuggestionBanner() {
        if (!"DEV".equals(Session.getRole())) return null;

        try {
            Map<String, Object> result = ApiClient.mySuggestions();
            boolean eligible = Boolean.TRUE.equals(result.get("eligible"));
            if (!eligible) return null;

            JPanel banner = new JPanel(new BorderLayout(10, 0));
            banner.setBackground(new Color(254, 242, 242));
            banner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 38, 38)),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            banner.setAlignmentX(Component.LEFT_ALIGNMENT);
            banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

            JLabel text = new JLabel("<html><body style='width: 100%'>You have the lowest workload on the team: "
                    + "go to the Issues screen and take on more issues to balance the team's overall "
                    + "workload.</body></html>");
            text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            text.setForeground(new Color(185, 28, 28));

            JButton goBtn = new JButton("Go to Issues");
            goBtn.setBackground(new Color(220, 38, 38));
            goBtn.setForeground(Color.WHITE);
            goBtn.setFocusPainted(false);
            goBtn.addActionListener(e -> navigateTo(new IssuesListScreen()));

            banner.add(text, BorderLayout.CENTER);
            banner.add(goBtn, BorderLayout.EAST);
            return banner;
        } catch (ApiClient.ApiException ex) {
            return null; // secondary notice: if unavailable, it doesn't block the dashboard
        }
    }

    private JPanel createColumn(String title, Color color, List<Map<String, Object>> issueList) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        column.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);

        JLabel countLabel = new JLabel(String.valueOf(issueList.size()));
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        countLabel.setForeground(color);
        countLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(countLabel, BorderLayout.EAST);
        column.add(headerPanel);
        column.add(Box.createVerticalStrut(10));

        for (Map<String, Object> issue : issueList) {
            JPanel card = createIssueCard(issue);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            column.add(card);
            column.add(Box.createVerticalStrut(8));
        }

        return column;
    }

    private JPanel createIssueCard(Map<String, Object> issue) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        String priority = (String) issue.get("priority");

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(Color.WHITE);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel((String) issue.get("title"));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(17, 24, 39));

        JLabel priorityLabel = new JLabel(priority == null ? "NONE" : priority);
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        priorityLabel.setForeground(priorityColor(priority));
        priorityLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(priorityColor(priority)),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));

        titleRow.add(titleLabel, BorderLayout.WEST);
        titleRow.add(priorityLabel, BorderLayout.EAST);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoPanel.setBackground(new Color(249, 250, 251));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String assignee = (String) issue.get("assigneeEmail");
        JLabel assigneeInfo = new JLabel(assignee == null ? "Unassigned" : "Assigned to: " + assignee);
        assigneeInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        assigneeInfo.setForeground(new Color(75, 85, 99));

        JLabel typeLabel = new JLabel(String.valueOf(issue.get("type")).toLowerCase());
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeLabel.setBackground(new Color(37, 99, 235));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setOpaque(true);
        typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        infoPanel.add(assigneeInfo);
        infoPanel.add(typeLabel);

        // Punto 10: etichette personalizzabili "chip"
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) issue.get("labels");
        JPanel labelsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        labelsPanel.setBackground(Color.WHITE);
        labelsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (labels != null) {
            for (String l : labels) {
                JLabel chip = new JLabel(l);
                chip.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                chip.setForeground(new Color(55, 65, 81));
                chip.setOpaque(true);
                chip.setBackground(new Color(229, 231, 235));
                chip.setBorder(BorderFactory.createEmptyBorder(1, 6, 1, 6));
                labelsPanel.add(chip);
            }
        }

        card.add(titleRow);
        card.add(infoPanel);
        card.add(labelsPanel);

        return card;
    }

    private Color priorityColor(String priority) {
        if (priority == null) return new Color(156, 163, 175);
        switch (priority) {
            case "NONE": return new Color(156, 163, 175);
            case "LOW": return new Color(34, 197, 94);
            case "MEDIUM": return new Color(59, 130, 246);
            case "HIGH": return new Color(249, 115, 22);
            case "CRITICAL": return new Color(220, 38, 38);
            default: return new Color(107, 114, 128);
        }
    }
}
