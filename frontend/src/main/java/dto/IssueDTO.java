package dto;

import java.util.ArrayList;
import java.util.List;

public class IssueDTO {

    private String id;
    private String title;
    private String description;
    private IssueType type;
    private IssuePriority priority;
    private IssueStatus status;
    private String assigneeEmail;
    private List<String> labels = new ArrayList<>();

    public IssueDTO() { }

    public IssueDTO(String id, String title, String description, IssueType type, IssuePriority priority,
                     IssueStatus status, String assigneeEmail, List<String> labels) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.assigneeEmail = assigneeEmail;
        this.labels = labels != null ? labels : new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public IssueType getType() { return type; }
    public void setType(IssueType type) { this.type = type; }

    public IssuePriority getPriority() { return priority; }
    public void setPriority(IssuePriority priority) { this.priority = priority; }

    public IssueStatus getStatus() { return status; }
    public void setStatus(IssueStatus status) { this.status = status; }

    public String getAssigneeEmail() { return assigneeEmail; }
    public void setAssigneeEmail(String assigneeEmail) { this.assigneeEmail = assigneeEmail; }

    public boolean isAssigned() { return assigneeEmail != null; }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels != null ? labels : new ArrayList<>(); }
}
