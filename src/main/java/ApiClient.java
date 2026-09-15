import json.Json;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Client REST per il back-end di BugBoard26.
 *
 * Il front-end comunica col back-end esclusivamente attraverso queste chiamate
 * di rete (Sezione 3.2 della traccia): nessuna classe del front-end accede
 * direttamente a Db/Issue/User del back-end.
 */
public class ApiClient {

    // Indirizzo del back-end: puo' essere sovrascritto con -Dbugboard.api=http://host:porta
    private static final String BASE_URL = System.getProperty("bugboard.api", "http://localhost:8080");
    private static final HttpClient client = HttpClient.newHttpClient();

    public static class ApiException extends RuntimeException {
        public final int status;
        public ApiException(int status, String message) { super(message); this.status = status; }
    }

    private static HttpRequest.Builder request(String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder().uri(URI.create(BASE_URL + path));
        if (Session.getToken() != null) b.header("Authorization", "Bearer " + Session.getToken());
        b.header("Content-Type", "application/json; charset=utf-8");
        return b;
    }

    private static BodyPublisher json(Map<String, Object> body) {
        return BodyPublishers.ofString(Json.write(body), StandardCharsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private static Object call(HttpRequest req) {
        try {
            HttpResponse<String> resp = client.send(req, BodyHandlers.ofString(StandardCharsets.UTF_8));
            String bodyStr = (resp.body() == null || resp.body().isEmpty()) ? "null" : resp.body();
            Object parsed = Json.parse(bodyStr);
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) return parsed;
            String msg = "Errore del server (HTTP " + resp.statusCode() + ")";
            if (parsed instanceof Map && ((Map<String, Object>) parsed).get("error") != null) {
                msg = String.valueOf(((Map<String, Object>) parsed).get("error"));
            }
            throw new ApiException(resp.statusCode(), msg);
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Could not reach the BugBoard26 server (" + BASE_URL + "). "
                    + "Please make sure the back-end is running.");
        }
    }

    // ==================== Punto 1: autenticazione e utenti ====================

    @SuppressWarnings("unchecked")
    public static Map<String, Object> login(String email, String password) {
        HttpRequest req = request("/api/login")
                .POST(json(Json.obj("email", email, "password", password))).build();
        return (Map<String, Object>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listUsers() {
        HttpRequest req = request("/api/users").GET().build();
        return (List<Map<String, Object>>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createUser(String email, String password, String name, String role, String team) {
        HttpRequest req = request("/api/users")
                .POST(json(Json.obj("email", email, "password", password, "name", name, "role", role, "team", team))).build();
        return (Map<String, Object>) call(req);
    }

    // ==================== Punti 2 e 3: issue ====================

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listIssues(String type, String status, String priority, String createdAt, String sort) {
        StringBuilder q = new StringBuilder("/api/issues?");
        if (type != null) q.append("type=").append(type).append("&");
        if (status != null) q.append("status=").append(status).append("&");
        if (priority != null) q.append("priority=").append(priority).append("&");
        if (createdAt != null) q.append("createdAt=").append(createdAt).append("&");
        if (sort != null) q.append("sort=").append(sort).append("&");
        HttpRequest req = request(q.toString()).GET().build();
        return (List<Map<String, Object>>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createIssue(String title, String description, String type, String priority,
                                                    List<String> labels, String imageBase64) {
        Map<String, Object> body = Json.obj(
                "title", title, "description", description, "type", type, "priority", priority,
                "labels", labels, "imageBase64", imageBase64);
        HttpRequest req = request("/api/issues").POST(json(body)).build();
        return (Map<String, Object>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> updateStatus(String issueId, String status) {
        HttpRequest req = request("/api/issues/" + issueId + "/status")
                .method("PUT", json(Json.obj("status", status))).build();
        return (Map<String, Object>) call(req);
    }

    /** Punto 14 (rivisto): l'utente prende in carico da solo un bug non ancora assegnato. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> takeIssue(String issueId) {
        HttpRequest req = request("/api/issues/" + issueId + "/take")
                .method("PUT", BodyPublishers.noBody()).build();
        return (Map<String, Object>) call(req);
    }

    /**
     * Punto 14: verifica se l'utente loggato ha il carico di lavoro minimo tra tutti
     * gli utenti; se si', restituisce anche l'elenco delle issue disponibili.
     * Risposta: { eligible: bool, myWorkload: n, availableIssues: [...] }
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> mySuggestions() {
        HttpRequest req = request("/api/issues/suggestions").GET().build();
        return (Map<String, Object>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> addLabel(String issueId, String label) {
        HttpRequest req = request("/api/issues/" + issueId + "/labels")
                .POST(json(Json.obj("label", label))).build();
        return (Map<String, Object>) call(req);
    }

    // ==================== Punti 7 e 17: dashboard e report ====================

    @SuppressWarnings("unchecked")
    public static Map<String, Object> dashboard() {
        HttpRequest req = request("/api/admin/dashboard").GET().build();
        return (Map<String, Object>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> monthlyReport(int year, int month) {
        HttpRequest req = request("/api/admin/reports/monthly?year=" + year + "&month=" + month).GET().build();
        return (Map<String, Object>) call(req);
    }

    // ==================== Create Project (admin) ====================

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> listProjects() {
        HttpRequest req = request("/api/projects").GET().build();
        return (List<Map<String, Object>>) call(req);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> createProject(String name, List<String> teams) {
        HttpRequest req = request("/api/projects")
                .POST(json(Json.obj("name", name, "teams", teams))).build();
        return (Map<String, Object>) call(req);
    }

    // ==================== Choose Project (DEV) ====================

    /** Progetti a cui l'utente loggato partecipa, con il relativo team. */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> myProjects() {
        HttpRequest req = request("/api/my-project").GET().build();
        return (List<Map<String, Object>>) call(req);
    }

    /** Aggiunge la partecipazione a un progetto (o aggiorna il team se gia' scelto). */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> chooseProject(String projectId, String team) {
        HttpRequest req = request("/api/my-project")
                .method("PUT", json(Json.obj("projectId", projectId, "team", team))).build();
        return (Map<String, Object>) call(req);
    }

    /** Rimuove la partecipazione dell'utente a un progetto. */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> leaveProject(String projectId) {
        HttpRequest req = request("/api/my-project?projectId=" + projectId)
                .method("DELETE", BodyPublishers.noBody()).build();
        return (Map<String, Object>) call(req);
    }
}
