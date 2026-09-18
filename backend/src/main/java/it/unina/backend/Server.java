package it.unina.backend;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import json.Json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Server {

    private final Db db = new Db();
    private final int port;

    public Server(int port) { this.port = port; }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new Server(port).start();
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/login", this::handleLogin);
        server.createContext("/api/users", this::handleUsers);
        server.createContext("/api/issues", this::handleIssues);
        server.createContext("/api/projects", this::handleProjects);
        server.createContext("/api/my-project", this::handleMyProject);
        server.createContext("/api/admin/dashboard", this::handleDashboard);
        server.createContext("/api/admin/reports/monthly", this::handleMonthlyReport);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("=================================================");
        System.out.println(" BugBoard26 back-end avviato su http://localhost:" + port);
        System.out.println(" Utente amministratore di default:");
        System.out.println("   email:    admin");
        System.out.println("   password: ciao");
        System.out.println("=================================================");
    }


    private String body(HttpExchange ex) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = ex.getRequestBody();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
        return bos.toString(StandardCharsets.UTF_8);
    }

    private void sendJson(HttpExchange ex, int status, Object payload) throws IOException {
        byte[] bytes = Json.write(payload).getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        // CORS permissivo: utile se in futuro il front-end viene sostituito con una SPA web
        // (Sezione 3.2: "il front-end deve poter essere sostituito [...] senza modifiche sul back-end").
        ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void sendError(HttpExchange ex, int status, String message) throws IOException {
        sendJson(ex, status, Json.obj("error", message));
    }

    private Map<String, String> queryParams(HttpExchange ex) {
        Map<String, String> map = new HashMap<>();
        String q = ex.getRequestURI().getRawQuery();
        if (q == null || q.isEmpty()) return map;
        for (String pair : q.split("&")) {
            int eq = pair.indexOf('=');
            if (eq < 0) continue;
            try {
                String key = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
                String val = URLDecoder.decode(pair.substring(eq + 1), "UTF-8");
                if (!val.isEmpty()) map.put(key, val);
            } catch (UnsupportedEncodingException ignored) {
                // UTF-8 e' sempre supportata
            }
        }
        return map;
    }

    private User authenticate(HttpExchange ex) {
        String header = ex.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) return null;
        return db.userFromToken(header.substring("Bearer ".length()).trim());
    }


    private void handleLogin(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { sendError(ex, 405, "Method not allowed"); return; }
        try {
            Map<String, Object> req = Json.parseObject(body(ex));
            String email = (String) req.get("email");
            String password = (String) req.get("password");
            User u = db.findUserByEmail(email);
            if (u == null || !u.checkPassword(password)) {
                sendError(ex, 401, "Invalid credentials");
                return;
            }
            String token = db.createSession(u.getEmail());
            Map<String, Object> resp = new LinkedHashMap<>(u.toPublicMap());
            resp.put("token", token);
            sendJson(ex, 200, resp);
        } catch (RuntimeException e) {
            sendError(ex, 400, "Invalid request: " + e.getMessage());
        }
    }

    private void handleUsers(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }

        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            // Elenco leggibile da qualunque utente autenticato (serve per assegnazioni ed etichette)
            List<Map<String, Object>> list = db.allUsers().stream()
                    .map(User::toPublicMap).collect(Collectors.toList());
            sendJson(ex, 200, list);
            return;
        }

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            // Punto 1: solo un amministratore puo' creare nuove utenze
            if (caller.getRole() != User.Role.ADMIN) { sendError(ex, 403, "Administrator permissions required"); return; }
            try {
                Map<String, Object> req = Json.parseObject(body(ex));
                String email = (String) req.get("email");
                String password = (String) req.get("password");
                String name = (String) req.get("name");
                String roleStr = req.get("role") == null ? "DEV" : (String) req.get("role");
                User.Role role = User.Role.valueOf(roleStr.toUpperCase());
                User.Team team = req.get("team") == null ? null : User.Team.valueOf(((String) req.get("team")).toUpperCase());
                User created = db.createUser(email, password, name, role, team);
                sendJson(ex, 201, created.toPublicMap());
            } catch (IllegalArgumentException e) {
                sendError(ex, 400, e.getMessage());
            }
            return;
        }

        sendError(ex, 405, "Method not allowed");
    }

    private void handleIssues(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }

        String path = ex.getRequestURI().getPath();
        String[] parts = path.split("/"); // "", "api", "issues", [id], [azione]

        if (parts.length == 3) {
            if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
                // Punto 3: vista riepilogativa con filtro/ordinamento (tipo, stato, priorita', data creazione)
                Map<String, String> q = queryParams(ex);
                try {
                    Issue.Type type = q.containsKey("type") ? Issue.Type.valueOf(q.get("type")) : null;
                    Issue.Status status = q.containsKey("status") ? Issue.Status.valueOf(q.get("status")) : null;
                    Issue.Priority priority = q.containsKey("priority") ? Issue.Priority.valueOf(q.get("priority")) : null;
                    String createdAt = q.get("createdAt");
                    String sort = q.get("sort");
                    List<Map<String, Object>> list = db.queryIssues(type, status, priority, createdAt, sort).stream()
                            .map(Issue::toMap).collect(Collectors.toList());
                    sendJson(ex, 200, list);
                } catch (IllegalArgumentException e) {
                    sendError(ex, 400, "Invalid filter parameter");
                }
                return;
            }
            if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
                // Punto 2: tutti gli utenti autenticati possono segnalare una issue
                try {
                    Map<String, Object> req = Json.parseObject(body(ex));
                    String title = (String) req.get("title");
                    String description = (String) req.get("description");
                    if (req.get("type") == null) throw new IllegalArgumentException("Issue type is required");
                    Issue.Type type = Issue.Type.valueOf(((String) req.get("type")).toUpperCase());
                    Issue.Priority priority = req.get("priority") == null ? Issue.Priority.NONE
                            : Issue.Priority.valueOf(((String) req.get("priority")).toUpperCase());

                    Issue issue = db.createIssue(title, description, type, priority, caller.getEmail());

                    // Punto 10: etichette gia' in fase di creazione, se fornite
                    Object labels = req.get("labels");
                    if (labels instanceof List) {
                        for (Object l : (List<?>) labels) issue.addLabel(String.valueOf(l));
                    }
                    // Punto 2: allegato immagine opzionale (gia' codificato in base64 dal client)
                    Object image = req.get("imageBase64");
                    if (image instanceof String && !((String) image).isEmpty()) {
                        issue.setImageBase64((String) image);
                    }
                    sendJson(ex, 201, issue.toMap());
                } catch (IllegalArgumentException e) {
                    sendError(ex, 400, e.getMessage());
                }
                return;
            }
            sendError(ex, 405, "Method not allowed");
            return;
        }

        if (parts.length == 4 && "suggestions".equals(parts[3])) {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { sendError(ex, 405, "Method not allowed"); return; }
            // Punto 14: verifica se il chiamante ha il carico di lavoro minimo tra tutti
            // gli utenti; solo in tal caso vengono restituite le issue disponibili.
            boolean eligible = db.isEligibleForSuggestion(caller.getEmail());
            Map<String, Object> resp = new LinkedHashMap<>();
            resp.put("eligible", eligible);
            resp.put("myWorkload", db.currentWorkload(caller.getEmail()));
            resp.put("availableIssues", eligible
                    ? db.availableBugs().stream().map(Issue::toMap).collect(Collectors.toList())
                    : List.of());
            sendJson(ex, 200, resp);
            return;
        }

        if (parts.length >= 4) {
            String id = parts[3];
            Issue issue = db.findIssue(id);
            if (issue == null) { sendError(ex, 404, "Issue not found"); return; }

            if (parts.length == 4) {
                if ("GET".equalsIgnoreCase(ex.getRequestMethod())) { sendJson(ex, 200, issue.toMap()); return; }
                sendError(ex, 405, "Method not allowed");
                return;
            }

            String action = parts[4];

            if ("status".equals(action) && "PUT".equalsIgnoreCase(ex.getRequestMethod())) {
                // Coerente col punto 6 della traccia: puo' cambiare stato l'assegnatario o un admin
                if (caller.getRole() != User.Role.ADMIN && !caller.getEmail().equalsIgnoreCase(issue.getAssigneeEmail())) {
                    sendError(ex, 403, "Only the assignee or an administrator can change the status");
                    return;
                }
                try {
                    Map<String, Object> req = Json.parseObject(body(ex));
                    Issue.Status s = Issue.Status.valueOf(((String) req.get("status")).toUpperCase());
                    issue.setStatus(s);
                    sendJson(ex, 200, issue.toMap());
                } catch (IllegalArgumentException e) {
                    sendError(ex, 400, "Invalid status");
                }
                return;
            }

            if ("take".equals(action) && "PUT".equalsIgnoreCase(ex.getRequestMethod())) {
                // Punto 14 (rivisto): e' l'utente stesso a prendersi in carico il bug,
                // non l'amministratore. Consentito solo agli utenti di ruolo DEV.
                if (caller.getRole() != User.Role.DEV) {
                    sendError(ex, 403, "Only DEV users can take a bug");
                    return;
                }
                try {
                    Issue updated = db.takeIssue(id, caller.getEmail());
                    sendJson(ex, 200, updated.toMap());
                } catch (IllegalStateException e) {
                    sendError(ex, 409, e.getMessage());
                } catch (IllegalArgumentException e) {
                    sendError(ex, 400, e.getMessage());
                }
                return;
            }

            if ("labels".equals(action) && "POST".equalsIgnoreCase(ex.getRequestMethod())) {
                // Punto 10: numero variabile di etichette personalizzabili sui bug
                try {
                    Map<String, Object> req = Json.parseObject(body(ex));
                    String label = (String) req.get("label");
                    if (label == null || label.trim().isEmpty()) { sendError(ex, 400, "Invalid label"); return; }
                    issue.addLabel(label);
                    sendJson(ex, 200, issue.toMap());
                } catch (RuntimeException e) {
                    sendError(ex, 400, "Invalid request");
                }
                return;
            }

            sendError(ex, 404, "Endpoint not found");
            return;
        }

        sendError(ex, 404, "Endpoint not found");
    }

    private void handleProjects(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }

        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            // Elenco leggibile da qualunque utente autenticato
            List<Map<String, Object>> list = db.listProjects().stream()
                    .map(Project::toMap).collect(Collectors.toList());
            sendJson(ex, 200, list);
            return;
        }

        if ("POST".equalsIgnoreCase(ex.getRequestMethod())) {
            // Solo un amministratore puo' creare un nuovo progetto
            if (caller.getRole() != User.Role.ADMIN) { sendError(ex, 403, "Administrator permissions required"); return; }
            try {
                Map<String, Object> req = Json.parseObject(body(ex));
                String name = (String) req.get("name");
                Object teamsObj = req.get("teams");
                List<String> teams = new ArrayList<>();
                if (teamsObj instanceof List) {
                    for (Object t : (List<?>) teamsObj) {
                        teams.add(String.valueOf(t));
                    }
                }
                Project created = db.createProject(name, teams);
                sendJson(ex, 201, created.toMap());
            } catch (IllegalArgumentException e) {
                sendError(ex, 400, e.getMessage());
            }
            return;
        }

        sendError(ex, 405, "Method not allowed");
    }

    /**
     * Sezione "Choose Project" (utenti DEV): GET restituisce i progetti a cui
     * l'utente partecipa, PUT ne aggiunge uno (o aggiorna il team se gia' scelto),
     * DELETE rimuove la partecipazione a un progetto.
     */
    private void handleMyProject(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }

        if ("GET".equalsIgnoreCase(ex.getRequestMethod())) {
            List<Map<String, Object>> list = new ArrayList<>();
            for (Map<String, String> choice : caller.getProjectChoices()) {
                Project p = db.findProject(choice.get("projectId"));
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("projectId", choice.get("projectId"));
                row.put("projectName", p == null ? null : p.getName());
                row.put("team", choice.get("team"));
                list.add(row);
            }
            sendJson(ex, 200, list);
            return;
        }

        if ("PUT".equalsIgnoreCase(ex.getRequestMethod())) {
            // La scelta del progetto e' riservata agli utenti DEV: gli amministratori
            // gestiscono i progetti dalla sezione "Create Project".
            if (caller.getRole() != User.Role.DEV) {
                sendError(ex, 403, "Only DEV users can choose a project");
                return;
            }
            try {
                Map<String, Object> req = Json.parseObject(body(ex));
                String projectId = (String) req.get("projectId");
                String team = (String) req.get("team");
                User updated = db.chooseProject(caller.getEmail(), projectId, team);
                sendJson(ex, 200, updated.toPublicMap());
            } catch (IllegalArgumentException e) {
                sendError(ex, 400, e.getMessage());
            }
            return;
        }

        if ("DELETE".equalsIgnoreCase(ex.getRequestMethod())) {
            if (caller.getRole() != User.Role.DEV) {
                sendError(ex, 403, "Only DEV users can leave a project");
                return;
            }
            try {
                Map<String, String> q = queryParams(ex);
                String projectId = q.get("projectId");
                User updated = db.leaveProject(caller.getEmail(), projectId);
                sendJson(ex, 200, updated.toPublicMap());
            } catch (IllegalArgumentException e) {
                sendError(ex, 400, e.getMessage());
            }
            return;
        }

        sendError(ex, 405, "Method not allowed");
    }

    private void handleDashboard(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }
        if (caller.getRole() != User.Role.ADMIN) { sendError(ex, 403, "Administrator permissions required"); return; }
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { sendError(ex, 405, "Method not allowed"); return; }
        // Punto 7: dashboard amministratore con informazioni aggregate sui bug (anche per team)
        sendJson(ex, 200, db.dashboardStats());
    }

    private void handleMonthlyReport(HttpExchange ex) throws IOException {
        User caller = authenticate(ex);
        if (caller == null) { sendError(ex, 401, "Authentication required"); return; }
        if (caller.getRole() != User.Role.ADMIN) { sendError(ex, 403, "Administrator permissions required"); return; }
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { sendError(ex, 405, "Method not allowed"); return; }
        try {
            Map<String, String> q = queryParams(ex);
            Calendar now = Calendar.getInstance();
            int year = q.containsKey("year") ? Integer.parseInt(q.get("year")) : now.get(Calendar.YEAR);
            int month = q.containsKey("month") ? Integer.parseInt(q.get("month")) : (now.get(Calendar.MONTH) + 1);
            if (month < 1 || month > 12) { sendError(ex, 400, "Invalid month"); return; }
            // Punto 17: report mensile sull'attivita' del team, aggregato, per utente e per team
            sendJson(ex, 200, db.monthlyReport(year, month));
        } catch (NumberFormatException e) {
            sendError(ex, 400, "Invalid year/month parameters");
        }
    }
}
