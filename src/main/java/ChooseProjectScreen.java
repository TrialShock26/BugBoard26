import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sezione riservata agli utenti DEV: scelta del progetto (menu a tendina) e,
 * a cascata, del team a cui partecipare all'interno di quel progetto (secondo
 * menu a tendina, popolato con i team indicati dall'amministratore per il
 * progetto selezionato).
 */
public class ChooseProjectScreen extends BaseFrame {

    private JComboBox<ProjectItem> projectCombo;
    private JComboBox<String> teamCombo;
    private DefaultTableModel myProjectsModel;
    private JTable myProjectsTable;
    private final List<String> myProjectIds = new ArrayList<>();
    private List<Map<String, Object>> projects = new ArrayList<>();

    public ChooseProjectScreen() {
        super("BugBoard26 - Choose Project");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);

        if (Session.isAdmin()) {
            add(adminNotApplicablePanel(), BorderLayout.CENTER);
        } else {
            add(createContent(), BorderLayout.CENTER);
            loadProjects();
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

    private JPanel adminNotApplicablePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 244, 246));
        JLabel label = new JLabel("This section is for DEV users. Admins manage projects from \"Create Project\".");
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
            if (item[1].equals("ChooseProject")) menuItem.setBackground(new Color(229, 231, 235));
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
            public void mouseExited(MouseEvent evt) {
                item.setBackground(page.equals("ChooseProject") ? new Color(229, 231, 235) : Color.WHITE);
            }
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
            case "Reports": next = new ReportsScreen(); break;
            case "CreateProject": next = new CreateProjectScreen(); break;
            case "ChooseProject": return; // already here
            case "Logout":
                Session.clear();
                next = new LoginScreen();
                break;
        }
        if (next != null) {
            navigateTo(next);
        }
    }

    // ================= CONTENT =================
    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("Choose your project");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(formTitle);
        form.add(Box.createVerticalStrut(6));

        JLabel hint = new JLabel("Pick a project, then pick the team you take part in. You can join more than one project.");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(107, 114, 128));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(hint);
        form.add(Box.createVerticalStrut(18));

        JLabel projectLabel = new JLabel("Project *");
        projectLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        projectLabel.setForeground(new Color(75, 85, 85));
        projectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(projectLabel);
        form.add(Box.createVerticalStrut(4));

        projectCombo = new JComboBox<>();
        projectCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        projectCombo.setMaximumSize(new Dimension(320, 34));
        projectCombo.setPreferredSize(new Dimension(320, 34));
        projectCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Alla scelta del progetto, il secondo menu si ripopola con i team di quel progetto
        projectCombo.addActionListener(e -> refreshTeamCombo());
        form.add(projectCombo);
        form.add(Box.createVerticalStrut(16));

        JLabel teamLabel = new JLabel("Team *");
        teamLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        teamLabel.setForeground(new Color(75, 85, 85));
        teamLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(teamLabel);
        form.add(Box.createVerticalStrut(4));

        teamCombo = new JComboBox<>();
        teamCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        teamCombo.setMaximumSize(new Dimension(320, 34));
        teamCombo.setPreferredSize(new Dimension(320, 34));
        teamCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(teamCombo);
        form.add(Box.createVerticalStrut(20));

        JButton confirmBtn = new JButton("Join project");
        confirmBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmBtn.setBackground(new Color(220, 38, 38));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        confirmBtn.addActionListener(e -> confirmChoice());
        form.add(confirmBtn);

        panel.add(form, BorderLayout.NORTH);

        // Elenco dei progetti a cui l'utente partecipa (puo' essere piu' di uno)
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(new Color(243, 244, 246));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setBackground(new Color(243, 244, 246));
        JLabel tableTitle = new JLabel("My projects");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.add(tableTitle, BorderLayout.WEST);

        JButton leaveBtn = new JButton("Leave selected");
        leaveBtn.setFocusPainted(false);
        leaveBtn.addActionListener(e -> leaveSelected());
        tableHeader.add(leaveBtn, BorderLayout.EAST);
        tablePanel.add(tableHeader, BorderLayout.NORTH);

        String[] columns = {"Project", "Team"};
        myProjectsModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        myProjectsTable = new JTable(myProjectsModel);
        myProjectsTable.setRowHeight(28);
        myProjectsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        myProjectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(myProjectsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    /** Ripopola il menu dei team in base al progetto attualmente selezionato. */
    @SuppressWarnings("unchecked")
    private void refreshTeamCombo() {
        teamCombo.removeAllItems();
        ProjectItem selected = (ProjectItem) projectCombo.getSelectedItem();
        if (selected == null) return;
        List<String> teams = (List<String>) selected.data.get("teams");
        if (teams != null) {
            for (String t : teams) teamCombo.addItem(t);
        }
    }

    private void loadProjects() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return ApiClient.listProjects();
            }

            @Override
            protected void done() {
                try {
                    projects = get();
                    projectCombo.removeAllItems();
                    for (Map<String, Object> p : projects) {
                        projectCombo.addItem(new ProjectItem(p));
                    }
                    refreshTeamCombo();
                    loadMyProjects();
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ChooseProjectScreen.this,
                            "Error loading projects: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadMyProjects() {
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() {
                return ApiClient.myProjects();
            }

            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> mine = get();
                    myProjectsModel.setRowCount(0);
                    for (Map<String, Object> row : mine) {
                        Object projectName = row.get("projectName");
                        myProjectsModel.addRow(new Object[]{
                                projectName == null ? row.get("projectId") : projectName,
                                row.get("team")
                        });
                    }
                    myProjectIds.clear();
                    for (Map<String, Object> row : mine) {
                        myProjectIds.add(String.valueOf(row.get("projectId")));
                    }
                } catch (Exception e) {
                    // informazione accessoria: se non disponibile non blocca la schermata
                }
            }
        };
        worker.execute();
    }

    private void confirmChoice() {
        ProjectItem project = (ProjectItem) projectCombo.getSelectedItem();
        String team = (String) teamCombo.getSelectedItem();

        if (project == null) {
            JOptionPane.showMessageDialog(this, "No project available to choose.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (team == null || team.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select the team you take part in.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ApiClient.chooseProject(project.getId(), team);
            loadMyProjects();
            JOptionPane.showMessageDialog(this,
                    "You joined \"" + project.getName() + "\" as part of the " + team + " team.",
                    "Choice saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (ApiClient.ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void leaveSelected() {
        int row = myProjectsTable.getSelectedRow();
        if (row < 0 || row >= myProjectIds.size()) {
            JOptionPane.showMessageDialog(this, "Select a project from the table first.",
                    "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ApiClient.leaveProject(myProjectIds.get(row));
            loadMyProjects();
        } catch (ApiClient.ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Voce del menu progetti: mostra il nome, ma conserva l'intera mappa (id, teams, ...). */
    private static class ProjectItem {
        final Map<String, Object> data;
        ProjectItem(Map<String, Object> data) { this.data = data; }
        String getId() { return String.valueOf(data.get("id")); }
        String getName() { return String.valueOf(data.get("name")); }
        @Override public String toString() { return getName(); }
    }
}
