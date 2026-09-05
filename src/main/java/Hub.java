import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class Hub extends BaseFrame {

    private Map<String, Project> projects;
    private Map<String, Team> teams;
    private Map<String, Issue> issues;

    public Hub() {
        super("BugBoard26 - Dashboard");

        setFullscreen(isFullscreen);
        setLocationRelativeTo(null);

        initializeData();

        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createDashboardContent(), BorderLayout.CENTER);
    }

    private void initializeData() {
        // Team
        teams = new HashMap<>();
        Team frontendTeam = new Team("Frontend Team", Color.BLUE);
        teams.put("frontend", frontendTeam);
        Team backendTeam = new Team("Backend Team", new Color(147, 51, 234));
        teams.put("backend", backendTeam);
        Team mobileTeam = new Team("Mobile Team", new Color(249, 115, 22));
        teams.put("mobile", mobileTeam);
        Team qaTeam = new Team("QA Team", new Color(6, 182, 212));
        teams.put("qa", qaTeam);

        // Projects
        projects = new HashMap<>();
        Project project1 = new Project("E-Commerce Platform");
        project1.addTeam(teams.get("frontend"));
        project1.addTeam(teams.get("backend"));
        projects.put("ecom", project1);

        Project project2 = new Project("Mobile Banking App");
        project2.addTeam(teams.get("mobile"));
        project2.addTeam(teams.get("backend"));
        projects.put("bank", project2);

        Project project3 = new Project("CRM System");
        project3.addTeam(teams.get("backend"));
        project3.addTeam(teams.get("qa"));
        projects.put("crm", project3);

        Project project4 = new Project("Company Website");
        project4.addTeam(teams.get("frontend"));
        project4.addTeam(teams.get("qa"));
        projects.put("web", project4);

        // Issues
        issues = new LinkedHashMap<>();

        issues.put("BUG001", new Issue("BUG001", "Bug login", "La pagina di login non risponde dopo il submit",
                projects.get("ecom"), teams.get("frontend"), "Mario Rossi", Priority.HIGH, new Date(), IssueType.BUG));

        issues.put("BUG002", new Issue("BUG002", "Database connection timeout", "Le connessioni al database vanno in timeout dopo 30 secondi",
                projects.get("crm"), teams.get("backend"), "Anna Verdi", Priority.CRITICAL, new Date(), IssueType.BUG));

        issues.put("FEAT001", new Issue("FEAT001", "Dark mode", "Implementare il supporto per tema scuro",
                projects.get("web"), teams.get("frontend"), "Luca Bianchi", Priority.MEDIUM, new Date(), IssueType.FEATURE));

        issues.put("BUG003", new Issue("BUG003", "API update", "Aggiornare le API per il nuovo standard",
                projects.get("bank"), teams.get("backend"), "Giovanni Neri", Priority.HIGH, new Date(), Status.ONGOING, IssueType.BUG));

        issues.put("BUG004", new Issue("BUG004", "Crash su Android 13", "L'app si chiude all'avvio su Android 13",
                projects.get("bank"), teams.get("mobile"), "Sofia Galli", Priority.CRITICAL, new Date(), Status.ONGOING, IssueType.BUG));

        issues.put("FEAT002", new Issue("FEAT002", "Export PDF", "Implementare esportazione report in PDF",
                projects.get("crm"), teams.get("qa"), "Chiara Russo", Priority.MEDIUM, new Date(), Status.ONGOING, IssueType.FEATURE));

        issues.put("BUG005", new Issue("BUG005", "Errore pagamento", "Il gateway di pagamento restituisce errore 500",
                projects.get("ecom"), teams.get("backend"), "Anna Verdi", Priority.HIGH, new Date(), Status.RESOLVED, IssueType.BUG));

        issues.put("BUG006", new Issue("BUG006", "Test falliti", "Test di integrazione falliscono su CI/CD",
                projects.get("crm"), teams.get("qa"), "Paolo Ferrara", Priority.MEDIUM, new Date(), Status.RESOLVED, IssueType.BUG));
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Color.WHITE);
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, new Color(209, 213, 219)
        ));
        JLabel logo = new JLabel("BugBoard26");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logo.setForeground(new Color(220, 38, 38));
        logo.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);

        String[] menuItems = {
                "Dashboard",
                "Issues",
                "Nuova Issue",
                "Admin Dashboard",
                "Reports"
        };


        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItem = createMenuItem(menuItems[i], menuItems[i]);
            sidebar.add(menuItem);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());

        JPanel logoutItem = createMenuItem("🚪  Logout", "Logout");
        logoutItem.setBackground(new Color(254, 242, 242));
        sidebar.add(logoutItem);
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

                JFrame next = null;

                switch (page) {
                    case "Dashboard":
                        next = new Hub();
                        break;

                    case "Issues":
                        next = new IssuesListScreen();
                        break;

                    case "NewIssue":
                        JOptionPane.showMessageDialog(null, "Nuova Issue non ancora implementata");
                        return;

                    case "Admin":
                        JOptionPane.showMessageDialog(null, "Admin non ancora implementato");
                        return;

                    case "Reports":
                        JOptionPane.showMessageDialog(null, "Reports non ancora implementato");
                        return;
                }

                if (next != null) {
                    //next.setFullscreen(BaseFrame.isFullscreen);
                    next.setVisible(true);
                    SwingUtilities.getWindowAncestor(item).dispose();
                }
            }
        });

        return item;
    }
    private JPanel createDashboardContent() {
        JPanel content = new JPanel(new BorderLayout(20, 20));
        content.setBackground(new Color(243, 244, 246));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JLabel welcomeLabel = new JLabel("Hi, Username - Today Overview");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(17, 24, 39));
        header.add(welcomeLabel, BorderLayout.WEST);
        content.add(header, BorderLayout.NORTH);

        // Board panel: solo TODO
        JPanel boardPanel = new JPanel(new GridLayout(1, 1, 20, 0)); // una sola colonna
        boardPanel.setBackground(new Color(243, 244, 246));
        boardPanel.add(createColumn("Todo", new Color(156, 163, 175), Status.TODO));

        content.add(boardPanel, BorderLayout.CENTER);
        return content;
    }
    private JPanel createColumn(String title, Color color, Status status) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(Color.WHITE);
        column.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        column.setAlignmentX(Component.LEFT_ALIGNMENT); // Allineamento sinistro per la colonna

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);

        long count = issues.values().stream().filter(i -> i.getStatus() == status).count();
        JLabel countLabel = new JLabel(String.valueOf(count));
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

        // Aggiungi solo le TODO
        issues.values().stream()
                .filter(i -> i.getStatus() == status)
                .forEach(issue -> {
                    JPanel card = createIssueCard(issue);
                    card.setAlignmentX(Component.LEFT_ALIGNMENT); // Allinea al bordo sinistro della colonna
                    column.add(card);
                    column.add(Box.createVerticalStrut(8));
                });

        return column;
    }
    private JPanel createIssueCard(Issue issue) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT); // Allinea il card al bordo sinistro della colonna

        // Header: Priorità
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(249, 250, 251));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel priorityLabel = new JLabel(issue.getPriority().name());
        priorityLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        priorityLabel.setForeground(issue.getPriority().getColor());
        priorityLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(issue.getPriority().getColor()),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        headerPanel.add(priorityLabel, BorderLayout.EAST);

        // Titolo allineato a sinistra
        JLabel titleLabel = new JLabel(issue.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(new Color(17, 24, 39));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Team e tipo issue
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        infoPanel.setBackground(new Color(249, 250, 251));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teamInfo = new JLabel("Team: " + issue.getTeam().getName());
        teamInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        teamInfo.setForeground(new Color(75, 85, 99));

        JLabel typeLabel = new JLabel(issue.getType().name().toLowerCase());
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        typeLabel.setBackground(new Color(37, 99, 235));
        typeLabel.setForeground(Color.WHITE);
        typeLabel.setOpaque(true);
        typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));

        infoPanel.add(teamInfo);
        infoPanel.add(typeLabel);

        card.add(headerPanel);
        card.add(titleLabel);
        card.add(infoPanel);

        return card;
    }
    // Classi interne
    enum Priority {
        LOW("Bassa", new Color(156, 163, 175)),
        MEDIUM("Media", new Color(59, 130, 246)),
        HIGH("Alta", new Color(249, 115, 22)),
        CRITICAL("Critica", new Color(220, 38, 38));

        private String label;
        private Color color;
        Priority(String label, Color color) { this.label = label; this.color = color; }
        public Color getColor() { return color; }
    }

    enum Status { TODO, ONGOING, RESOLVED }
    enum IssueType { BUG, QUESTION, DOCUMENTATION, FEATURE }

    class Team {
        private String name;
        private Color color;
        private java.util.List<String> members;
        public Team(String name, Color color) { this.name = name; this.color = color; this.members = new ArrayList<>(); }
        public String getName() { return name; }
        public Color getColor() { return color; }
        public void addMember(String member) { members.add(member); }
    }

    class Project {
        private String name;
        private java.util.List<Team> teams;
        public Project(String name) { this.name = name; this.teams = new ArrayList<>(); }
        public void addTeam(Team team) { teams.add(team); }
    }

    class Issue {
        private String id, title, description, assignee;
        private Project project;
        private Team team;
        private Priority priority;
        private Date createdAt;
        private Status status;
        private IssueType type;

        public Issue(String id, String title, String description, Project project,
                     Team team, String assignee, Priority priority, Date createdAt, IssueType type) {
            this(id, title, description, project, team, assignee, priority, createdAt, Status.TODO, type);
        }

        public Issue(String id, String title, String description, Project project,
                     Team team, String assignee, Priority priority, Date createdAt, Status status, IssueType type) {
            this.id = id; this.title = title; this.description = description; this.project = project;
            this.team = team; this.assignee = assignee; this.priority = priority; this.createdAt = createdAt;
            this.status = status; this.type = type;
        }

        public String getTitle() { return title; }
        public Team getTeam() { return team; }
        public Priority getPriority() { return priority; }
        public Status getStatus() { return status; }
        public IssueType getType() { return type; }
    }
}