import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Point 7: "An admin dashboard is required, showing aggregated information about
 * bugs, such as the number of open bugs, those assigned per user, the average
 * resolution time (aggregated and per user) and other useful data." Extended to
 * also report per Team.
 *
 * Point 1: creating new user accounts (email, password, DEV/ADMIN role) is an
 * operation reserved to administrators, so it lives on this screen.
 */
public class AdminDashboardScreen extends BaseFrame {

    private JLabel openLabel, ongoingLabel, resolvedLabel, totalLabel, avgLabel;
    private DefaultTableModel perUserModel;
    private DefaultTableModel perTeamModel;
    private DefaultTableModel usersModel;

    public AdminDashboardScreen() {
        super("BugBoard26 - Admin Dashboard");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);

        if (!Session.isAdmin()) {
            add(accessDeniedPanel(), BorderLayout.CENTER);
        } else {
            add(createTabs(), BorderLayout.CENTER);
            loadDashboard();
            loadUsers();
        }
    }

    // ================= TOP BAR =================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 70));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        userPanel.setBackground(Color.WHITE);

        // BugBoard26 logo next to the title/user info, on every screen
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

    private JPanel accessDeniedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 244, 246));
        JLabel label = new JLabel("This section is restricted to administrators.");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(107, 114, 128));
        panel.add(label);
        return panel;
    }

    // ================= SIDEBAR =================
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
            if (item[1].equals("Admin")) menuItem.setBackground(new Color(254, 242, 242));
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
            case "NewIssue": next = new NewIssueScreen(); break;
            case "Admin": return; // gia' qui
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

    // ================= TABS =================
    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabs.addTab("Overview", createOverviewPanel());
        tabs.addTab("Users", createUsersPanel());
        return tabs;
    }

    // ================= OVERVIEW (point 7) =================
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel cards = new JPanel(new GridLayout(1, 5, 15, 0));
        cards.setBackground(new Color(243, 244, 246));

        openLabel = valueLabel("0");
        ongoingLabel = valueLabel("0");
        resolvedLabel = valueLabel("0");
        totalLabel = valueLabel("0");
        avgLabel = valueLabel("N/A");

        cards.add(statCard("Open bugs (todo)", openLabel, new Color(156, 163, 175)));
        cards.add(statCard("Ongoing", ongoingLabel, new Color(59, 130, 246)));
        cards.add(statCard("Resolved", resolvedLabel, new Color(34, 197, 94)));
        cards.add(statCard("Total issues", totalLabel, new Color(75, 85, 99)));
        cards.add(statCard("Avg. resolution time (h)", avgLabel, new Color(220, 38, 38)));

        panel.add(cards, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        tablesPanel.setBackground(new Color(243, 244, 246));

        String[] userColumns = {"User", "Email", "Assigned (total)", "Open now", "Avg. resolution time (h)"};
        perUserModel = new DefaultTableModel(userColumns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable userTable = new JTable(perUserModel);
        userTable.setRowHeight(28);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane userTableScroll = new JScrollPane(userTable);
        userTableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        userTableScroll.setPreferredSize(new Dimension(0, 180));

        JLabel userTableTitle = new JLabel("Statistics per user");
        userTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFocusPainted(false);
        refreshBtn.addActionListener(e -> loadDashboard());

        JPanel userTableHeaderRow = new JPanel(new BorderLayout());
        userTableHeaderRow.setBackground(new Color(243, 244, 246));
        userTableHeaderRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        userTableHeaderRow.add(userTableTitle, BorderLayout.WEST);
        userTableHeaderRow.add(refreshBtn, BorderLayout.EAST);

        // Point 17 (extended): admin reports also broken down by Team
        String[] teamColumns = {"Team", "Members", "Assigned (total)", "Open now", "Avg. resolution time (h)"};
        perTeamModel = new DefaultTableModel(teamColumns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable teamTable = new JTable(perTeamModel);
        teamTable.setRowHeight(28);
        teamTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane teamTableScroll = new JScrollPane(teamTable);
        teamTableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        teamTableScroll.setPreferredSize(new Dimension(0, 150));

        JLabel teamTableTitle = new JLabel("Statistics per team");
        teamTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tablesPanel.add(userTableHeaderRow);
        tablesPanel.add(Box.createVerticalStrut(8));
        tablesPanel.add(userTableScroll);
        tablesPanel.add(Box.createVerticalStrut(20));
        tablesPanel.add(teamTableTitle);
        tablesPanel.add(Box.createVerticalStrut(8));
        tablesPanel.add(teamTableScroll);

        panel.add(tablesPanel, BorderLayout.CENTER);
        return panel;
    }

    private JLabel valueLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        valueLabel.setForeground(color);
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(new Color(107, 114, 128));
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);
        return card;
    }

    private void loadDashboard() {
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() {
                return ApiClient.dashboard();
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void done() {
                try {
                    Map<String, Object> stats = get();
                    openLabel.setText(String.valueOf(stats.get("openCount")));
                    ongoingLabel.setText(String.valueOf(stats.get("ongoingCount")));
                    resolvedLabel.setText(String.valueOf(stats.get("resolvedCount")));
                    totalLabel.setText(String.valueOf(stats.get("totalCount")));
                    Object avg = stats.get("avgResolutionHoursOverall");
                    avgLabel.setText(avg == null ? "N/A" : String.valueOf(avg));

                    perUserModel.setRowCount(0);
                    List<Map<String, Object>> perUser = (List<Map<String, Object>>) stats.get("perUser");
                    for (Map<String, Object> row : perUser) {
                        Object userAvg = row.get("avgResolutionHours");
                        perUserModel.addRow(new Object[]{
                                row.get("name"), row.get("email"), row.get("assignedCount"),
                                row.get("openCount"), userAvg == null ? "N/A" : userAvg
                        });
                    }

                    // Point 17 (extended): per-team breakdown
                    perTeamModel.setRowCount(0);
                    List<Map<String, Object>> perTeam = (List<Map<String, Object>>) stats.get("perTeam");
                    if (perTeam != null) {
                        for (Map<String, Object> row : perTeam) {
                            Object teamAvg = row.get("avgResolutionHours");
                            perTeamModel.addRow(new Object[]{
                                    row.get("team"), row.get("memberCount"), row.get("assignedCount"),
                                    row.get("openCount"), teamAvg == null ? "N/A" : teamAvg
                            });
                        }
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(AdminDashboardScreen.this,
                            "Error loading the dashboard: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // ================= USERS (point 1) =================
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // User creation form
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JTextField nameField = new JTextField(12);
        JTextField emailField = new JTextField(16);
        JPasswordField passwordField = new JPasswordField(10);
        // I ruoli disponibili sono ADMIN e DEV (niente READONLY)
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"DEV", "ADMIN"});
        JComboBox<String> teamCombo = new JComboBox<>(new String[]{"FRONTEND", "BACKEND", "MOBILE", "QA"});
        teamCombo.setEnabled(true);
        JButton createBtn = new JButton("Create user");
        createBtn.setBackground(new Color(220, 38, 38));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);

        // Il team ha senso solo per i DEV: disabilitato quando si crea un ADMIN
        roleCombo.addActionListener(e -> teamCombo.setEnabled("DEV".equals(roleCombo.getSelectedItem())));

        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Password:")); form.add(passwordField);
        form.add(new JLabel("Role:")); form.add(roleCombo);
        form.add(new JLabel("Team:")); form.add(teamCombo);
        form.add(createBtn);

        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String team = "DEV".equals(role) ? (String) teamCombo.getSelectedItem() : null;

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Email and password are required.",
                        "Missing fields", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                ApiClient.createUser(email, password, name, role, team);
                nameField.setText(""); emailField.setText(""); passwordField.setText("");
                loadUsers();
                JOptionPane.showMessageDialog(this, "User created successfully.",
                        "User created", JOptionPane.INFORMATION_MESSAGE);
            } catch (ApiClient.ApiException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(form, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Role", "Team"};
        usersModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(usersModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        panel.add(tableScroll, BorderLayout.CENTER);

        return panel;
    }

    private void loadUsers() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return ApiClient.listUsers();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> users = get();
                    usersModel.setRowCount(0);
                    for (Map<String, Object> u : users) {
                        Object team = u.get("team");
                        usersModel.addRow(new Object[]{u.get("id"), u.get("name"), u.get("email"),
                                u.get("role"), team == null ? "-" : team});
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(AdminDashboardScreen.this,
                            "Error loading users: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
