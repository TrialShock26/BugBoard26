package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import controller.Session;
import controller.TeamController;
import dto.TeamDTO;
import exception.ApiException;

public class CreateProjectScreen extends BaseFrame {

    private JTextField nameField;
    private final List<JTextField> teamFields = new ArrayList<>();
    private JPanel teamsContainer;
    private DefaultTableModel projectsModel;

    /* Numero massimo di campi "team" che l'admin puo' aggiungere per un progetto. */
    private static final int MAX_TEAMS = 6;

    public CreateProjectScreen() {
        super("BugBoard26 - Create Project");

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

    //  TOP BAR
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
            if (item[1].equals("CreateProject")) menuItem.setBackground(new Color(254, 242, 242));
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
            case "Reports": next = new ReportsScreen(); break;
            case "CreateProject": return; // already here
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

    //  CONTENT
    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Project creation form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formTitle = new JLabel("New project");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(formTitle);
        form.add(Box.createVerticalStrut(15));

        JLabel nameLabel = new JLabel("Project name *");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameLabel.setForeground(new Color(75, 85, 85));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(nameLabel);

        nameField = new JTextField();
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameField.setMaximumSize(new Dimension(400, 36));
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(nameField);
        form.add(Box.createVerticalStrut(15));

        JLabel teamsLabel = new JLabel("Participating teams *");
        teamsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        teamsLabel.setForeground(new Color(75, 85, 85));
        teamsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(teamsLabel);
        form.add(Box.createVerticalStrut(5));

        JLabel teamsHint = new JLabel("Type the team name; use + to add another one (up to " + MAX_TEAMS + ").");
        teamsHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        teamsHint.setForeground(new Color(107, 114, 128));
        teamsHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(teamsHint);
        form.add(Box.createVerticalStrut(6));

        teamsContainer = new JPanel();
        teamsContainer.setLayout(new BoxLayout(teamsContainer, BoxLayout.Y_AXIS));
        teamsContainer.setBackground(Color.WHITE);
        teamsContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(teamsContainer);

        addTeamRow(); // si parte con un solo campo di inserimento

        form.add(Box.createVerticalStrut(20));

        JButton createBtn = new JButton("Create Project");
        createBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        createBtn.setBackground(new Color(220, 38, 38));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFocusPainted(false);
        createBtn.addActionListener(e -> createProject());
        form.add(createBtn);

        panel.add(form, BorderLayout.NORTH);

        // Existing projects table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 8));
        tablePanel.setBackground(new Color(243, 244, 246));

        JLabel tableTitle = new JLabel("Existing projects");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Project", "Teams"};
        projectsModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(projectsModel);
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235)));
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        panel.add(tablePanel, BorderLayout.CENTER);
        return panel;
    }

    /** Aggiunge un nuovo campo di testo per un ulteriore team, fino al limite massimo. */
    private void addTeamRow() {
        if (teamFields.size() >= MAX_TEAMS) return;
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teamFields.add(field);
        rebuildTeamRows();
    }

    private void removeTeamRow(JTextField field) {
        teamFields.remove(field);
        if (teamFields.isEmpty()) {
            // C'e' sempre almeno un campo disponibile
            JTextField fresh = new JTextField();
            fresh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            teamFields.add(fresh);
        }
        rebuildTeamRows();
    }

    /** Ridisegna le righe dei campi team: il pulsante "+" compare solo sull'ultima riga
     * (finche' non si raggiunge MAX_TEAMS), il pulsante "-" su ogni riga oltre la prima. */
    private void rebuildTeamRows() {
        teamsContainer.removeAll();
        for (int i = 0; i < teamFields.size(); i++) {
            JTextField field = teamFields.get(i);

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            row.setBackground(Color.WHITE);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            field.setPreferredSize(new Dimension(220, 32));
            row.add(field);

            boolean isLast = (i == teamFields.size() - 1);
            if (isLast && teamFields.size() < MAX_TEAMS) {
                JButton addBtn = new JButton("+");
                addBtn.setMargin(new Insets(2, 10, 2, 10));
                addBtn.setFocusPainted(false);
                addBtn.setToolTipText("Add another team");
                addBtn.addActionListener(e -> addTeamRow());
                row.add(addBtn);
            }
            if (teamFields.size() > 1) {
                JButton removeBtn = new JButton("\u2212");
                removeBtn.setMargin(new Insets(2, 10, 2, 10));
                removeBtn.setFocusPainted(false);
                removeBtn.setToolTipText("Remove this team");
                removeBtn.addActionListener(e -> removeTeamRow(field));
                row.add(removeBtn);
            }

            teamsContainer.add(row);
        }
        teamsContainer.revalidate();
        teamsContainer.repaint();
    }

    private void createProject() {
        String name = nameField.getText().trim();
        List<String> enteredTeams = new ArrayList<>();
        for (JTextField field : teamFields) {
            String value = field.getText().trim();
            if (!value.isEmpty()) enteredTeams.add(value);
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Project name is required.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (enteredTeams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter at least one participating team.",
                    "Missing field", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            TeamController.createTeams(name, enteredTeams);
            nameField.setText("");
            teamFields.clear();
            addTeamRow();
            loadProjects();
            JOptionPane.showMessageDialog(this, "Project created successfully.",
                    "Project created", JOptionPane.INFORMATION_MESSAGE);
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProjects() {
        SwingWorker<List<TeamDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TeamDTO> doInBackground() {
                return TeamController.listTeams();
            }

            @Override
            protected void done() {
                try {
                    List<TeamDTO> teams = get();
                    // Non esiste una tabella "Project": raggruppiamo i team per il valore
                    // condiviso del loro campo "project" (cfr. TeamDTO / procedura add_new_teams).
                    Map<String, List<String>> teamNamesByProject = new LinkedHashMap<>();
                    for (TeamDTO t : teams) {
                        teamNamesByProject.computeIfAbsent(t.getProject(), k -> new ArrayList<>()).add(t.getName());
                    }
                    projectsModel.setRowCount(0);
                    for (Map.Entry<String, List<String>> entry : teamNamesByProject.entrySet()) {
                        projectsModel.addRow(new Object[]{entry.getKey(), String.join(", ", entry.getValue())});
                    }
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(CreateProjectScreen.this,
                            "Error loading projects: " + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
