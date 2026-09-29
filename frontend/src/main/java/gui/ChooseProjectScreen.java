package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import controller.ProjectController;
import controller.Session;
import dto.ProjectDTO;
import dto.TeamDTO;
import exception.ApiException;


public class ChooseProjectScreen extends BaseFrame {

    private JComboBox<ProjectItem> projectCombo;
    private JComboBox<TeamItem> teamCombo;
    private DefaultTableModel myProjectsModel;
    private boolean updatingProjectCombo;

    public ChooseProjectScreen() {
        super("BugBoard26 - Choose Project");

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createTopBar(), BorderLayout.NORTH);

        add(createContent(), BorderLayout.CENTER);
        loadProjects();
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

    private JPanel adminNotApplicablePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(243, 244, 246));
        JLabel label = new JLabel("This section is for DEV users. Admins manage projects from \"Create Project\".");
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(new Color(107, 114, 128));
        panel.add(label);
        return panel;
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
            if (item[1].equals("ChooseProject")) markCurrentPage(menuItem);
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
        boolean current = page.equals("ChooseProject");
        if (current) markCurrentPage(item);

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        item.add(label);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { if (!current) item.setBackground(new Color(229, 231, 235)); }
            public void mouseExited(MouseEvent evt) {
                if (!current) item.setBackground(Color.WHITE);
            }
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
            case "Reports": next = new ReportsScreen(); break;
            case "CreateProject": next = new CreateProjectScreen(); break;
            case "CreateUser": next = new CreateUserScreen(); break;
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
        projectCombo.addActionListener(e -> {
            if (!updatingProjectCombo) refreshTeamCombo();
        });
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
        JLabel tableTitle = new JLabel("My projects and their teams");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.add(tableTitle, BorderLayout.WEST);
        JButton refreshProjects = new JButton("↻");
        refreshProjects.setToolTipText("Aggiorna");
        refreshProjects.addActionListener(e -> loadProjects());
        tableHeader.add(refreshProjects, BorderLayout.EAST);
        tablePanel.add(tableHeader, BorderLayout.NORTH);

        String[] columns = {"Project", "Teams in this project"};
        myProjectsModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable myProjectsTable = new JTable(myProjectsModel);
        myProjectsTable.setRowHeight(28);
        myProjectsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        myProjectsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScroll = new JScrollPane(myProjectsTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private void refreshTeamCombo() {
        ProjectItem selected = (ProjectItem) projectCombo.getSelectedItem();
        teamCombo.removeAllItems();
        if (selected == null) {
            return;
        }
        String projectId = selected.getId();
        SwingWorker<List<TeamDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TeamDTO> doInBackground() {
                return ProjectController.listTeams(projectId);
            }

            @Override
            protected void done() {
                try {
                    List<TeamDTO> teams = get();
                    ProjectItem currentProject = (ProjectItem) projectCombo.getSelectedItem();
                    if (currentProject == null || !currentProject.getId().equals(projectId)) return;
                    teamCombo.removeAllItems();
                    for (TeamDTO t : teams) teamCombo.addItem(new TeamItem(t));
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ChooseProjectScreen.this,
                            "Error loading teams: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void loadProjects() {
        SwingWorker<List<ProjectDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProjectDTO> doInBackground() {
                return ProjectController.listProjects();
            }

            @Override
            protected void done() {
                try {
                    List<ProjectDTO> projects = get();
                    updatingProjectCombo = true;
                    try {
                        projectCombo.removeAllItems();
                        for (ProjectDTO p : projects) projectCombo.addItem(new ProjectItem(p));
                    } finally {
                        updatingProjectCombo = false;
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
        SwingWorker<List<ProjectMembershipRow>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProjectMembershipRow> doInBackground() {
                List<ProjectDTO> projects = ProjectController.listMyProjects();
                List<ProjectMembershipRow> rows = new ArrayList<>();
                for (ProjectDTO project : projects) {
                    List<TeamDTO> teams = ProjectController.listTeams(project.getId());
                    List<String> teamNames = new ArrayList<>();
                    for (TeamDTO team : teams) teamNames.add(team.getName());
                    rows.add(new ProjectMembershipRow(project.getName(), String.join(", ", teamNames)));
                }
                return rows;
            }

            @Override
            protected void done() {
                try {
                    List<ProjectMembershipRow> mine = get();
                    myProjectsModel.setRowCount(0);
                    for (ProjectMembershipRow row : mine) {
                        myProjectsModel.addRow(new Object[]{row.projectName, row.teamNames});
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ChooseProjectScreen.this,
                            "Could not load your projects and their teams. Details: " + cause.getMessage(),
                            "Error loading memberships", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void confirmChoice() {
        ProjectItem project = (ProjectItem) projectCombo.getSelectedItem();
        TeamItem team = (TeamItem) teamCombo.getSelectedItem();

        if (project == null) {
            JOptionPane.showMessageDialog(this, "No project available to choose.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (team == null) {
            JOptionPane.showMessageDialog(this, "Select the team you take part in.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // PUT /projects/{id}/teams/{id}
            ProjectController.joinTeam(project.getId(), team.getId());
            loadMyProjects();
            JOptionPane.showMessageDialog(this,
                    "You joined \"" + project.getName() + "\" as part of the " + team.getName() + " team.",
                    "Choice saved", JOptionPane.INFORMATION_MESSAGE);
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class TeamItem {
        final TeamDTO data;
        TeamItem(TeamDTO data) { this.data = data; }
        String getId() { return data.getId(); }
        String getName() { return data.getName(); }
        @Override public String toString() { return getName(); }
    }

    private static class ProjectMembershipRow {
        final String projectName;
        final String teamNames;

        ProjectMembershipRow(String projectName, String teamNames) {
            this.projectName = projectName;
            this.teamNames = teamNames;
        }
    }

    private static class ProjectItem {
        final ProjectDTO data;
        ProjectItem(ProjectDTO data) { this.data = data; }
        String getId() { return data.getId(); }
        String getName() { return data.getName(); }
        @Override public String toString() { return getName(); }
    }
}








