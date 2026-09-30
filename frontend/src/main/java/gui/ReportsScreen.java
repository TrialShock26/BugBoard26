package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import controller.ReportController;
import controller.Session;
import dto.*;


public class ReportsScreen extends BaseFrame {
    private JComboBox<String> monthCombo;
    private JComboBox<ProjectDTO> projectCombo;
    private JSpinner yearSpinner;
    private JLabel totalBugsLabel, handledBugsLabel, avgLabel;
    private DefaultTableModel perUserModel;
    private DefaultTableModel perTeamModel;

    public ReportsScreen() {
        super("BugBoard26 - Reports");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);

        if (!Session.isAdmin()) {
            add(accessDeniedPanel(), BorderLayout.CENTER);
        } else {
            add(createContent(), BorderLayout.CENTER);
            loadProjects();
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

    private JPanel accessDeniedPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 244, 246));
        JLabel label = new JLabel("This section is restricted to administrators.");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(107, 114, 128));
        panel.add(label);
        return panel;
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
        menuItems.add(new String[]{"My Issues", "My Issues"});
        menuItems.add(new String[]{"List Issues", "List Issues"});
        menuItems.add(new String[]{"New Issue", "NewIssue"});
        menuItems.add(new String[]{"Choose Project", "ChooseProject"});
        if (Session.isAdmin()) {
            menuItems.add(new String[]{"Dashboard", "Admin"});
            menuItems.add(new String[]{"Reports", "Reports"});
            menuItems.add(new String[]{"Create Project", "CreateProject"});
            menuItems.add(new String[]{"Create User", "CreateUser"});
        }
        for (String[] item : menuItems) {
            if (item[1].equals("Admin")) addAdminSectionDivider(sidebar);
            JPanel menuItem = createMenuItem(item[0], item[1]);
            if (item[1].equals("Reports")) markCurrentPage(menuItem);
            sidebar.add(menuItem);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createMenuItem("Logout", "Logout"));
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
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

    private JPanel createMenuItem(String text, String page) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(250, 45));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boolean current = page.equals("Reports");
        if (current) markCurrentPage(item);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { if (!current) item.setBackground(new Color(229, 231, 235)); }
            public void mouseExited(MouseEvent evt) { if (!current) item.setBackground(Color.WHITE); }
            public void mouseClicked(MouseEvent evt) { handleNavigation(page); }
        });

        return item;
    }

    private void markCurrentPage(JPanel item) {
        item.setBackground(new Color(254, 242, 242));
        item.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(220, 38, 38)));
    }

    private void handleNavigation(String page) {
        JFrame next = null;
        switch (page) {
            case "Dashboard": next = Session.isAdmin() ? new AdminDashboardScreen() : new Hub(); break;
            case "My Issues": next = new Hub(); break;
            case "List Issues": next = new IssuesListScreen(); break;
            case "NewIssue": next = new NewIssueScreen(); break;
            case "Admin": next = new AdminDashboardScreen(); break;
            case "Reports": return; // gia' qui
            case "CreateProject": next = new CreateProjectScreen(); break;
            case "CreateUser": next = new CreateUserScreen(); break;
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

    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selector.setBackground(Color.WHITE);
        selector.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        Calendar now = Calendar.getInstance();
        String[] months = {"january", "february", "march", "april", "may", "june",
                "july", "august", "september", "october", "november", "december"};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(now.get(Calendar.MONTH));

        projectCombo = new JComboBox<>();
        projectCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value instanceof ProjectDTO ? ((ProjectDTO) value).getName() : "Select a project");
                return this;
            }
        });

        yearSpinner = new JSpinner(new SpinnerNumberModel(now.get(Calendar.YEAR), 2020, 2100, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));

        JButton generateBtn = new JButton("↻");
        generateBtn.setToolTipText("Update");
        generateBtn.setFocusPainted(false);
        generateBtn.addActionListener(e -> generateReport());

        JButton submitBtn = new JButton("Send");
        submitBtn.setBackground(new Color(220, 38, 38));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.addActionListener(e -> generateReport());

        selector.add(new JLabel("Project:")); selector.add(projectCombo);
        selector.add(new JLabel("Month:")); selector.add(monthCombo);
        selector.add(new JLabel("Year:")); selector.add(yearSpinner);
        selector.add(generateBtn);
        selector.add(submitBtn);

        panel.add(selector, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 20));
        middle.setBackground(new Color(243, 244, 246));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setBackground(new Color(243, 244, 246));
        totalBugsLabel = valueLabel("0");
        handledBugsLabel = valueLabel("0");
        avgLabel = valueLabel("N/A");
        cards.add(statCard("Total bugs", totalBugsLabel, new Color(59, 130, 246)));
        cards.add(statCard("Handled bugs", handledBugsLabel, new Color(34, 197, 94)));
        cards.add(statCard("Avg. resolution time (h)", avgLabel, new Color(220, 38, 38)));
        middle.add(cards, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        tablesPanel.setBackground(new Color(243, 244, 246));

        String[] userColumns = {"User email", "Total bugs", "Handled bugs", "Avg. resolution time (h)"};
        perUserModel = new DefaultTableModel(userColumns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable userTable = new JTable(perUserModel);
        userTable.setRowHeight(28);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane userTableScroll = new JScrollPane(userTable);
        userTableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        userTableScroll.setPreferredSize(new Dimension(0, 160));

        JLabel userTableTitle = new JLabel("Statistics per user");
        userTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        String[] teamColumns = {"Team", "Total bugs", "Handled bugs", "Avg. resolution time (h)"};
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

        tablesPanel.add(userTableTitle);
        tablesPanel.add(Box.createVerticalStrut(8));
        tablesPanel.add(userTableScroll);
        tablesPanel.add(Box.createVerticalStrut(20));
        tablesPanel.add(teamTableTitle);
        tablesPanel.add(Box.createVerticalStrut(8));
        tablesPanel.add(teamTableScroll);

        middle.add(tablesPanel, BorderLayout.CENTER);

        panel.add(middle, BorderLayout.CENTER);
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

    private void generateReport() {
        ProjectDTO selectedProject = (ProjectDTO) projectCombo.getSelectedItem();
        if (selectedProject == null) {
            JOptionPane.showMessageDialog(this, "Select a project first.",
                    "Project required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int month = monthCombo.getSelectedIndex() + 1;
        int year = (Integer) yearSpinner.getValue();
        String projectName = selectedProject.getName();

        SwingWorker<ReportDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected ReportDTO doInBackground() {
                return ReportController.monthlyReport(year, month, projectName);
            }

            @Override
            protected void done() {
                try {
                    ReportDTO report = get();
                    totalBugsLabel.setText(String.valueOf(countOrZero(report.getTotalBugs())));
                    handledBugsLabel.setText(String.valueOf(countOrZero(report.getTotalHandledBugs())));
                    Double avg = report.getAverageGlobalResolutionTime();
                    avgLabel.setText(avg == null ? "N/A" : String.valueOf(new DecimalFormat("#.##").format(avg)));

                    perUserModel.setRowCount(0);
                    perTeamModel.setRowCount(0);

                    Set<UserDTO> users = new HashSet<>();
                    for (UserBugsDTO ub : report.getTotalBugsPerUser()) {
                        users.add(ub.getUser());
                    }

                    for (UserDTO user : users) {
                        Integer opened = null;
                        for (UserBugsDTO ub : report.getTotalBugsPerUser()) {
                            if (ub.getUser().equals(user)) {
                                opened = ub.getBugs();
                                break;
                            }
                        }
                        Integer handled = null;
                        for (UserBugsDTO ub : report.getTotalHandledBugsPerUser()) {
                            if (ub.getUser().equals(user)) {
                                handled = ub.getBugs();
                                break;
                            }
                        }

                        Double userAvg = null;
                        for (UserTimeDTO ut : report.getAverageResolutionTimePerUser()) {
                            if (ut.getUser().equals(user)) {
                                userAvg = ut.getTime();
                                break;
                            }
                        }

                        perUserModel.addRow(new Object[]{
                                user == null ? "Unknown user" : user.getEmail(),
                                countOrZero(opened),
                                countOrZero(handled),
                                userAvg == null ? "N/A" : userAvg
                        });
                    }

                    Set<TeamDTO> teams = new HashSet<>();
                    for (TeamBugsDTO ub : report.getTotalBugsPerTeam()) {
                        teams.add(ub.getTeam());
                    }

                    for (TeamDTO team : teams) {
                        Integer opened = null;
                        for (TeamBugsDTO ub : report.getTotalBugsPerTeam()) {
                            if (ub.getTeam().equals(team)) {
                                opened = ub.getBugs();
                                break;
                            }
                        }
                        Integer handled = null;
                        for (TeamBugsDTO ub : report.getTotalHandledBugsPerTeam()) {
                            if (ub.getTeam().equals(team)) {
                                handled = ub.getBugs();
                                break;
                            }
                        }

                        Double teamAvg = null;
                        for (TeamTimeDTO ut : report.getAverageResolutionTimePerTeam()) {
                            if (ut.getTeam().equals(team)) {
                                teamAvg = ut.getTime();
                                break;
                            }
                        }

                        perTeamModel.addRow(new Object[]{
                                team == null ? "Unknown team" : team.getName(),
                                countOrZero(opened),
                                countOrZero(handled),
                                teamAvg == null ? "N/A" : teamAvg
                        });
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ReportsScreen.this,
                            "Error generating the report: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private int countOrZero(Integer count) {
        return count == null ? 0 : count;
    }

    private void loadProjects() {
        SwingWorker<java.util.List<ProjectDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected java.util.List<ProjectDTO> doInBackground() {
                return ReportController.projectsForAdmin();
            }

            @Override
            protected void done() {
                try {
                    projectCombo.removeAllItems();
                    for (ProjectDTO project : get()) projectCombo.addItem(project);
                    if (projectCombo.getItemCount() == 0) {
                        JOptionPane.showMessageDialog(ReportsScreen.this,
                                "You are not assigned to any project.",
                                "No projects", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ReportsScreen.this,
                            "Error loading projects: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}





