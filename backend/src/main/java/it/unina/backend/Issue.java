package it.unina.backend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Issue {

    public enum Type { BUG, QUESTION, DOCUMENTATION, FEATURE }
    public enum Priority { NONE, LOW, MEDIUM, HIGH, CRITICAL }
    public enum Status { TODO, ONGOING, RESOLVED }

    private final String id;
    private final String title;
    private final String description;
    private final Type type;
    private final Priority priority;              // punto 2: mai null, default NONE se non specificata
    private Status status = Status.TODO;          // punto 2: le issue nascono in stato "todo"
    private final String reporterEmail;
    private String assigneeEmail;                 // punto 4/14: nullable, valorizzato all'assegnazione
    private final List<String> labels = new ArrayList<>(); // punto 10: etichette personalizzabili
    private String imageBase64;                   // punto 2: allegato immagine opzionale
    private final long createdAt;
    private Long resolvedAt;                       // usato per calcolare i tempi di risoluzione (punti 7, 17)

    public Issue(String id, String title, String description, Type type, Priority priority,
                 String reporterEmail, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority == null ? Priority.NONE : priority;
        this.reporterEmail = reporterEmail;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Type getType() { return type; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public String getReporterEmail() { return reporterEmail; }
    public String getAssigneeEmail() { return assigneeEmail; }
    public List<String> getLabels() { return labels; }
    public String getImageBase64() { return imageBase64; }
    public long getCreatedAt() { return createdAt; }
    public Long getResolvedAt() { return resolvedAt; }

    public void setAssigneeEmail(String assigneeEmail) { this.assigneeEmail = assigneeEmail; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public void setStatus(Status status) {
        this.status = status;
        if (status == Status.RESOLVED) {
            if (resolvedAt == null) resolvedAt = System.currentTimeMillis();
        } else {
            resolvedAt = null;
        }
    }

    void forceResolvedAt(Long ts) { this.resolvedAt = ts; }

    public void addLabel(String label) {
        if (label == null) return;
        String clean = label.trim();
        if (!clean.isEmpty() && labels.stream().noneMatch(l -> l.equalsIgnoreCase(clean))) {
            labels.add(clean);
        }
    }

    public double resolutionHours() {
        if (resolvedAt == null) return -1;
        return (resolvedAt - createdAt) / 3600000.0;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("title", title);
        m.put("description", description);
        m.put("type", type.name());
        m.put("priority", priority.name());
        m.put("status", status.name());
        m.put("reporterEmail", reporterEmail);
        m.put("assigneeEmail", assigneeEmail);
        m.put("labels", labels);
        m.put("hasImage", imageBase64 != null);
        m.put("createdAt", createdAt);
        m.put("resolvedAt", resolvedAt);
        return m;
    }
}
