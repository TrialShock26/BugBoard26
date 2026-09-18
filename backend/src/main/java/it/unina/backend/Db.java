package it.unina.backend;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class Db {

    private final Map<String, User> usersById = new LinkedHashMap<>();
    private final Map<String, User> usersByEmail = new LinkedHashMap<>();
    private final Map<String, Issue> issues = new LinkedHashMap<>();
    private final Map<String, String> sessions = new ConcurrentHashMap<>(); // token -> email
    private final Map<String, Project> projects = new LinkedHashMap<>();

    private int userSeq = 1;
    private int issueSeq = 1;
    private int projectSeq = 1;

    public Db() {
        seed();
    }


    private void seed() {
        // Punto 1: "Il sistema viene fornito con un account da amministratore gia' attivo,
        // con credenziali di default." I ruoli disponibili sono ADMIN e DEV.
        createUserInternal("admin", "ciao", "Amministratore", User.Role.ADMIN, null);
        createUserInternal("mario.rossi", "ciao", "Mario Rossi", User.Role.DEV, User.Team.FRONTEND);
        createUserInternal("anna.verdi", "ciao", "Anna Verdi", User.Role.DEV, User.Team.BACKEND);
        createUserInternal("luca.bianchi", "ciao", "Luca Bianchi", User.Role.DEV, User.Team.FRONTEND);
        createUserInternal("sofia.galli", "ciao", "Sofia Galli", User.Role.DEV, User.Team.MOBILE);

        long now = System.currentTimeMillis();
        long day = 24L * 3600 * 1000;

        Issue i1 = createIssueInternal("Bug login", "La pagina di login non risponde dopo il submit",
                Issue.Type.BUG, Issue.Priority.HIGH, "mario.rossi", now - 2 * day);
        i1.addLabel("frontend"); i1.addLabel("urgente");
        i1.setAssigneeEmail("mario.rossi");
        i1.setStatus(Issue.Status.ONGOING);

        Issue i2 = createIssueInternal("Database connection timeout", "Le connessioni al database vanno in timeout dopo 30 secondi",
                Issue.Type.BUG, Issue.Priority.CRITICAL, "anna.verdi", now - 5 * day);
        i2.addLabel("backend");
        i2.setAssigneeEmail("anna.verdi");
        i2.setStatus(Issue.Status.ONGOING);

        Issue i3 = createIssueInternal("Dark mode", "Implementare il supporto per tema scuro",
                Issue.Type.FEATURE, Issue.Priority.MEDIUM, "luca.bianchi", now - day);
        i3.addLabel("frontend"); i3.addLabel("ui");

        Issue i4 = createIssueInternal("Crash su Android 13", "L'app si chiude all'avvio su Android 13",
                Issue.Type.BUG, Issue.Priority.CRITICAL, "sofia.galli", now - 3 * day);
        i4.addLabel("mobile");
        i4.setAssigneeEmail("sofia.galli");
        i4.setStatus(Issue.Status.ONGOING);

        Issue i5 = createIssueInternal("Errore pagamento", "Il gateway di pagamento restituisce errore 500",
                Issue.Type.BUG, Issue.Priority.HIGH, "mario.rossi", now - 10 * day);
        i5.addLabel("backend"); i5.addLabel("sicurezza");
        i5.setAssigneeEmail("anna.verdi");
        i5.setStatus(Issue.Status.RESOLVED);
        i5.forceResolvedAt(now - 8 * day); // risolta in ~2 giorni, per dati di report piu' realistici

        Issue i6 = createIssueInternal("Test falliti su CI/CD", "Test di integrazione falliscono su CI/CD",
                Issue.Type.BUG, Issue.Priority.MEDIUM, "luca.bianchi", now - 12 * day);
        i6.addLabel("qa");
        i6.setAssigneeEmail("sofia.galli");
        i6.setStatus(Issue.Status.RESOLVED);
        i6.forceResolvedAt(now - 9 * day);

        Issue i7 = createIssueInternal("Export PDF", "Implementare esportazione dell'elenco issue in PDF",
                Issue.Type.FEATURE, Issue.Priority.NONE, "anna.verdi", now - 6 * day);
        i7.addLabel("reportistica");

        // Bug non ancora assegnato: candidato per l'auto-assegnazione (punto 14).
        Issue i8 = createIssueInternal("Notifiche push duplicate", "Alcuni utenti ricevono la stessa notifica push piu' volte",
                Issue.Type.BUG, Issue.Priority.MEDIUM, "mario.rossi", now - day / 2);
        i8.addLabel("mobile"); i8.addLabel("notifiche");

        Issue i9 = createIssueInternal("Filtro priorita' non funziona", "Selezionando 'Critica' il filtro non restituisce risultati",
                Issue.Type.BUG, Issue.Priority.HIGH, "anna.verdi", now - day / 4);
        i9.addLabel("frontend");

        // Progetti demo (sezione admin "Create Project")
        createProjectInternal("Company Website", List.of("FRONTEND", "QA"), now - 20 * day);
        createProjectInternal("Mobile Banking App", List.of("MOBILE", "BACKEND"), now - 15 * day);
    }


    private synchronized Project createProjectInternal(String name, List<String> teams, long createdAt) {
        String id = "P" + (projectSeq++);
        Project p = new Project(id, name, teams, createdAt);
        projects.put(id, p);
        return p;
    }

    public synchronized Project createProject(String name, List<String> teams) {
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Project name is required");
        List<String> cleanTeams = new ArrayList<>();
        if (teams != null) {
            for (String t : teams) {
                if (t != null && !t.trim().isEmpty()) cleanTeams.add(t.trim());
            }
        }
        if (cleanTeams.isEmpty()) throw new IllegalArgumentException("At least one team is required");
        return createProjectInternal(name.trim(), cleanTeams, System.currentTimeMillis());
    }

    public List<Project> listProjects() {
        return new ArrayList<>(projects.values());
    }

    public Project findProject(String id) {
        return id == null ? null : projects.get(id);
    }


    public synchronized User chooseProject(String userEmail, String projectId, String projectTeam) {
        User user = findUserByEmail(userEmail);
        if (user == null) throw new IllegalArgumentException("User not found");
        Project project = findProject(projectId);
        if (project == null) throw new IllegalArgumentException("Project not found");
        if (projectTeam == null || projectTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Team is required");
        }
        String clean = projectTeam.trim();
        boolean valid = project.getTeams().stream().anyMatch(t -> t.equalsIgnoreCase(clean));
        if (!valid) throw new IllegalArgumentException("The selected team does not belong to this project");

        user.chooseProject(project.getId(), clean);
        return user;
    }

    /** Rimuove la partecipazione dell'utente a un progetto. */
    public synchronized User leaveProject(String userEmail, String projectId) {
        User user = findUserByEmail(userEmail);
        if (user == null) throw new IllegalArgumentException("User not found");
        if (!user.leaveProject(projectId)) {
            throw new IllegalArgumentException("You are not taking part in this project");
        }
        return user;
    }

    private synchronized User createUserInternal(String email, String password, String name, User.Role role, User.Team team) {
        String id = "U" + (userSeq++);
        User u = new User(id, email, password, name, role, team);
        usersById.put(id, u);
        usersByEmail.put(email.toLowerCase(), u);
        return u;
    }

    public synchronized User createUser(String email, String password, String name, User.Role role, User.Team team) {
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("Email is required");
        if (password == null || password.length() < 4) throw new IllegalArgumentException("Password must be at least 4 characters long");
        if (usersByEmail.containsKey(email.toLowerCase())) throw new IllegalArgumentException("Email already registered");
        String safeName = (name == null || name.trim().isEmpty()) ? email : name.trim();
        User.Team safeTeam = role == User.Role.ADMIN ? null : team;
        return createUserInternal(email.trim(), password, safeName, role, safeTeam);
    }

    public User findUserByEmail(String email) {
        return email == null ? null : usersByEmail.get(email.toLowerCase());
    }

    public Collection<User> allUsers() { return usersById.values(); }

    public List<User> assignableUsers() {
        return usersById.values().stream()
                .filter(u -> u.getRole() == User.Role.DEV)
                .collect(Collectors.toList());
    }


    private synchronized Issue createIssueInternal(String title, String description, Issue.Type type,
                                                     Issue.Priority priority, String reporterEmail, long createdAt) {
        String id = "ISS" + String.format("%03d", issueSeq++);
        Issue issue = new Issue(id, title, description, type, priority, reporterEmail, createdAt);
        issues.put(id, issue);
        return issue;
    }

    public synchronized Issue createIssue(String title, String description, Issue.Type type,
                                            Issue.Priority priority, String reporterEmail) {
        // Punto 2: "Tutti gli utenti autenticati possono segnalare una issue indicando
        // almeno un titolo e una descrizione."
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title is required");
        if (description == null || description.trim().isEmpty()) throw new IllegalArgumentException("Description is required");
        if (type == null) throw new IllegalArgumentException("Issue type is required");
        return createIssueInternal(title.trim(), description.trim(), type, priority, reporterEmail,
                System.currentTimeMillis());
    }

    public Issue findIssue(String id) { return issues.get(id); }

    public List<Issue> queryIssues(Issue.Type type, Issue.Status status, Issue.Priority priority,
                                     String createdAtDate, String sort) {
        Long dayStart = null;
        Long dayEnd = null;
        if (createdAtDate != null && !createdAtDate.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.clear();
            String[] parts = createdAtDate.split("-");
            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]), 0, 0, 0);
            dayStart = cal.getTimeInMillis();
            Calendar end = (Calendar) cal.clone();
            end.add(Calendar.DAY_OF_MONTH, 1);
            dayEnd = end.getTimeInMillis();
        }
        final Long from = dayStart;
        final Long to = dayEnd;

        List<Issue> result = issues.values().stream()
                .filter(i -> type == null || i.getType() == type)
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> priority == null || i.getPriority() == priority)
                .filter(i -> from == null || (i.getCreatedAt() >= from && i.getCreatedAt() < to))
                .collect(Collectors.toList());

        Comparator<Issue> cmp;
        if ("priority".equalsIgnoreCase(sort)) {
            cmp = Comparator.comparingInt((Issue i) -> i.getPriority().ordinal()).reversed();
        } else if ("title".equalsIgnoreCase(sort)) {
            cmp = Comparator.comparing(Issue::getTitle, String.CASE_INSENSITIVE_ORDER);
        } else if ("status".equalsIgnoreCase(sort)) {
            cmp = Comparator.comparingInt(i -> i.getStatus().ordinal());
        } else {
            cmp = Comparator.comparingLong(Issue::getCreatedAt).reversed(); // default: le piu' recenti prima
        }
        result.sort(cmp);
        return result;
    }

    public long currentWorkload(String email) {
        return issues.values().stream()
                .filter(i -> email.equalsIgnoreCase(i.getAssigneeEmail()))
                .filter(i -> i.getStatus() != Issue.Status.RESOLVED)
                .count();
    }


    public boolean isEligibleForSuggestion(String email) {
        User caller = findUserByEmail(email);
        if (caller == null || caller.getRole() != User.Role.DEV) return false;

        long myLoad = currentWorkload(email);
        for (User other : assignableUsers()) {
            if (other.getEmail().equalsIgnoreCase(email)) continue;
            if (currentWorkload(other.getEmail()) < myLoad) {
                return false; // esiste qualcuno con un carico piu' basso: non sei il minimo
            }
        }
        return true;
    }

    public List<Issue> availableBugs() {
        return issues.values().stream()
                .filter(i -> i.getType() == Issue.Type.BUG)
                .filter(i -> i.getAssigneeEmail() == null)
                .filter(i -> i.getStatus() != Issue.Status.RESOLVED)
                .sorted(Comparator.comparingLong(Issue::getCreatedAt))
                .collect(Collectors.toList());
    }

    public synchronized Issue takeIssue(String issueId, String callerEmail) {
        Issue issue = issues.get(issueId);
        if (issue == null) throw new IllegalArgumentException("Issue not found");
        if (issue.getType() != Issue.Type.BUG) throw new IllegalArgumentException("Only bugs can be taken");
        if (issue.getAssigneeEmail() != null) throw new IllegalStateException("Issue already assigned to another user");
        issue.setAssigneeEmail(callerEmail);
        if (issue.getStatus() == Issue.Status.TODO) issue.setStatus(Issue.Status.ONGOING);
        return issue;
    }


    public Map<String, Object> dashboardStats() {
        long open = issues.values().stream().filter(i -> i.getStatus() == Issue.Status.TODO).count();
        long ongoing = issues.values().stream().filter(i -> i.getStatus() == Issue.Status.ONGOING).count();
        long resolved = issues.values().stream().filter(i -> i.getStatus() == Issue.Status.RESOLVED).count();

        DoubleSummaryStatistics overall = issues.values().stream()
                .filter(i -> i.getStatus() == Issue.Status.RESOLVED)
                .mapToDouble(Issue::resolutionHours)
                .summaryStatistics();

        List<Map<String, Object>> perUser = new ArrayList<>();
        for (User u : assignableUsers()) {
            List<Issue> assigned = issues.values().stream()
                    .filter(i -> u.getEmail().equalsIgnoreCase(i.getAssigneeEmail()))
                    .collect(Collectors.toList());
            DoubleSummaryStatistics userStats = assigned.stream()
                    .filter(i -> i.getStatus() == Issue.Status.RESOLVED)
                    .mapToDouble(Issue::resolutionHours)
                    .summaryStatistics();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("email", u.getEmail());
            row.put("name", u.getName());
            row.put("assignedCount", (long) assigned.size());
            row.put("openCount", currentWorkload(u.getEmail()));
            row.put("avgResolutionHours", userStats.getCount() == 0 ? null : round2(userStats.getAverage()));
            perUser.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("openCount", open);
        result.put("ongoingCount", ongoing);
        result.put("resolvedCount", resolved);
        result.put("totalCount", (long) issues.size());
        result.put("avgResolutionHoursOverall", overall.getCount() == 0 ? null : round2(overall.getAverage()));
        result.put("perUser", perUser);
        result.put("perTeam", perTeamStats());
        return result;
    }

    private List<Map<String, Object>> perTeamStats() {
        List<Map<String, Object>> perTeam = new ArrayList<>();
        for (User.Team team : User.Team.values()) {
            List<User> members = assignableUsers().stream()
                    .filter(u -> u.getTeam() == team)
                    .collect(Collectors.toList());
            if (members.isEmpty()) continue;

            Set<String> memberEmails = members.stream().map(u -> u.getEmail().toLowerCase()).collect(Collectors.toSet());
            List<Issue> assigned = issues.values().stream()
                    .filter(i -> i.getAssigneeEmail() != null && memberEmails.contains(i.getAssigneeEmail().toLowerCase()))
                    .collect(Collectors.toList());
            long openNow = assigned.stream().filter(i -> i.getStatus() != Issue.Status.RESOLVED).count();
            DoubleSummaryStatistics teamStats = assigned.stream()
                    .filter(i -> i.getStatus() == Issue.Status.RESOLVED)
                    .mapToDouble(Issue::resolutionHours)
                    .summaryStatistics();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("team", team.name());
            row.put("memberCount", (long) members.size());
            row.put("assignedCount", (long) assigned.size());
            row.put("openCount", openNow);
            row.put("avgResolutionHours", teamStats.getCount() == 0 ? null : round2(teamStats.getAverage()));
            perTeam.add(row);
        }
        return perTeam;
    }


    public Map<String, Object> monthlyReport(int year, int month) {
        Calendar start = Calendar.getInstance();
        start.clear();
        start.set(year, month - 1, 1, 0, 0, 0);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);
        long startMs = start.getTimeInMillis();
        long endMs = end.getTimeInMillis();

        List<Issue> opened = issues.values().stream()
                .filter(i -> i.getCreatedAt() >= startMs && i.getCreatedAt() < endMs)
                .collect(Collectors.toList());
        List<Issue> resolved = issues.values().stream()
                .filter(i -> i.getResolvedAt() != null && i.getResolvedAt() >= startMs && i.getResolvedAt() < endMs)
                .collect(Collectors.toList());

        DoubleSummaryStatistics stats = resolved.stream().mapToDouble(Issue::resolutionHours).summaryStatistics();

        Map<String, Long> openedPerUser = opened.stream()
                .collect(Collectors.groupingBy(Issue::getReporterEmail, Collectors.counting()));
        Map<String, Long> resolvedPerUser = resolved.stream()
                .filter(i -> i.getAssigneeEmail() != null)
                .collect(Collectors.groupingBy(Issue::getAssigneeEmail, Collectors.counting()));

        Set<String> emails = new TreeSet<>();
        emails.addAll(openedPerUser.keySet());
        emails.addAll(resolvedPerUser.keySet());

        List<Map<String, Object>> perUser = new ArrayList<>();
        for (String email : emails) {
            List<Issue> userResolved = resolved.stream()
                    .filter(i -> email.equalsIgnoreCase(i.getAssigneeEmail()))
                    .collect(Collectors.toList());
            DoubleSummaryStatistics us = userResolved.stream().mapToDouble(Issue::resolutionHours).summaryStatistics();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("email", email);
            row.put("opened", openedPerUser.getOrDefault(email, 0L));
            row.put("resolved", resolvedPerUser.getOrDefault(email, 0L));
            row.put("avgResolutionHours", us.getCount() == 0 ? null : round2(us.getAverage()));
            perUser.add(row);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("opened", (long) opened.size());
        result.put("resolved", (long) resolved.size());
        result.put("avgResolutionHours", stats.getCount() == 0 ? null : round2(stats.getAverage()));
        result.put("perUser", perUser);
        result.put("perTeam", perTeamMonthly(opened, resolved));
        return result;
    }

    private List<Map<String, Object>> perTeamMonthly(List<Issue> opened, List<Issue> resolved) {
        List<Map<String, Object>> perTeam = new ArrayList<>();
        for (User.Team team : User.Team.values()) {
            Set<String> memberEmails = allUsers().stream()
                    .filter(u -> u.getTeam() == team)
                    .map(u -> u.getEmail().toLowerCase())
                    .collect(Collectors.toSet());
            if (memberEmails.isEmpty()) continue;

            long openedCount = opened.stream()
                    .filter(i -> i.getReporterEmail() != null && memberEmails.contains(i.getReporterEmail().toLowerCase()))
                    .count();
            List<Issue> resolvedByTeam = resolved.stream()
                    .filter(i -> i.getAssigneeEmail() != null && memberEmails.contains(i.getAssigneeEmail().toLowerCase()))
                    .collect(Collectors.toList());
            DoubleSummaryStatistics teamStats = resolvedByTeam.stream()
                    .mapToDouble(Issue::resolutionHours)
                    .summaryStatistics();

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("team", team.name());
            row.put("opened", openedCount);
            row.put("resolved", (long) resolvedByTeam.size());
            row.put("avgResolutionHours", teamStats.getCount() == 0 ? null : round2(teamStats.getAverage()));
            perTeam.add(row);
        }
        return perTeam;
    }

    private double round2(double v) { return Math.round(v * 100.0) / 100.0; }

    // ==================== SESSIONI (punto 1) ====================

    public String createSession(String email) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, email.toLowerCase());
        return token;
    }

    public User userFromToken(String token) {
        if (token == null) return null;
        String email = sessions.get(token);
        return email == null ? null : findUserByEmail(email);
    }
}
