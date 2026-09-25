package dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class IssueDTO {

    @Setter private String id;
    @Setter private String title;
    @Setter private String description;
    @Setter private IssueType type;
    @Setter private IssuePriority priority;
    @Setter private IssueStatus status;
    @Setter private String assigneeEmail;
    private List<String> labels = new ArrayList<>();

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

    public boolean isAssigned() { return assigneeEmail != null; }

    public void setLabels(List<String> labels) { this.labels = labels != null ? labels : new ArrayList<>(); }
}
