package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import controller.ReportController;
import controller.Session;
import dto.ReportDTO;


public class ReportsScreen extends BaseFrame {

    private JComboBox<Integer> monthCombo;
    private JSpinner yearSpinner;
    private JLabel openedLabel, resolvedLabel, avgLabel;
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
            generateReport();
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
            if (item[1].equals("Reports")) menuItem.setBackground(new Color(254, 242, 242));
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
            case "Admin": next = new AdminDashboardScreen(); break;
            case "Reports": return; // gia' qui
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
        Integer[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedItem(now.get(Calendar.MONTH) + 1);

        yearSpinner = new JSpinner(new SpinnerNumberModel(now.get(Calendar.YEAR), 2020, 2100, 1));
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));

        JButton generateBtn = new JButton("Generate report");
        generateBtn.setBackground(new Color(220, 38, 38));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setFocusPainted(false);
        generateBtn.addActionListener(e -> generateReport());

        selector.add(new JLabel("Month:")); selector.add(monthCombo);
        selector.add(new JLabel("Year:")); selector.add(yearSpinner);
        selector.add(generateBtn);

        panel.add(selector, BorderLayout.NORTH);

        JPanel middle = new JPanel(new BorderLayout(0, 20));
        middle.setBackground(new Color(243, 244, 246));

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 0));
        cards.setBackground(new Color(243, 244, 246));
        openedLabel = valueLabel("0");
        resolvedLabel = valueLabel("0");
        avgLabel = valueLabel("N/A");
        cards.add(statCard("Issues opened this month", openedLabel, new Color(59, 130, 246)));
        cards.add(statCard("Issues resolved this month", resolvedLabel, new Color(34, 197, 94)));
        cards.add(statCard("Avg. resolution time (h)", avgLabel, new Color(220, 38, 38)));
        middle.add(cards, BorderLayout.NORTH);

        JPanel tablesPanel = new JPanel();
        tablesPanel.setLayout(new BoxLayout(tablesPanel, BoxLayout.Y_AXIS));
        tablesPanel.setBackground(new Color(243, 244, 246));

        String[] userColumns = {"User email", "Opened (by them)", "Resolved (assigned to them)", "Avg. resolution time (h)"};
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

        String[] teamColumns = {"Team", "Opened", "Resolved", "Avg. resolution time (h)"};
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
        int month = (Integer) monthCombo.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();

        SwingWorker<ReportDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected ReportDTO doInBackground() {
                return ReportController.monthlyReport(year, month);
            }

            @Override
            protected void done() {
                try {
                    ReportDTO report = get();
                    openedLabel.setText(String.valueOf(report.getOpened()));
                    resolvedLabel.setText(String.valueOf(report.getResolved()));
                    Double avg = report.getAvgResolutionHours();
                    avgLabel.setText(avg == null ? "N/A" : String.valueOf(avg));

                    perUserModel.setRowCount(0);
                    for (ReportDTO.UserReportStat row : report.getPerUser()) {
                        Double userAvg = row.getAvgResolutionHours();
                        perUserModel.addRow(new Object[]{
                                row.getEmail(), row.getOpened(), row.getResolved(),
                                userAvg == null ? "N/A" : userAvg
                        });
                    }

                    perTeamModel.setRowCount(0);
                    for (ReportDTO.TeamReportStat row : report.getPerTeam()) {
                        Double teamAvg = row.getAvgResolutionHours();
                        perTeamModel.addRow(new Object[]{
                                row.getTeam(), row.getOpened(), row.getResolved(),
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
}
